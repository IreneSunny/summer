package ruc.summer.storage.dao.core;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.beanutils.PropertyUtils;
import org.bson.types.ObjectId;
import ruc.summer.storage.dao.helper.MongoClient;
import ruc.summer.storage.dao.helper.Query;
import ruc.summer.storage.dao.helper.converter.BaseProxy;
import ruc.summer.storage.dao.helper.converter.bean.BeanEnhancer;
import ruc.summer.storage.model.KeepPoint;
import ruc.summer.storage.model.MongoObject;
import ruc.summer.storage.model.WebPage;
import ruc.summer.util.Pagination;

import java.util.*;

/**
 * SMARTER makes us smarter by using the powerful Web brain.
 *
 * @author: xiatian
 * @date: 8/25/12
 */
public abstract class MongoDao<T extends MongoObject> {
    /**
     * 维持了对象和对应的MongoDB中的Collection名称之间的对应关系
     */
    public static Map<Class<? extends MongoObject>, String> Db_Collections = new HashMap<Class<? extends MongoObject>, String>();

    static {
        Db_Collections.put(KeepPoint.class, "data.keeppoint");
        Db_Collections.put(WebPage.class, "data.webpage");
    }

    /**
     * 获取类所对应的MongoDB中的集合名称，默认为类的名称的小写形式
     *
     * @param clazz
     * @return
     */
    public static String getCollectionName(Class<? extends MongoObject> clazz) {
        String name = Db_Collections.get(clazz);
        return (name == null) ? clazz.getSimpleName().toLowerCase() : name;
    }

    protected Class<T> clazz = null;

    protected DBCollection collection = null;

    public MongoDao(Class<T> clazz) throws DaoException {
        this.clazz = clazz;
        collection = MongoClient.getCollection(getCollectionName(clazz));
    }

    public void delete(Query query) throws DaoException {
        collection.remove(parseQuery(query));
    }

    public void deleteById(Object id) throws DaoException {
        collection.remove(new BasicDBObject("_id", id));
    }

    /**
     * 生成对象默认的查询主键条件，用于判断对象是否存在的默认条件
     * @param obj
     * @return
     * @throws DaoException
     */
    protected Query key(T obj) throws DaoException  {
        return Query.create("id", obj.getId());
    }

    /**
     * 默认的增加对象方法，根据ID判断重复
     *
     * @param obj
     * @return
     * @throws DaoException
     */
    public boolean addIfNotExists(T obj) throws DaoException {
        if(exists(key(obj))) {
            return false;
        } else {
            add(obj);
            return true;
        }
    }

    /**
     * 默认的增加或更新对象方法，根据ID判断重复
     * @param obj
     * @throws DaoException
     */
    public int upsert(T obj) throws DaoException {
        if(exists(key(obj))) {
            update(obj);
            return BaseDao.Action_Update;
        } else {
            add(obj);
            return BaseDao.Action_Insert;
        }
    }

    /**
     * 默认的更新对象方法，根据ID判断重复
     * @param obj
     * @throws DaoException
     */
    public void update(T obj) throws DaoException {
        update(key(obj), obj);
    }

    public long count() throws DaoException {
        return collection.count();
    }

    /**
     * 获取对象T中满足query条件的记录数量
     * @param query
     * @return
     * @throws DaoException
     */
    public long count(Query query) throws DaoException {
        return collection.count(parseQuery(query));
    }


    public T findById(Object id) throws DaoException {
        DBObject doc = collection.findOne(new BasicDBObject("_id", id));
        return as(doc);
    }

    public T findOne(Query conditions) throws DaoException {
        DBObject doc = collection.findOne(parseQuery(conditions));
        return as(doc);
    }

    public boolean exists(Query conditions) throws DaoException {
        return count(conditions) > 0;
    }


    public List<T> getAll() throws DaoException {
        List<T> list = new ArrayList<T>();
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            list.add(as(cursor.next()));
        }
        return list;
    }

    public List<T> findList(Query query) throws DaoException {
        final DBCursor cursor = collection.find(parseQuery(query)).sort(parseOrder(query)).skip(query.getSkip()).limit(query.getLimit());
        List<T> list = new ArrayList<T>();
        while (cursor.hasNext()) {
            DBObject item = cursor.next();
            list.add(as(item));
        }
        return list;
    }

    /**
     * 返回从start位置开始的count条记录，排序方式默认为_id, 子类通过覆盖该方法重写排序方式
     * @param start
     * @param count
     * @return
     * @throws DaoException
     */
    public List<T> findList(int start, int count) throws DaoException {
        final DBCursor cursor = collection.find().sort(new BasicDBObject("_id", -1)).skip(start).limit(count);
        List<T> list = new ArrayList<T>();
        while (cursor.hasNext()) {
            list.add(as(cursor.next()));
        }
        return list;
    }

    public Iterator<T> iterator(Query query) throws DaoException {
        final DBCursor cursor = collection.find(parseQuery(query)).sort(parseOrder(query)).skip(query.getSkip()).limit(query.getLimit());
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }

            @Override
            public T next() {
                return as(cursor.next());
            }

            @Override
            public void remove() {
                cursor.remove();
            }
        };
    }

    public void add(T obj) throws DaoException {
        DBObject doc = toDBObject(obj);
        collection.save(doc);
    }

    public final boolean addIfNotExists(Query conditions, T obj) throws DaoException {
        if (exists(conditions)) {
            return false;
        } else {
            add(obj);
            return true;
        }
    }

    public final void update(Query conditions, T obj) throws DaoException {
        DBObject q = parseQuery(conditions);
        collection.update(q, toDBObject(obj));
    }

    public final void update(Query query, DBObject doc) throws DaoException {
        collection.update(parseQuery(query), doc);
    }

    public final int upsert(Query conditions, T obj) throws DaoException {
        if (exists(conditions)) {
            update(conditions, obj);
            return BaseDao.Action_Update;
        } else {
            add(obj);
            return BaseDao.Action_Insert;
        }
    }

    public Pagination<T> pagination(Query query, int page, int pageSize) throws DaoException {
        Pagination<T> p = new Pagination<T>(page,  pageSize);
        p.setRecordSum(count(query));
        query.skip(p.getPosition()-1).limit(pageSize);
        p.setRecordList(findList(query));
        return p;
    }

    /**
     * 把通用的查询Query对象，转化为MongoDB的DBObject.
     *
     * @param conditions
     * @return
     * @throws DaoException
     */
    protected final BasicDBObject parseQuery(Query conditions) throws DaoException {
        BasicDBObject query = new BasicDBObject();

        for (Query.Condition condition : conditions) {
            String field = condition.getField();
            if (field.equals("id")) field = "_id";
            switch (condition.getType()) {
                case Query.Condition.Type_Equal:
                    query.put(field, condition.getValue());
                    break;
                case Query.Condition.Type_GT:
                    query.put(field, new BasicDBObject("$gt", condition.getValue()));
                    break;
                case Query.Condition.Type_GTE:
                    query.put(field, new BasicDBObject("$gte", condition.getValue()));
                    break;
                case Query.Condition.Type_LT:
                    query.put(field, new BasicDBObject("$lt", condition.getValue()));
                    break;
                case Query.Condition.Type_LTE:
                    query.put(field, new BasicDBObject("$lte", condition.getValue()));
                    break;
                case Query.Condition.Type_NE:
                    query.put(field, new BasicDBObject("$ne", condition.getValue()));
                    break;
                default:
                    throw new DaoException("Unsupport QueryCondition type:" + condition.getType());
            }
        }
        return query;
    }

    protected final BasicDBObject parseOrder(Query conditions) {
        BasicDBObject orders = new BasicDBObject();
        for (Query.Order order : conditions.getOrders()) {
            String field = order.getField();
            if(field.equals("id")){
                field = "_id";
            }
            orders.put(field, order.getType());
        }
        return orders;
    }

    public final DBObject toDBObject(Object obj) {
        Object val = BaseProxy.passValue(obj);
        if (val instanceof DBObject) {
            return (DBObject) val;
        } else {
            throw new RuntimeException("Converted object is not instance of DBObject");
        }
    }

    @SuppressWarnings({"unchecked"})
    public final T as(DBObject source) {
        if (source == null) return null;

        T newObject = (T) BeanEnhancer.create(clazz, source);
        newObject.setId((ObjectId)source.get("_id"));
        try {
            T copyObject = clazz.newInstance();
            //@ todo maybe other way of solving this?
            // if no complex entities with polymorphism are used, it should be safe to just "return newObject" and skip bean copying
            PropertyUtils.copyProperties(copyObject, newObject);
            copyObject.setId((ObjectId)source.get("_id"));
            return copyObject;
        } catch (Exception e) {
            e.printStackTrace();
            return newObject;
        }
    }
}
