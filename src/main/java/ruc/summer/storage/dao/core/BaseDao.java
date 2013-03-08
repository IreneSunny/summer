package ruc.summer.storage.dao.core;

import ruc.summer.storage.dao.helper.Query;
import ruc.summer.util.Pagination;

import java.util.Iterator;
import java.util.List;


/**
 * SMARTER makes us smarter by using the powerful Web brain.
 *
 * @author: Summer XIA
 * @date: 8/20/12
 */
public interface BaseDao<T> {

    /**
     * 删除符合查询条件的所有记录
     * @param query
     * @throws DaoException
     */
    void delete(Query query) throws DaoException;

    void deleteById(Object id) throws DaoException;

    /**
     * 获取对象T中的所有记录的数量
     * @return
     * @throws DaoException
     */
    long count() throws DaoException;

    /**
     * 获取对象T中满足query条件的记录数量
     * @param query
     * @return
     * @throws DaoException
     */
    long count(Query query) throws DaoException;

    /**
     * 根据ID获取唯一一个对象，如果对象不存在，则返回null。对于MongoObject类型的对象，为_id字段
     * @param id
     * @return
     * @throws DaoException
     */
    T findById(Object id) throws DaoException;

    /**
     * <p>如果obj不存在，则存入存储库中，返回ture，否则直接返回false</p>
     * <p>
     *     不同的对象，其是否存在的判断条件各不相同，
     * 例如，Saying默认按照signature作为判重字段名称，Story则默认按照urlMd5作为判重依据
     * </p>
     * @param obj
     * @return
     * @throws DaoException
     */
    boolean addIfNotExists(T obj) throws DaoException;

    /**
     * <p>如果对象不存在，则插入，返回Action_Insert；
     * 否则更新该对象，返回Action_Update</p>
     * <p>不同的对象，其是否存在的判断条件各不相同，
     * 例如，Saying默认按照signature作为判重字段名称，Story则默认按照urlMd5作为判重依据
     * </p>
     * @param obj
     * @throws DaoException
     */
    int upsert(T obj) throws DaoException;

    /**
     * <p>根据对象的默认判重条件，更新该对象，如果按照默认条件查询该对象不存在，则抛出异常进行提示</p>
     * <p>不同的对象，其是否存在的判断条件各不相同，
     * 例如，Saying默认按照signature作为判重字段名称，Story则默认按照urlMd5作为判重依据
     * </p>
     * @param obj
     * @throws DaoException
     */
    void update(T obj) throws DaoException;

    /**
     * 获取该对象所有的内容
     * @return
     * @throws DaoException
     */
    List<T> getAll() throws DaoException;

    /**
     * 根据查询条件生成迭代器
     * @param query
     * @return
     * @throws DaoException
     */
    Iterator<T> iterator(Query query) throws DaoException;

    /**
     * 根据查询条件返回查询结果
     * @param query
     * @return
     * @throws DaoException
     */
    List<T> findList(Query query) throws DaoException;

    /**
     * 返回从start位置开始的count条记录，排序方式由实现类决定
     * @param start
     * @param count
     * @return
     * @throws DaoException
     */
    List<T> findList(int start, int count) throws DaoException;


    /**
     * 根据conditions作为查询条件，返回第一个满足条件的对象
     * @param conditions
     * @return
     * @throws DaoException
     */
    T findOne(Query conditions) throws DaoException;

    /**
     * 根据conditions作为查询条件，判断对象是否存在
     * @param conditions
     * @return
     * @throws DaoException
     */
    boolean exists(Query conditions) throws DaoException;

    /**
     * 增加一个对象到仓库中
     * @param obj
     * @throws DaoException
     */
    void add(T obj) throws DaoException;

    /**
     * 根据QueryCondition作为区分对象的条件，如果对象不存在，则插入该对象，返回true，否则返回false
     * @param obj
     * @return
     * @throws DaoException
     */
    boolean addIfNotExists(Query conditions, T obj) throws DaoException;

    /**
     * 更新一个对象到仓库中
     * @param obj
     * @throws DaoException
     */
    void update(Query conditions, T obj) throws DaoException;

    /**
     * 根据QueryCondition判断是否重复， 更新一个对象到仓库中，返回Action_Update；
     * 如果对象不存在，则增加，返回Action_Insert
     * @param obj
     * @throws DaoException
     */
    int upsert(Query conditions, T obj) throws DaoException;

    Pagination<T> pagination(Query query, int page, int pageSize) throws DaoException;

    public static final int Action_Insert = 0;
    public static final int Action_Update = 1;
}
