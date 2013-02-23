package ruc.summer.storage.dao;

import ruc.summer.storage.dao.core.BaseDao;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.model.WebPage;

/**
 * User: xiatian
 * Date: 2/23/13 4:46 PM
 */
public interface WebPageDao extends BaseDao<WebPage> {
    /**
     * URL对应的签名内容是否存在
     * @param url
     * @param signature
     * @return
     * @throws ruc.summer.storage.dao.core.DaoException
     */
    boolean existsBySignature(String url, String signature) throws DaoException;

    /**
     * 获取指定签名所对应的WebPage
     * @param url
     * @param signature
     * @return
     * @throws DaoException
     */
    WebPage getBySignature(String url, String signature) throws DaoException;
}
