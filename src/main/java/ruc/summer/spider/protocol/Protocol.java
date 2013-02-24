package ruc.summer.spider.protocol;

import org.zhinang.protocol.metadata.Metadata;
import ruc.summer.spider.CrawlDatum;


/** A retriever of url content. Implemented by protocol extensions. */
public interface Protocol {
    public ProtocolOutput getProtocolOutput(String url) throws ProtocolException;

    public ProtocolOutput getProtocolOutput(String url, String refer) throws ProtocolException;

    public ProtocolOutput getProtocolOutput(CrawlDatum crawlDatum) throws ProtocolException;

    /**
     * 获取头部信息，不自动跳转URL
     * @param url
     * @param refer
     * @return
     * @throws ProtocolException
     */
    public Metadata getHeaders(String url, String refer) throws ProtocolException;
}
