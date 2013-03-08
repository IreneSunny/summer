package ruc.summer.spider.protocol.filter;


public class BasicURLFilter implements URLFilter{

  @Override
  public String filter(String urlString) {
    String link = urlString;
    if(link == null) return null;
    int pos = link.indexOf(";jsessionid=");
    if (pos > 0) {
      link = link.substring(0, pos);
    }
    if(link.endsWith("#")){
      link = link.substring(0, link.length()-1);
    }
    
    //过滤掉图片链接
    if(isImageLink(link)){
      return null;
    }
    
    if(!urlString.toLowerCase().startsWith("http://")){
      return null;
    }
    
    return link;
  }

  private static String[] imagesSuffix = new String[]{".jpg", ".jpeg", ".gif", ".png", ".bmp", ".tiff"};
  private boolean isImageLink(String link){
    for(String suffix: imagesSuffix){
      if(link.toLowerCase().endsWith(suffix)){
        return true;
      }
    }
    return false;
  }
}
