package ruc.summer.spider.protocol.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhinang.protocol.http.HttpClientAgent;
import org.zhinang.protocol.http.UrlResponse;
import org.zhinang.protocol.metadata.Metadata;
import org.zhinang.util.BlankUtils;
import ruc.summer.conf.ConfFactory;
import ruc.summer.spider.CrawlDatum;
import ruc.summer.spider.protocol.*;

public class HttpProtocol implements Protocol {
    private static Logger LOG = LoggerFactory.getLogger(HttpProtocol.class);
    private static HttpClientAgent agent = new HttpClientAgent(ConfFactory.getConf());

    /**
     * 设置代理，如果host为null或者port为0，则移除原有的代理
     * @param host
     * @param port
     */
    public void setProxy(String host, int port) {
        if(port==0 || BlankUtils.isBlank(host)) {
            agent.removeProxy();
        } else {
            agent.setProxyHost(host, port);
        }
    }

    @Override
    public Metadata getHeaders(String url, String refer) throws ProtocolException {
        try{
            return agent.getHeaders(url, refer, false);
        }catch (Exception e) {
            throw new ProtocolException(e);
        }
    }
    
    public ProtocolOutput getProtocolOutput(String url) throws ProtocolException {
        return getProtocolOutput(url, null);
    }

    @Override
    public ProtocolOutput getProtocolOutput(CrawlDatum crawlDatum) throws ProtocolException {
        String url = crawlDatum.getUrl();
        String referer = crawlDatum.getParentUrl();
        return getProtocolOutput(url, referer);
    }
    
    public ProtocolOutput getProtocolOutput(String url, String referer) throws ProtocolException {       
        try {            
            UrlResponse response = agent.execute(url, referer);
            //FIXME why?天涯http://main.tianya.cn/bbs/hot/1.shtml在第一抓取时，返回的Header中带有ETag标记，内容为js代码，重新抓取即可获取正常内容，
            //需要进一步从根本上解决问题
            if("INET".equals(response.getHeader("ETag"))){
                response = agent.execute(url, referer);
            }
            Content c = new Content(url, response.getFollowedUrl(), response.getContent(), response.getContentType(), response.getEncoding(),   response.getHeaders());
            ProtocolStatus status = new ProtocolStatus(response.getCode());
            ProtocolOutput output = new ProtocolOutput(c, status);
            
            return output;
        } catch (Exception e) {
            LOG.error("URIError:" + url + ", refer:" + referer + "\n" + e.getMessage());
            throw new ProtocolException(e);
        }
    }

}
