package ruc.summer.spider.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ruc.summer.conf.ConfFactory;
import ruc.summer.spider.CrawlDatum;
import ruc.summer.spider.protocol.http.HttpProtocol;

import java.net.MalformedURLException;
import java.net.URL;

public class ProtocolFactory {

    public static final Logger LOG = LoggerFactory.getLogger(ProtocolFactory.class);

    /**
     * Returns the appropriate {@link Protocol} implementation for a url.
     * 
     * @param urlString
     *            Url String
     * @return The appropriate {@link Protocol} implementation for a given
     *         {@link java.net.URL}.
     * @throws ProtocolException
     *             when Protocol can not be found for urlString
     */
    public static Protocol getProtocol(String urlString) throws ProtocolException {
        try {
            URL url = new URL(urlString);
            String protocolName = url.getProtocol();
            if (protocolName == null)
                throw new ProtocolException(urlString);

            if (protocolName.equalsIgnoreCase("http")) {
                return getHttpProtocol();
            }
            throw new ProtocolException("暂不支持该协议.");
        } catch (MalformedURLException e) {
            throw new ProtocolException(urlString + e.toString());
        }
    }

    public static Protocol getHttpProtocol() {
        if(httpProtocol == null) {
                httpProtocol = new HttpProtocol();
        }
        return httpProtocol;
    }

    private static Protocol httpProtocol = null;

    public static Content getContent(String url) throws ProtocolException {
        return getContent(url, null);
    }

    public static Content getContent(String url, String refer) throws ProtocolException {
        Protocol protocol = getHttpProtocol();
        ProtocolOutput output = protocol.getProtocolOutput(url, refer);
        return output.getContent();
    }
    
    public static Content getContent(CrawlDatum crawlDatum) throws ProtocolException {
        Protocol protocol = getHttpProtocol();
        ProtocolOutput output = protocol.getProtocolOutput(crawlDatum);
        return output.getContent();
    }

}
