package ruc.summer.spider.protocol.normalizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;


public final class URLNormalizers {
  private static URLNormalizer[] normalizers = new URLNormalizer[]{new BasicURLNormalizer()};
  
  /**
   * Normalize
   * @param urlString The URL string to normalize.
   * @return A normalized String
   * @throws java.net.MalformedURLException If the given URL string is malformed.
   */
  public static String normalize(String urlString)
          throws MalformedURLException {
    String newUrl = urlString;
    for(URLNormalizer normalizer: normalizers){
      newUrl = normalizer.normalize(newUrl, null);
    }
    return newUrl;
  }
  
  private static void check() throws Exception {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = in.readLine()) != null) {
      String out = normalize(line);
      System.out.println(out);
    }    
  }
  
  public static void main(String[] args) throws Exception {
    check();
  }
}
