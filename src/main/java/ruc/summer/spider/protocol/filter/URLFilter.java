package ruc.summer.spider.protocol.filter;

/**
 * Interface used to limit which URLs enter Nutch.
 * Used by the injector and the db updater.
 */

public interface URLFilter {
  
  /* Interface for a normalize that transforms a URL: it can pass the
     original URL through or "delete" the URL by returning null */
  public String filter(String urlString);
}
