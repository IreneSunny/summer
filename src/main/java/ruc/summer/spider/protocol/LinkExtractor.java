package ruc.summer.spider.protocol;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ruc.summer.spider.protocol.normalizer.URLNormalizers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * User: xiatian
 * Date: 2/24/13 1:42 PM
 */
public class LinkExtractor {
    private Set<String> commonLinks = new HashSet<String>();
    private Set<String> cssLinks = new HashSet<String>();
    private Set<String> jsLinks = new HashSet<String>();
    private Set<String> imageLinks = new HashSet<String>();

    public void parse(String url, byte[] content, String encoding) {
        try {
            if(encoding==null) {
                System.err.println("conent encoding is null!!!!");
            }
            String lower = encoding.toLowerCase();
            System.out.println("encoding:" + encoding);
            if(lower.equals("gb2312") || lower.equals("zh_cn") ||lower.equals("zh-cn")){
                encoding = "GBK";
            }

            Document doc = Jsoup.parse(new ByteArrayInputStream(content), encoding, url);

            parse(doc);

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void parse(String url) throws IOException {
        parse(Jsoup.connect(url).get());
    }

    private void parse(Document doc) {
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href =  link.attr("abs:href");
            if(href.startsWith("mailto")) continue;

            commonLinks.add(href);
        }

        Elements media = doc.select("img[src]");
        for (Element link : media) {
            imageLinks.add(link.attr("abs:src"));
        }

        Elements cssElements = doc.select("link[href]");
        for (Element link : cssElements) {
            cssLinks.add(link.attr("abs:href"));
        }

        Elements jsElements = doc.select("script[src]");
        for (Element link : jsElements) {
            jsLinks.add(link.attr("abs:src"));
        }
    }

    public static Set<String> extractLinks(Document doc) {
        final Set<String> result = new HashSet<String>();

        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        // href ...
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (url.startsWith("mailto")) {
                continue;
            }
            try{
                if(url.endsWith("#")) {
                    url = url.substring(0, url.length() - 1);
                }
                result.add(URLNormalizers.normalize(url));
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        // img ...
        for (Element src : media) {
            String url = src.attr("abs:src");

            try{
                result.add(URLNormalizers.normalize(url));
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        // js, css, ...
        for (Element link : imports) {
            String url = link.attr("abs:href");

            try{
                result.add(URLNormalizers.normalize(url));
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Set<String> extractLinks(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        return extractLinks(doc);
    }

    public static Set<String> extractLinks(String url, byte[] content, String encoding) {
        try {
            if(encoding==null) {
                System.err.println("conent encoding is null!!!!");
            }
            String lower = encoding.toLowerCase();
            System.out.println("encoding:" + encoding);
            if(lower.equals("gb2312") || lower.equals("zh_cn") ||lower.equals("zh-cn")){
                encoding = "GBK";
            }

            Document doc = Jsoup.parse(new ByteArrayInputStream(content), encoding, url);
            return extractLinks(doc);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return new HashSet<String>();
    }


    public Set<String> getCommonLinks() {
        return commonLinks;
    }

    public Set<String> getCssLinks() {
        return cssLinks;
    }

    public Set<String> getJsLinks() {
        return jsLinks;
    }

    public Set<String> getImageLinks() {
        return imageLinks;
    }

    public final static void main(String[] args) throws Exception{
        String site = "http://www.ruc.edu.cn";

        LinkExtractor extractor = new LinkExtractor();
        extractor.parse(site);

        for(String link: extractor.getCommonLinks()) {
            System.out.println("LINK:\t" + link);
        }

        for(String link: extractor.getJsLinks()) {
            System.out.println("JS:\t" + link);
        }

        for(String link: extractor.getCssLinks()) {
            System.out.println("CSS:\t" + link);
        }

        for(String link: extractor.getImageLinks()) {
            System.out.println("IMG:\t" + link);
        }
    }
}
