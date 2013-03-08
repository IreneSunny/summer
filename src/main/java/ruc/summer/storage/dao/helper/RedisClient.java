package ruc.summer.storage.dao.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ruc.summer.conf.ConfFactory;
import ruc.summer.storage.dao.core.DaoException;

import java.util.List;

/**
 * RedisClient提供链接Redis服务器的方法，该类不能直接实例化，只能通过connect链接到JRedis服务器
 *
 * @author xiatian
 *
 */
public class RedisClient {
    private static final Logger LOG = LoggerFactory.getLogger(RedisClient.class);

    static JedisPool pool = null;
    //在Redis中的key会自动合并上该前缀
    private static String Redis_Key_Prefix = "";
    private RedisClient(){}

    public static JedisPool getPool() {
        if (pool == null) {
            String host = ConfFactory.getConf().get("redis.host", "server28");
            int port = ConfFactory.getConf().getInt("redis.port", 6379);
            LOG.warn("REDIS INFO: host={}, port={}", host, port);

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxActive(50);
            pool = new JedisPool(config, host, port, 2000);
            Redis_Key_Prefix = ConfFactory.getConf().get("redis.key.prefix", "");
            if(Redis_Key_Prefix.length() >0 && !Redis_Key_Prefix.endsWith(":")) {
                Redis_Key_Prefix += ":";
            }
        }
        return pool;
    }

    /**
     * 对用户指定的key进行规范化处理，加上统一的前缀，
     * 这样，如果系统在同一台机器上运行多套时，可以通过指定不同的前缀实现Redis中数据的互不干扰
     * @param key
     * @return
     */
    public static String normalizeKey(String key) {
    	return Redis_Key_Prefix + key;
    }

    public static boolean exists(String key) {
        JedisPool pool = getPool();
        Jedis jedis = pool.getResource();
        boolean existed = jedis.exists(key);
        pool.returnResource(jedis);

        return existed;
    }

    public static List<String> lrange(String key, int start, int count) throws DaoException {
        JedisPool pool = RedisClient.getPool();
        Jedis jedis = pool.getResource();

        List<String> list = jedis.lrange(key, start, start + count - 1);

        pool.returnResource(jedis);
        return list;
    }

}
