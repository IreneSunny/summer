package ruc.summer.spider;

/**
 * User: xiatian
 * Date: 2/24/13 11:55 AM
 */
public class CrawlDatum {
    private final int TYPE_LINK = 0;
    private final int TYPE_CSS = 1;
    private final int TYPE_IMAGE = 2;
    private final int TYPE_JS = 3;

    private String url;
    private String parentUrl;
    private String anchor;
    private int type = 0;
    private int depth = 1;

    public CrawlDatum(){}

    public CrawlDatum(String url, String parentUrl, int depth) {
        this.url = url;
        this.parentUrl = parentUrl;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }
}
