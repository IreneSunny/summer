package ruc.summer.spider.protocol;

//JDK imports

import org.zhinang.protocol.metadata.Metadata;
import org.zhinang.util.Signature;
import ruc.summer.storage.model.MongoObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

//@Entity(value="spider.content", noClassnameStored=true)
public class Content extends MongoObject {
    private static final long serialVersionUID = -3530137432551797560L;

    //@Indexed(value=IndexDirection.ASC, name="urlIndex", unique=true, dropDups=true)
    private String url;

    /** 由于Http的Redirect特点，url与实际获取的url地址不相同，此时应采用跳转后的url作为base，计算其他子链接的绝对链接结果 */
    private String base;

    private String encoding;

    private byte[] content;

    private String contentType;

    private Metadata metadata;

    private String signature;

    public Content() {
        metadata = new Metadata();
    }

    public Content(String url, String base, byte[] content, String contentType, String encoding, Metadata metadata) {

        if (url == null)
            throw new IllegalArgumentException("null url");
        if (base == null)
            throw new IllegalArgumentException("null base(followed url)");
        if (content == null)
            throw new IllegalArgumentException("null content");
        if (metadata == null)
            throw new IllegalArgumentException("null metadata");

        this.url = url;
        this.base = base;
        this.content = content;
        this.signature = Signature.getMd5String(content);
        this.metadata = metadata;
        this.encoding = encoding;
        this.contentType = contentType;
    }

    //
    // Accessor methods
    //


    /** The url fetched. */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * The base url for relative links contained in the content. Maybe be
     * different from url if the request redirected.
     */
    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSignature(){
    	return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    /** The binary content retrieved. */
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
        this.signature = Signature.getMd5String(content);
    }

    public int getContentLength() {
        if (this.content != null) {
            return this.content.length;
        } else {
            return 0;
        }
    }

    /**
     * The media type of the retrieved content.
     *
     * @see <a href="http://www.iana.org/assignments/media-types/">
     *      http://www.iana.org/assignments/media-types/</a>
     */
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /** Other protocol-specific data. */
    public Metadata getMetadata() {
        return metadata;
    }

    /** Other protocol-specific data. */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Content)) {
            return false;
        }
        Content that = (Content) o;
        return this.url.equals(that.url) && this.base.equals(that.base) && Arrays.equals(this.getContent(), that.getContent())
                && this.contentType.equals(that.contentType) && this.metadata.equals(that.metadata);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getHtmlSource() {
        try {
            return new String(content, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String(content);
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("url: " + url + "\n");
        buffer.append("base: " + base + "\n");
        buffer.append("contentType: " + contentType + "\n");
        buffer.append("metadata: " + metadata + "\n");
        buffer.append("encoding: " + encoding + "\n");
        buffer.append("signature: " + signature.toString() + "\n");
        buffer.append("Content:\n");
        try {
            buffer.append(new String(content, encoding));
        } catch (UnsupportedEncodingException e) {
            buffer.append("unsupported encoding.");
        }

        return buffer.toString();

    }

}
