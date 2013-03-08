package ruc.summer.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zhinang.util.LinkUtils;
import org.zhinang.util.io.SerialUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.dao.helper.RedisClient;
import ruc.summer.util.Signature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class handles FetchItems which come from the same host ID (be it a
 * proto/hostname or proto/IP pair). It also keeps track of requests in progress
 * and elapsed time between requests.
 */
public class FetchItemQueue {
    private static final Logger LOG = LoggerFactory.getLogger(FetchItemQueue.class);

    // private static final Logger LOG =
    // LoggerFactory.getLogger(FetchItemQueue.class);

    /**
     * 爬虫在Redis中存放的正准备抓取的链接所存放的队列的主键名称， 队列自左到右按时间存放待抓取的CrawlDatum，抓取时，从左侧依次弹出抓取
     */
    private static final String Spider_ListKey_CrawlDatum = "spider:list:crawldatum";

    private static final String Spider_SetKey_CrawlDatumMd5 = RedisClient.normalizeKey("spider:set:crawldatum:md5");

    /**
     * 正在等待处理的链接集合
     */
    private static final String Spider_SetKey_WaitingLink = "spider:set:urls";

    /**
     * 正在被线程抓取的URL集合，通过该集合可以防止同一个URL被多个线程同时抓取处理
     */
    private static final String Spider_SetKey_FetchingLink = "spider:set:fetching:urls";

    /** 同一个站点连续两次爬行之间的最小时间间隔，默认为1秒 */
    private static final long Fetch_Min_Interval = 1000;

    /**
     * 左侧入队，意味着该条目会被尽快抓取
     * 
     * @param datum
     */
    public void lpush(CrawlDatum datum)  {

        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();

        String md5 = Signature.getMd5String(datum.getUrl());
        boolean addItem = false;
        if (!jedis.sismember(Spider_SetKey_CrawlDatumMd5, md5)) {
            addItem = true;

            //记录该主键到抓取队列中，以避免重复抓取
            jedis.sadd(Spider_SetKey_CrawlDatumMd5, md5);
        } else {
            //如果已经存在，则按照一定概率保留
            int random = new Random().nextInt(100);
            if(random < 3) {
                addItem = true;
            }
        }

        if(addItem) {
            String xml_string = SerialUtils.toXML(datum);
            // 加入爬行队列
            jedis.lpush(RedisClient.normalizeKey(Spider_ListKey_CrawlDatum), xml_string);
            // 加入爬行队列集合，用于快速判断一个URL是否已经在爬行队列之中
            jedis.sadd(RedisClient.normalizeKey(Spider_SetKey_WaitingLink), datum.getUrl());
        }

        pool.returnResource(jedis);
    }

    public void lpush(String homepage) {
        CrawlDatum datum = new CrawlDatum(homepage, null, 1);
        lpush(datum);
    }

    public void rpush(String homepage) {
        CrawlDatum datum = new CrawlDatum(homepage, null, 1);
        rpush(datum);
    }

    /**
     * 右侧入队
     * 
     * @param datum
     */
    public void rpush(CrawlDatum datum)  {

        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();

        String md5 = Signature.getMd5String(datum.getUrl());
        boolean addItem = false;
        if (!jedis.sismember(Spider_SetKey_CrawlDatumMd5, md5)) {
            addItem = true;

            //记录该主键到抓取队列中，以避免重复抓取
            jedis.sadd(Spider_SetKey_CrawlDatumMd5, md5);
        } else {
            //如果已经存在，则按照一定概率保留
            int random = new Random().nextInt(100);
            if(random < 3) {
                addItem = true;
            }
        }

        if(addItem) {
            String xml_string = SerialUtils.toXML(datum);
            // 加入爬行队列
            jedis.rpush(RedisClient.normalizeKey(Spider_ListKey_CrawlDatum), xml_string);

            // 加入爬行队列集合，用于快速判断一个URL是否已经在爬行队列之中
            jedis.sadd(RedisClient.normalizeKey(Spider_SetKey_WaitingLink), datum.getUrl());
        }

        pool.returnResource(jedis);
    }

    public long size()  {
        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();
        long size = jedis.llen(RedisClient.normalizeKey(Spider_ListKey_CrawlDatum));
        pool.returnResource(jedis);

        return size;
    }

    public List<CrawlDatum> getList(int start, int count) throws DaoException {
        List<String> list = RedisClient.lrange(RedisClient.normalizeKey(Spider_ListKey_CrawlDatum), start, count);

        List<CrawlDatum> datumList = new ArrayList<CrawlDatum>();
        if (list != null) {
            for (String xml_string : list) {
                datumList.add((CrawlDatum) SerialUtils.fromXML(xml_string));
            }
        }

        return datumList;
    }

    /**
     * 爬取完毕后应该执行该操作
     *
     * @param url
     */
    public void finishItem(String url) {
        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();

        // 从爬行队列集合中删除该url，表示该url已经处理完毕
        jedis.srem(RedisClient.normalizeKey(Spider_SetKey_WaitingLink), url);

        // 从正在抓取的集合中删除该条目
        jedis.srem(RedisClient.normalizeKey(Spider_SetKey_FetchingLink), url);

        jedis.srem(Spider_SetKey_CrawlDatumMd5, Signature.getMd5String(url));

        pool.returnResource(jedis);
    }

    /**
     * url是否已经在爬行集合之中
     *
     * @param url
     */
    public boolean exist(String url)  {
        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();

        boolean existed = jedis.sismember(RedisClient.normalizeKey(Spider_SetKey_WaitingLink), url);

        pool.returnResource(jedis);

        return existed;
    }

    /**
     * 从队列左端弹出一个元素，进行抓取，如果刚弹出的元素在最近时间内（设定值）被抓取过， 则把该元素加到队列的右侧尾部，继续判断下一个
     *
     */
    public CrawlDatum nextItem() {
        CrawlDatum datum = null;

        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();

        try {
            String xml_string = jedis.lpop(RedisClient.normalizeKey(Spider_ListKey_CrawlDatum));
            datum = (CrawlDatum) SerialUtils.fromXML(xml_string);

            LOG.debug("准备获取一个有效的URL");
            // 循环处理每一个元素，如果该元素所对应的url的域名的上次抓取时间太近，则循环取一下
            while (xml_string != null) {
                String domain = LinkUtils.getDomain(datum.getUrl());
                String siteKey = RedisClient.normalizeKey("lastFetchSite:" + domain);

                if (jedis.exists(siteKey)) {
                    long lastFetchTime = Long.parseLong(jedis.get(siteKey));
                    if (System.currentTimeMillis() - lastFetchTime < Fetch_Min_Interval) {
                        // 加入爬行队列
                        jedis.rpush(RedisClient.normalizeKey(Spider_ListKey_CrawlDatum), xml_string);
                        xml_string = jedis.lpop(RedisClient.normalizeKey(Spider_ListKey_CrawlDatum));
                        datum = (CrawlDatum) SerialUtils.fromXML(xml_string);
                        continue;
                    }
                }

                jedis.set(siteKey, Long.toString(System.currentTimeMillis()));
                jedis.expire(siteKey, 3600);
                break;
            }
            if (datum != null) {
                if (jedis.sismember(RedisClient.normalizeKey(Spider_SetKey_FetchingLink), datum.getUrl())) {
                    LOG.info("URL最近已经被其他线程抓取过：" + datum.getUrl());
                    // finishItem(datum.getUrl());
                    datum = null; // 返回null
                } else {
                    jedis.sadd(RedisClient.normalizeKey(Spider_SetKey_FetchingLink), datum.getUrl());
                    LOG.info("获取的URL:" + datum.getUrl());
                }
            }
        } finally {
            pool.returnResource(jedis);
        }
        return datum;
    }

    /**
     * 删除缓存数据
     */
    public void removeCache() {
        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();
        jedis.del(RedisClient.normalizeKey(Spider_SetKey_FetchingLink));
        pool.returnResource(jedis);
    }
}
