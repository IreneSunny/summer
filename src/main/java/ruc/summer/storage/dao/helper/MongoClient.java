package ruc.summer.storage.dao.helper;

import com.mongodb.*;
import org.bson.types.ObjectId;
import ruc.summer.conf.ConfFactory;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.util.Signature;
import sun.util.LocaleServiceProviderPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MongoClient {
    public static final String Collection_Crawl = "crawl";

    /** 降序排序 */
    public static final int Sort_Decrease = -1;

    /** 升序排序 */
    public static final int Sort_Increase = 1;

//    private static final Logger LOG = LoggerFactory.getLogger(MongoClient.class);
    static DB db = null;
    static Map<String, DBCollection> dbCollections = new HashMap<String, DBCollection>();

    private MongoClient() {

    }

    public static DB connect() throws DaoException {
        if (db == null) {
            try {
                Mongo m = new Mongo(ConfFactory.getConf().get("mongodb.host", "server29"),
                        ConfFactory.getConf().getInt("mongodb.port",  27017));
                db = m.getDB(ConfFactory.getConf().get("mongodb.dbname", "repo"));
                String username = ConfFactory.getConf().get("mongodb.user", "summer");
                String password = ConfFactory.getConf().get("mongodb.password", "xiatian");
                boolean auth = db.authenticate(username, password.toCharArray());
                if(!auth) {
                    System.out.println("MongoDB验证失败");
                }

                //设置Log为固定集合类型，仅保留一定大小的内容
//                db.createCollection(AuditLog.Collection_Name, new BasicDBObject("capped", true).append("size", 100000).append("max", 50000));
            } catch (IOException e1) {
                throw new DaoException(e1);
            } catch (MongoException e2) {
                throw new DaoException(e2);
            }
        }
        return db;
    }

    public static DBCollection getCollection(String name) throws DaoException {
        DBCollection c = dbCollections.get(name);
        if (c == null) {
            c = connect().getCollection(name);
            dbCollections.put(name, c);
        }
        return c;
    }

    /**
   * 根据种子生成ObjectId
   * @param seed
   * @return
   */
  public static ObjectId makeObjectId(String seed){
      byte[] buffer = new byte[12];

      //前4个字节固定为19780912010101的seconds数值, 274381261000
      long time = 274381261000l;
      buffer[3] = (byte)(time & 0xFF);
      time >>= 8;
      buffer[2] = (byte)(time & 0xFF);
      time >>= 8;
      buffer[1] = (byte)(time & 0xFF);
      time >>= 8;
      buffer[0] = (byte)(time);

      //后2个字节为字符串的md5值，每2个byte相加结果作为1个存入
      byte[] hash = Signature.calculate(seed.getBytes());
      int hashIndex = 0;
      for(int i=4; i<12; i++) {
          buffer[i] = (byte)(hash[hashIndex] ^ hash[hashIndex+1]);
          hashIndex += 2;
      }

      return new ObjectId(buffer);
  }
}
