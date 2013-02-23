package ruc.summer.storage.model;

import org.bson.types.ObjectId;
import ruc.summer.util.Signature;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if(metadata==null) {
            metadata = new HashMap<String, String>();
        }
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

    private static final int CHUNK_SIZE = 6000;
    //private static Pattern metaPattern = Pattern.compile("<meta\\s+([^>]*http-equiv=\"?content-type\"?[^>]*)>", Pattern.CASE_INSENSITIVE);
    private static Pattern metaPattern = Pattern.compile("<meta\\s+([^>]*http-equiv=\"?content-type\"?[^>]*)>", Pattern.CASE_INSENSITIVE);
    private static Pattern xmlnsPattern = Pattern.compile("<html\\s+([^>]*xmlns=\"?[^>]*)>", Pattern.CASE_INSENSITIVE);
    private static Pattern langPattern = Pattern.compile( "lang=\"?([a-z][_\\-0-9a-z]*)", Pattern.CASE_INSENSITIVE);
    private static Pattern charsetPattern = Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)", Pattern.CASE_INSENSITIVE);
    private static Pattern simpleCharsetPattern = Pattern.compile("charset\\s*=\\s*\"?([a-z][_\\-0-9a-z]*)\"?", Pattern.CASE_INSENSITIVE);

    /** 处理xml 中的encoding声明 */
    private static Pattern xmlEncodingPattern = Pattern.compile("encoding\\s*=\\s*\"?([a-z][_\\-0-9a-z]*)\"?", Pattern.CASE_INSENSITIVE);

    private static String sniffCharacterEncoding(byte[] content) {
        if (content == null)
            return null;
        int length = content.length < CHUNK_SIZE ? content.length : CHUNK_SIZE;

        // We don't care about non-ASCII parts so that it's sufficient
        // to just inflate each byte to a 16-bit value by padding.
        // For instance, the sequence {0x41, 0x82, 0xb7} will be turned into
        // {U+0041, U+0082, U+00B7}.
        String str = "";
        try {
            str = new String(content, 0, length, Charset.forName("ASCII").toString());
        } catch (UnsupportedEncodingException e) {
            // code should never come here, but just in case...
            return null;
        }

        Matcher metaMatcher = metaPattern.matcher(str);
        String encoding = null;

        if (metaMatcher.find()) {
            Matcher charsetMatcher = charsetPattern.matcher(metaMatcher.group(1));
            if (charsetMatcher.find())
                encoding = new String(charsetMatcher.group(1));
        }
        if (encoding == null) {
            Matcher xmlnsMatcher = xmlnsPattern.matcher(str);
            if (xmlnsMatcher.find()) {
                Matcher langMatcher = langPattern.matcher(xmlnsMatcher.group(1));
                if (langMatcher.find())
                    encoding = new String(langMatcher.group(1));
            }
        }
        if (encoding == null) {
            Matcher simpleMatcher = simpleCharsetPattern.matcher(str);
            if(simpleMatcher.find()) {
                encoding = new String(simpleMatcher.group(1));
            }
        }

        if(encoding==null) {
            Matcher xmlEncodingMatcher = xmlEncodingPattern.matcher(str);
            if(xmlEncodingMatcher.find()) {
                encoding = new String(xmlEncodingMatcher.group(1));
            }
        }

        if (encoding == null) {
            // 如果meta中没有出现http-equiv或content-type，则直接去猜测charset=XXX
            int pos = str.toLowerCase().indexOf("charset");
            StringBuffer sb = new StringBuffer();
            if (pos > 0) {
                for (int i = (pos + 8); i < 2000; i++) {
                    char ch = str.charAt(i);
                    if (ch == ' ' || ch == '=' || ch == '\'' || ch == '\"') {
                        if (sb.length() == 0) {
                            continue;
                        } else {
                            break;
                        }
                    } else if (ch == '-' || ch == '_' || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                        sb.append(ch);
                    } else {
                        break;
                    }
                }
            }
            if (sb.length() > 0)
                encoding = sb.toString();
        }

        return encoding;
    }

    /**
     * 自动判断网页或XML的内容类型
     *
     * @return
     */
    public String getEncoding() {
        String encoding = null;

        //STEP 1: 判断HTTP HEADER中有无charset设置，如有，则启用该设置
        if(metadata!=null && metadata.get("charset")!=null) {
            encoding = metadata.get("charset");
            return encoding;
        }

        //STEP 2: 判断HEADER中有无Content-Type设置，如：Content-Type : text/html; charset=gb2312
        String contentType = getMetadata().get("Content-Type");
        if(contentType!=null ) {
            int pos = contentType.indexOf("charset=");
            if(pos>0) {
                encoding = contentType.substring(pos + 8);
                String lower = encoding.toLowerCase();
                if(lower.equals("gb2312") || lower.equals("zh_cn") ||lower.equals("zh-cn")){
                    encoding = "GBK";
                }
                if(!"none".equals(lower)) {
                    //有的网页返回的头部信息中指定了encoding=None,此时不能使用None，而应进一步判断
                    //如http://liuxue.eol.cn/liu_xue_kuai_xun_3291/20110810/t20110810_662974.shtml
                    return encoding;
                }
                return encoding;
            }
        }

        String guessEncoding = sniffCharacterEncoding(content);
        if (guessEncoding != null) {
            encoding = guessEncoding;
        }

        //默认为UTF8编码
        if(encoding==null) {
            encoding = "utf-8";
        }

        // 注意，此处gb2312编码会被自动转换为更大的字符集gbk编码，否则，可能会出现部分乱码
        if (encoding.equalsIgnoreCase("gb2312") ) {
            encoding = "GBK";
        }

        return encoding;
    }

}
