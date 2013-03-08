package ruc.summer.spider.protocol.filter;

/** Creates and caches {@link URLFilter} implementing plugins.*/
public class URLFilters {

  /** Run all defined filters. Assume logical AND. */
  public static String filter(String urlString) {
    URLFilter filter = new BasicURLFilter();
    
    return filter.filter(urlString);
  }
}
