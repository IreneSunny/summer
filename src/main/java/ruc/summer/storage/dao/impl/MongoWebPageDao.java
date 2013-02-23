package ruc.summer.storage.dao.impl;

import com.mongodb.BasicDBObject;
import ruc.summer.storage.dao.WebPageDao;
import ruc.summer.storage.dao.core.BaseDao;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.dao.core.MongoDao;
import ruc.summer.storage.model.WebPage;

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
    public boolean existsBySignature(String url, String signature) throws DaoException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WebPage getBySignature(String url, String signature) throws DaoException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
