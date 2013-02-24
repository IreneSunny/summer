package ruc.summer.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhinang.conf.Configuration;
import org.zhinang.protocol.http.UrlResponse;
import org.zhinang.util.DateUtils;
import ruc.summer.conf.ConfFactory;
import ruc.summer.spider.protocol.*;
import ruc.summer.storage.Storage;

import javax.swing.text.AbstractDocument;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 修改自nutch1.0的Fetcher类，精简了http处理过程，生产者消费者模型没有变。
 * <p/>
 * A queue-based fetcher.
 * <p/>
 * <p/>
 * This fetcher uses a well-known model of one producer (a QueueFeeder) and many
 * consumers (FetcherThread-s).
 * <p/>
 * <p/>
 * QueueFeeder reads input fetchlists and populates a set of FetchItemQueue-s,
 * which hold FetchItem-s that describe the items to be fetched. There are as
 * many queues as there are unique hosts, but at any given time the total number
 * of fetch items in all queues is less than a fixed number (currently set to a
 * multiple of the number of threads).
 * <p/>
 * <p/>
 * As items are consumed from the queues, the QueueFeeder continues to add new
 * input items, so that their total count stays fixed (FetcherThread-s may also
 * add new items to the queues e.g. as a results of redirection) - until all
 * input items are exhausted, at which point the number of items in the queues
 * begins to decrease. When this number reaches 0 fetcher will finish.
 * <p/>
 * <p/>
 * This fetcher implementation handles per-host blocking itself, instead of
 * delegating this work to protocol-specific plugins. Each per-host queue
 * handles its own "politeness" settings, such as the maximum number of
 * concurrent requests and crawl delay between consecutive requests - and also a
 * list of requests in progress, and the time the last request was finished. As
 * FetcherThread-s ask for new items to be fetched, queues may return eligible
 * items or null if for "politeness" reasons this host's queue is not yet ready.
 * <p/>
 * <p/>
 * If there are still unfetched items in the queues, but none of the items are
 * ready, FetcherThread-s will spin-wait until either some items become
 * available, or a timeout is reached (at which point the Fetcher will abort,
 * assuming the task is hung).
 *
 * @author Andrzej Bialecki
 */
public class Fetcher {

    public static final Logger LOG = LoggerFactory.getLogger(Fetcher.class);

    private AtomicInteger activeThreads = new AtomicInteger(0);

    private long start = System.currentTimeMillis(); // start time of fetcher
    // run
    private AtomicLong lastRequestStart = new AtomicLong(start); // 线程最后开始抓取网页的时间。当该时间过期太久，就认为所有爬虫线程都hung了，终止fetch过程

    private AtomicLong bytes = new AtomicLong(0); // total bytes fetched
    private AtomicInteger pages = new AtomicInteger(0); // total pages fetched
    private AtomicInteger errors = new AtomicInteger(0); // total pages errored

    private FetchItemQueue queue = new FetchItemQueue();

    /**
     * This class picks items from queues and fetches the pages.
     */
    private class FetcherThread extends Thread {
        private AtomicLong retries = new AtomicLong(1);
        private int id = 0;
        // private boolean terminated = false;
        // private CrawlDatum fitCrawlDatum;
        private boolean indexing = true;

        public FetcherThread(Configuration conf, int id) {
            this.setDaemon(true); // don't hang JVM on exit
            this.id = id;
            this.setName("FetcherThread-" + id); // use an informative name
            this.indexing = conf.getBoolean("spider.fetch.indexing", true);
        }

        public void run() {
            activeThreads.incrementAndGet(); // count threads

            try {
                //存放正在抓取的链接库
                while (true) {
                    lastRequestStart.set(System.currentTimeMillis());
                    CrawlDatum fitCrawlDatum = queue.nextItem();
                    if (fitCrawlDatum == null) {
                        try {
                            long seconds = retries.get() < 10 ? retries.get() : 10;
                            Thread.currentThread();
                            //最长睡10秒
                            Thread.sleep(seconds * 1000);
                            retries.incrementAndGet();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    retries.set(1);
                    // LOG.info("fit:" + fit);
                    String reprUrl = fitCrawlDatum.getUrl().trim();

                    try {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("start fetching " + reprUrl);
                        }

                        // fetch the page
                        Protocol protocol = ProtocolFactory.getHttpProtocol();
                        ProtocolOutput protocolOutput = protocol.getProtocolOutput(fitCrawlDatum);
                        ProtocolStatus protocolStatus = protocolOutput.getStatus();
                        if (protocolStatus.getCode() == 200) {
                            Content content = protocolOutput.getContent();
                            String contentType = content.getContentType();
                            Map<String, String> headers = new HashMap<String, String>();
                            for(String name: content.getMetadata().names()) {
                                headers.put(name, content.getMetadata().get(name));
                            }
                            Storage.save(reprUrl, new Date(), headers, contentType, fitCrawlDatum.getAnchor(), "", content.getContent(), "summer");
                            if(fitCrawlDatum.getDepth()<5) {
                                if(contentType!=null && contentType.contains("html")) {
                                    Set<String> links = LinkExtractor.extractLinks(reprUrl, content.getContent(), content.getEncoding());
                                    for(String link: links) {
                                        queue.rpush(new CrawlDatum(link, reprUrl, fitCrawlDatum.getDepth()+1));
                                    }
                                }
                            }
                        }

                    } catch (Throwable t) { // unexpected exception
                        // unblock
                        logError(reprUrl, "end fetching with error " + t.getMessage());
                    }

                    queue.finishItem(reprUrl);
                }
            } catch (Throwable e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("fetcher caught:" + e.toString());
                }

            } finally {
                activeThreads.decrementAndGet(); // count threads
                LOG.info("-finishing thread " + getName() + ", activeThreads=" + activeThreads);
            }
        }

        private void logError(String url, String message) {
            if (LOG.isInfoEnabled()) {
                LOG.info("fetch of " + url + " failed with: " + message);
            }
            errors.incrementAndGet();
        }

    }

    public Fetcher() {
        LOG.info("删除爬虫队列缓存...");
        queue.removeCache();
    }

    private void updateStatus(int bytesInPage) throws IOException {
        pages.incrementAndGet();
        bytes.addAndGet(bytesInPage);
    }

    private void reportStatus() throws IOException {
        String status;
        long elapsed = (System.currentTimeMillis() - start) / 1000;
        status = activeThreads + " threads, " + pages + " pages, " + errors + " errors, "
                + Math.round(((float) pages.get() * 10) / elapsed) / 10.0 + " pages/s, "
                + Math.round(((((float) bytes.get()) * 8) / 1024) / elapsed) + " kb/s, ";
        LOG.info(status);
    }

    public void fetch(Configuration conf, int threadCount) throws Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info("Fetcher: starting");
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("Fetcher: threads: " + threadCount);
        }

        for (int i = 0; i < threadCount; i++) { // spawn threads
            new FetcherThread(conf, i + 1).start();
        }

        do { // wait for threads to exit
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }

            reportStatus();
            //LOG.info("-activeThreads=" + activeThreads);
        } while (activeThreads.get() > 0);
        LOG.info("-activeThreads=" + activeThreads);
    }

    /**
     * Run the fetcher.
     */
    public static void main(String[] args) throws Exception {

        String usage = "Usage: Fetcher <segment> [-threads n] [-jms]";

        int threads = 5;

        for (int i = 1; i < args.length; i++) { // parse command line
            if (args[i].equals("-threads")) { // found -threads option
                threads = Integer.parseInt(args[++i]);
                continue;
            }

        }

        Fetcher fetcher = new Fetcher(); // make a Fetcher
        fetcher.fetch(ConfFactory.getConf(), threads); // run the Fetcher
    }

}
