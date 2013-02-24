package ruc.summer.storage;

import org.junit.Test;
import org.zhinang.conf.Configuration;
import org.zhinang.protocol.http.HttpClientAgent;
import org.zhinang.protocol.http.UrlResponse;
import ruc.summer.storage.model.WebPage;

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
        HttpClientAgent agent = new HttpClientAgent(new Configuration());
        UrlResponse response = agent.execute(url);

        Map<String, String> map = new HashMap<String, String>();
        for(String key: response.getHeaders().names()) {
            map.put(key, response.getHeader(key));
        }
        Result result = Storage.save(url, new Date(), map, response.getContentType(), "中国人民大学", "", response.getContent(), "xiatian");

        System.out.println(result);
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
        List<Date> list = Storage.getKeepTimeList(url);
        for (Date d : list) {
            WebPage page = Storage.getWebPage(url, d);
            System.out.println(new String(page.getContent(), page.getEncoding()));
        }
    }
}
