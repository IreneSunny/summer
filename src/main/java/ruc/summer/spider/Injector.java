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

        queue.rpush("http://www.ruc.edu.cn/");
        queue.rpush("http://www.sohu.com/");
    }
}
