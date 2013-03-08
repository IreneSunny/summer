package ruc.summer.spider;

/**
 * User: xiatian
 * Date: 2/24/13 2:28 PM
 */
public class Injector {
    public static void main(String[] args) {
        FetchItemQueue queue = new FetchItemQueue();
        if(args.length>0) {
            for (int i = 0; i < args.length; i++) {
                if(args[i].startsWith("http")) {
                    queue.rpush(args[i]);
                }
            }
        }

        queue.lpush("http://bbs.tianya.cn/");
        queue.lpush("http://www.ruc.edu.cn/");
        queue.lpush("http://www.sohu.com/");
        queue.rpush("http://www.people.com.cn/");
        queue.rpush("http://www.xinhuanet.com/");
        queue.rpush("http://www.huanqiu.com/");
        queue.rpush("http://www.gmw.cn/");
        queue.rpush("http://www.xinmin.cn/");
        queue.rpush("http://www.shanghai.gov.cn/");
        queue.rpush("http://www.gov.hk/");
        queue.rpush("http://www.gd.gov.cn/");
        queue.rpush("http://www.beijing.gov.cn/");
        queue.rpush("http://www.cq.gov.cn/");
        queue.rpush("http://www.henan.gov.cn/");
        queue.rpush("http://www.qq.com/");
        queue.rpush("http://www.sina.com.cn/");
        queue.rpush("http://www.163.com/");
        queue.rpush("http://club.china.com/");
    }
}
