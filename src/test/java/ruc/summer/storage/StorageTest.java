package ruc.summer.storage;

import org.junit.Test;
import org.zhinang.conf.Configuration;
import org.zhinang.protocol.http.HttpClientAgent;
import org.zhinang.protocol.http.UrlResponse;
import ruc.summer.storage.model.WebPage;
import ruc.summer.util.GZipUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: xiatian
 * Date: 2/23/13 8:02 PM
 */
public class StorageTest {
    private String url = "http://www.ruc.edu.cn/";

    @Test
    public void testSave() throws Exception {
        url = "http://news.ifeng.com/";
        HttpClientAgent agent = new HttpClientAgent(new Configuration());
        UrlResponse response = agent.execute(url);

        Map<String, String> map = new HashMap<String, String>();
        for(String key: response.getHeaders().names()) {
            map.put(key, response.getHeader(key));
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = df.parse("2013-2-8 19:30:05");
        System.out.println(d);
        Result result = Storage.save(url, d, map, response.getContentType(), "凤凰网", "", response.getContent(), "xiatian");
//
//        System.out.println(result);
    }

    @Test
    public void testGetKeepTimeList() throws Exception {
        List<Date> list = Storage.getKeepTimeList(url);
        for (Date d : list) {
            System.out.println(d);
        }

    }

    @Test
    public void testGetWebPage() throws Exception {
        url = "http://www.ruc.edu.cn/archives/21325";
        List<Date> list = Storage.getKeepTimeList(url);
        for (Date d : list) {
            WebPage page = Storage.getWebPage(url, d, false);
            byte[] content = page.getContent();
            if (page.isZipped()) {
                content = GZipUtils.decompress(content);
            }
            System.out.println(new String(content, page.getEncoding()));
        }
    }
}
