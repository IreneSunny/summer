package ruc.summer.storage;

import ruc.summer.storage.dao.KeepPointDao;
import ruc.summer.storage.dao.WebPageDao;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.dao.impl.MongoKeepPointDao;
import ruc.summer.storage.dao.impl.MongoWebPageDao;

/**
 * User: xiatian
 * Date: 2/23/13 7:04 PM
 */
public class DaoFactory {
    private static KeepPointDao keepPointDao = null;
    private static WebPageDao webPageDao = null;

    public static KeepPointDao getKeepPointDao() throws DaoException {
        if(keepPointDao==null) {
            keepPointDao = new MongoKeepPointDao();
        }
        return keepPointDao;
    }

    public static WebPageDao getWebPageDao() throws DaoException {
        if (webPageDao == null) {
            webPageDao = new MongoWebPageDao();
        }
        return webPageDao;
    }
}
