package ruc.summer.storage.dao.impl;

import com.mongodb.BasicDBObject;
import ruc.summer.storage.dao.WebPageDao;
import ruc.summer.storage.dao.core.BaseDao;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.dao.core.MongoDao;
import ruc.summer.storage.dao.helper.Query;
import ruc.summer.storage.model.WebPage;
import ruc.summer.util.Signature;

/**
 * User: xiatian
 * Date: 2/23/13 6:24 PM
 */
public class MongoWebPageDao extends MongoDao<WebPage> implements WebPageDao {


    public MongoWebPageDao() throws DaoException {
        super(WebPage.class);

        collection.ensureIndex(new BasicDBObject("urlMd5", 1).append("signature", 1), "md5AndSignatureIndex", true);
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
    public WebPage getByUrlSignature(String url, String signature) throws DaoException {
        String md5 = Signature.getMd5String(url);

        return findOne(Query.create("urlMd5", md5).and("signature", signature));
    }

    @Override
    public WebPage getByMd5Signature(String urlMd5, String signature) throws DaoException {
        return findOne(Query.create("urlMd5", urlMd5).and("signature", signature));
    }
}
