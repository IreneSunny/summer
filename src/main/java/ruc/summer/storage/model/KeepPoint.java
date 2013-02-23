package ruc.summer.storage.model;

import org.bson.types.ObjectId;
import ruc.summer.util.Signature;

import java.util.Date;

/**
 * 代表一个留痕点，两个主键：
 * (1) urlMd5+keepTime作为主键，用于查询该时间有无留痕
 * (2) urlMd5 + signature: 用于定位该记录是否已经在WebPage中存在
 *
 * 当插入一条新记录时，首先查看urlMd5 + signature是否已经存在，如存在，
 * 则直接插入KeepPoint，不用再插入WebPage，
 * 否则表示该内容在WebPage中不存在，还需要插入WebPage
 *
 * User: xiatian
 * Date: 2/23/13 4:26 PM
 */
public class KeepPoint extends MongoObject {
    private String url;
    private String urlMd5;
    private Date keepTime;
    private String signature;
    private String agent;

    public KeepPoint(){

    }

    public KeepPoint(String url, Date keepTime, String signature, String agent) {
        this.url = url;
        this.urlMd5 = Signature.getMd5String(url);
        this.keepTime = keepTime;
        this.signature = signature;
        this.agent = agent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlMd5() {
        return urlMd5;
    }

    public void setUrlMd5(String urlMd5) {
        this.urlMd5 = urlMd5;
    }

    public Date getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Date keepTime) {
        this.keepTime = keepTime;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    @Override
    public String toString() {
        return "KeepPoint{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", urlMd5='" + urlMd5 + '\'' +
                ", keepTime=" + keepTime +
                ", signature='" + signature + '\'' +
                ", agent='" + agent + '\'' +
                '}';
    }
}
