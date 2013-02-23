package ruc.summer.storage.dao;

import ruc.summer.storage.dao.core.BaseDao;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.model.KeepPoint;

import java.util.Date;
import java.util.List;

/**
 * User: xiatian
 * Date: 2/23/13 4:33 PM
 */
public interface KeepPointDao extends BaseDao<KeepPoint> {
    /**
     * URL对应的签名是否存在
     * @param url
     * @param signature
     * @return
     */
    boolean existsBySignature(String url, String signature) throws DaoException;

    /**
     * url对应的时间点有无留痕记录
     * @param url
     * @param keepTime
     * @return
     * @throws DaoException
     */
    boolean existsByKeepTime(String url, Date keepTime) throws DaoException;

    /**
     * 获取起止时间之内的所有留痕点
     * @param url
     * @param startDate
     * @param endDate
     * @return
     * @throws DaoException
     */
    List<KeepPoint> getList(String url, Date startDate, Date endDate) throws DaoException;

    /**
     * 获取某一天的所有留痕记录
     * @param url
     * @param yyyyMMdd
     * @return
     * @throws DaoException
     */
    List<KeepPoint> getList(String url, String yyyyMMdd) throws DaoException;

    /**
     * 在某一天是否有过留痕记录，日期为yyyyMMdd的字符串，例如20130226
     * @param url
     * @param yyyyMMdd
     * @return
     * @throws DaoException
     */
    boolean existsOne(String url, String yyyyMMdd) throws DaoException;


}
