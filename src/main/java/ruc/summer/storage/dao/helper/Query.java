package ruc.summer.storage.dao.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  代表一组查询条件
 *
 * @author: xiatian
 * @date: 8/25/12
 */
public class Query implements Iterable<Query.Condition> {
    private List<Condition> conditions = new ArrayList<Condition>();

    private List<Order> orders = new ArrayList<Order>();
    private int skip = 0; //跳过多少个记录
    private int limit = Integer.MAX_VALUE; //最多保留多少个记录

    public static Query create() {
        return new Query();
    }

    public static Query create(String field, Object value) {
        return new Query(field, value);
    }

    public static Query create(String field, Object value, int type) {
        return new Query(field, value, type);
    }

    private Query(){}

    private Query(String field, Object value) {
        conditions.add(new Condition(field, value));
    }

    private Query(String field, Object value, int type) {
        conditions.add(new Condition(field, value, type));
    }

    public Query and(String field, Object value) {
        this.conditions.add(new Condition(field, value));
        return this;
    }

    public Query and(String field, Object value, int type) {
        this.conditions.add(new Condition(field, value, type));
        return this;
    }

    public Query and(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    /**
     * 根据字段升序排序
     * @param field
     * @return
     */
    public Query ascBy(String field){
        this.orders.add(new Order(field, Order.ASC));
        return this;
    }

    /**
     * 根据字段将序排序
     * @param field
     * @return
     */
    public Query descBy(String field){
        this.orders.add(new Order(field, Order.DESC));
        return this;
    }

    public Query skip(int skip){
        this.skip = skip;
        return this;
    }

    public int getSkip(){
        return this.skip;
    }

    public Query limit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getLimit(){
        return this.limit;
    }

    public Query clear(){
        this.conditions.clear();
        this.orders.clear();
        return this;
    }

    public Iterator<Condition> iterator(){
        return this.conditions.iterator();
    }

    public List<Order> getOrders(){
        return this.orders;
    }

    public static final class Order {
        public static final int ASC = 1;
        public static final int DESC = -1;
        /** 排序字段 */
        private String field;
        /** 排序类型，升序或将序 */
        private int type;

        public Order(String field, int type) {
            this.field = field;
            this.type = type;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    /**
     *  查询条件类，用于屏蔽不同的底层数据存储所使用的查询条件
     *
     * @author: xiatian
     * @date: 8/25/12
     */
    public static class Condition {
        /** 字段名称和值相等 */
        public static final int Type_Equal = 0;

        /** 字段名称和值为大于关系 */
        public static final int Type_GT = 1;

        /** 字段名称和值为大于等于关系 */
        public static final int Type_GTE = 2;

        /** 字段名称和值为小于关系 */
        public static final int Type_LT = 3;

        /** 字段名称和值为小于等于关系 */
        public static final int Type_LTE = 4;

        /** 字段名称和值为不等于关系 */
        public static final int Type_NE = 5;

        /** 查询条件所对应的字段名称 */
        private String field;

        /** 字段的取值 */
        private Object value;

        /** 查询条件类型, 如等于，大于... */
        private int type = Type_Equal;

        public Condition(String field, Object value) {
            this.field = field;
            this.value = value;
        }

        public Condition(String field, Object value, int type) {
            this.field = field;
            this.value = value;
            this.type = type;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
