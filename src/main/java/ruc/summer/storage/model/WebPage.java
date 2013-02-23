package ruc.summer.storage.model;

import org.bson.types.ObjectId;
import ruc.summer.util.Signature;

import java.util.Date;
import java.util.Map;

/**
 * 存储中心中保存的网页对象。
 * 每个网页由URL的MD5+内容的签名联合作为一个主键。
 *
 * User: xiatian
 * Date: 2/23/13 4:17 PM
 */
public class WebPage extends MongoObject {
    private String urlMd5;
    private String url;
    private String signature;
    private Date keepTime;
    private String title;

    /**
     * 网页的类型，如text/html, 或text/javascript，或者image/gif等
     */
    private String contentType;

    /**
     * 网页的文本内容，比如滤掉HTML标记后形成的文本
     */
    private String text;

    private byte[] content;

    /**
     * 网页的Metadata，典型的内容是HTTP Header信息
     */
    private Map<String, String> metadata;

    /** 网页的内容大小，方便统计加入 */
    private int size;

    public WebPage(){}

    public WebPage(String url, Date keepTime, String title, String contentType, String text, byte[] content, Map<String, String> metadata, String agent) {
        this.url = url;
        this.urlMd5 = Signature.getMd5String(url);
        this.keepTime = keepTime;
        this.title = title;
        this.contentType = contentType;
        this.text = text;
        this.content = content;
        this.signature = Signature.getMd5String(content);
        this.size = this.content.length;
        this.metadata = metadata;
        this.agent = agent;
    }

    /**
     * 网页的抓取Agent，代表网页的来源，如Nutch、MySpider等，方便追踪查看
     */
    private String agent;

    public String getUrlMd5() {
        return urlMd5;
    }

    public void setUrlMd5(String urlMd5) {
        this.urlMd5 = urlMd5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Date getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Date keepTime) {
        this.keepTime = keepTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @Override
    public String toString() {
        return "WebPage{" +
                "id=" + id +
                ", urlMd5='" + urlMd5 + '\'' +
                ", url='" + url + '\'' +
                ", signature='" + signature + '\'' +
                ", keepTime=" + keepTime +
                ", title='" + title + '\'' +
                ", contentType='" + contentType + '\'' +
                ", text='" + text + '\'' +
                ", content=" + content +
                ", metadata=" + metadata +
                ", size=" + size +
                ", agent='" + agent + '\'' +
                '}';
    }
}
