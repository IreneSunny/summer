package ruc.summer.storage.model;

import org.bson.types.ObjectId;

import java.io.Serializable;


/**
 * 标记类是否可以存储到MongoDB中的接口.<br/>
 * 注意事项：<br/>
 * <ol>
 * <li>mongodb中可以把Set对象插入进去，但实际上是以列表形式插入的，在读出的时候可以转为List对象，不能转为Set对象</li>
 * <li>增加一个字段，可以用update的$set方式</li>
 * <li>日期类型：db.users.find({creation_date:{$gt:new Date(2010,0,1), $lte:new Date(2010,11,31)}); </li>
 * <li></li>
 * </ol>
 *
 * @author xiatian
 */
public abstract class MongoObject implements Serializable {
    private static final long serialVersionUID = -3121979374993731491L;

    //默认在客户端生成ObjectId，以提高效率
    protected ObjectId id = new ObjectId();

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getId() {
        return this.id;
    }

}
