package ruc.summer.storage.dao.impl;

import com.mongodb.BasicDBObject;
import ruc.summer.storage.dao.KeepPointDao;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.dao.core.MongoDao;
import ruc.summer.storage.dao.helper.Query;
import ruc.summer.storage.model.KeepPoint;
import ruc.summer.util.Signature;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: xiatian
 * Date: 2/23/13 6:08 PM
 */
public class MongoKeepPointDao extends MongoDao<KeepPoint> implements KeepPointDao {

    public MongoKeepPointDao() throws DaoException {
        super(KeepPoint.class);

//        collection.dropIndex("md5AndSignatureIndex");
        collection.ensureIndex(new BasicDBObject("urlMd5", 1).append("signature", 1), "md5AndSignatureIndex", false);
        collection.ensureIndex(new BasicDBObject("urlMd5", 1).append("keepTime", 1), "md5AndKeepTimeIndex", true);
    }

    @Override
    public boolean existsByUrlSignature(String url, String signature) throws DaoException {
        String md5 = Signature.getMd5String(url);

        return count(Query.create("urlMd5", md5).and("signature", signature)) > 0;
    }

    @Override
    public boolean existsByMd5Signature(String urlMd5, String signature) throws DaoException {
        return count(Query.create("urlMd5", urlMd5).and("signature", signature)) > 0;
    }

    @Override
    public boolean existsByUrlKeepTime(String url, Date keepTime) throws DaoException {
        String md5 = Signature.getMd5String(url);

        return count(Query.create("urlMd5", md5).and("keepTime", keepTime)) > 0;
    }

    @Override
    public boolean existsByMd5KeepTime(String urlMd5, Date keepTime) throws DaoException {
        return count(Query.create("urlMd5", urlMd5).and("keepTime", keepTime)) > 0;
    }

    @Override
    public KeepPoint getByUrlKeepTime(String url, Date keepTime) throws DaoException {
        String md5 = Signature.getMd5String(url);
        return getByMd5KeepTime(md5, keepTime);
    }

    @Override
    public KeepPoint getByMd5KeepTime(String urlMd5, Date keepTime) throws DaoException {
        return findOne(Query.create("urlMd5", urlMd5).and("keepTime", keepTime));
    }

    @Override
    public KeepPoint getByMd5KeepTime(String urlMd5, Date keepTime, boolean nearby) throws DaoException {
        Query query = Query.create("urlMd5", urlMd5).and("keepTime", keepTime, Query.Condition.Type_GTE);
        return findOne(query);
    }

    @Override
    public List<KeepPoint> getListByUrl(String url, Date startDate, Date endDate) throws DaoException {
        String md5 = Signature.getMd5String(url);
        Query query = Query.create("urlMd5", md5).and("keepTime", startDate, Query.Condition.Type_GTE).and("keepTime", endDate, Query.Condition.Type_LTE);
        return findList(query);
    }

    @Override
    public List<KeepPoint> getListByUrl(String url, String yyyyMMdd) throws DaoException {
        String md5 = Signature.getMd5String(url);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date startDate = df.parse(yyyyMMdd);
            Date endDate = new Date(startDate.getTime() + 86400000);
            Query query = Query.create("urlMd5", md5).and("keepTime", startDate, Query.Condition.Type_GTE).and("keepTime", endDate, Query.Condition.Type_LTE);

            return findList(query);
        } catch (ParseException e) {
            throw new DaoException("日期格式错误，应为yyyyMMdd格式，如20130316");
        }
    }

    @Override
    public boolean existsOneByUrl(String url, String yyyyMMdd) throws DaoException {
        String md5 = Signature.getMd5String(url);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date startDate = df.parse(yyyyMMdd);
            Date endDate = new Date(startDate.getTime() + 86400000);
            Query query = Query.create("urlMd5", md5).and("keepTime", startDate, Query.Condition.Type_GTE).and("keepTime", endDate, Query.Condition.Type_LTE);

            return count(query)>0;
        } catch (ParseException e) {
            throw new DaoException("日期格式错误，应为yyyyMMdd格式，如20130316");
        }
    }
}
