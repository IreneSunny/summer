package ruc.summer.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 分页处理类, 记住需要设置分页对象包含的总记录数量，以便自动处理总页数, 使用方法：<br/>
 * <br/>方式1：
 * <pre>
 *     Pagination p = new Pagiantion(pageId, countPerPage);
 *     p.setRecordSum(totalRecordsSum);
 *     p.setRecordList(recordList);
 * </pre>
 * 
 * <br/>方式2：
 * <pre>
 *     Pagination p = new Pagiantion(pageId, countPerPage, totalRecords);
 *     p.setRecordList(recordList);
 * </pre>
 * @author xiat
 * 
 * @param T
 *            分页存放的基本数据对象类型
 */
public class Pagination<T> implements Serializable {
    private static final long serialVersionUID = -2046803242155647335L;

    /** 当前页 */
    private int currentPage;
    /** 总页数 */
    private int totalPage;
    /** 总记录数量 */
    private long recordSum;
    /** 每页数量 */
    private int countPerPage;
    /** 内容列表 */
    private List<T> recordList;
    
    public Pagination(int currentPage, int countPerPage) {
        this.currentPage = currentPage;
        this.countPerPage = countPerPage;
    }

    public Pagination(int currentPage, int countPerPage, long totalRecords) {
        this.currentPage = currentPage;
        this.countPerPage = countPerPage;
        this.setRecordSum(totalRecords);
    }

    public void setRecordSum(long sum) {
        this.recordSum = sum;

        // 计算总页数
        totalPage = (int) (sum / countPerPage);
        if ((sum % countPerPage > 0) || totalPage == 0) {
            totalPage++;
        }
    }

    /**
     * 每页有<code>pageSize</code>条记录，共<code>sum</code>记录，求总共的页数
     * 
     * @param sum
     * @param pageSize
     * @return
     */
    public static int totalPages(int sum, int pageSize) {
        int myTotalPage = (int) (sum / pageSize);
        if ((sum % pageSize > 0) || myTotalPage == 0) {
            myTotalPage++;
        }
        return myTotalPage;
    }

    public void setRecordList(List<T> recordList) {
        this.recordList = recordList;
    }

    public List<T> getRecordList() {
        if (recordList == null) {
            return new ArrayList<T>();
        } else {
            return this.recordList;
        }
    }

    /**
     * 获取当前页包含的实际记录数量
     * 
     * @return
     */
    public int getCurrentRealPageSize() {
        return getRecordList().size();
    }

    public void addRecord(T record) {
        if (recordList == null) {
            recordList = new ArrayList<T>();
        }

        recordList.add(record);
    }

    /**
     * 返回每页的记录数量
     * @return
     */
    public int getCountPerPage() {
        return countPerPage;
    }

    /**
     * 返回当前页码
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    /**
     * 返回总的记录数量
     * @return
     */
    public long getRecordSum() {
        return this.recordSum;
    }

    /**
     * 返回下一页的页码
     * @return
     */
    public int getNextPage() {
        if (currentPage >= totalPage) {
            return totalPage;
        } else {
            return (currentPage + 1);
        }
    }

    /**
     * 获取上一页的页码
     * @return
     */
    public int getPreviousPage() {
        if (currentPage <= 1) {
            return 1;
        } else {
            return (currentPage - 1);
        }
    }

    /**
     * 获取当前页面相关的周围10页的页码列表，如当前也为5，则结果为1,2,3,4,5,6,7,8,9,10
     * 
     * @return
     */
    public List<Integer> getPages() {
        LinkedList<Integer> pages = new LinkedList<Integer>();
        pages.add(getCurrentPage());
        int gap = 1;
        while (pages.size() < 10 && ((currentPage - gap) > 0 || (currentPage + gap) <= totalPage)) {
            if ((currentPage - gap) > 0) {
                pages.addFirst(currentPage - gap);
            }

            if ((currentPage + gap) <= totalPage) {
                pages.addLast(currentPage + gap);
            }

            gap++;
        }

        return pages;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("recordSum=" + this.recordSum);
        sb.append("; totalPage=" + this.getTotalPage());
        sb.append("\n_________________________\n");

        for (T t : getRecordList()) {
            sb.append(t);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 获取该分页记录的记录数位置,第一条记录的位置下标为1
     * 
     * @return
     */
    public int getPosition() {
        return (currentPage - 1) * countPerPage + 1;
    }
    
    /**
     * 获取下一个索引位置，第一个位置为当前记录在所有记录中的位置，每调用一次加1，
     * 在循环使用页面对象时，有时需要访问记录的下标，通过此方法可以完成
     * @return
     */
    public int getNextIndexPosition(){
        if(indexPosition==0) {
            indexPosition = getPosition();
        } else {
            indexPosition++;
        }
        return indexPosition;
    }
    
    private int indexPosition = 0;
}
