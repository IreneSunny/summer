package ruc.summer.storage;

import ruc.summer.storage.dao.KeepPointDao;
import ruc.summer.storage.dao.WebPageDao;
import ruc.summer.storage.dao.core.DaoException;
import ruc.summer.storage.dao.helper.Query;
import ruc.summer.storage.model.KeepPoint;
import ruc.summer.storage.model.WebPage;
import ruc.summer.util.Signature;

import java.util.*;

/**
 * User: xiatian
 * Date: 2/23/13 4:16 PM
 */
public class Storage {
    /**
     * 问题：要不要不要过于频繁的保留一个网页的多个版本？比如一个网站至少间隔1天方可以加入一条新记录
     *
     * @param url
     * @param fetchTime
     * @param metadata
     * @param contentType
     * @param title
     * @param text
     * @param content
     * @param agent
     * @return
     */
    public static Result save(String url, Date fetchTime, Map<String, String> metadata, String contentType, String title, String text, byte[] content, String agent) {
        try{
            String md5 = Signature.getMd5String(url);
            KeepPointDao keepPointDao = DaoFactory.getKeepPointDao();
            WebPageDao webPageDao = DaoFactory.getWebPageDao();
            if(keepPointDao.existsByMd5KeepTime(md5, fetchTime)) {
                return new Result(Result.STATUS_EXISTED, "该记录已经留痕！");
            }

            String signature = Signature.getMd5String(content);
            KeepPoint keepPoint = new KeepPoint(url, fetchTime, signature, agent);
            if(!keepPointDao.existsByMd5Signature(md5, signature)) {
                //insert webpage
                WebPage webPage = new WebPage(url, fetchTime,  title, contentType, text, content, metadata, agent);
                webPageDao.add(webPage);
            }

            //insert keeppoint
            keepPointDao.add(keepPoint);
            return new Result(Result.STATUS_OK, "成功保存到存储库！");
        } catch (DaoException e) {
            e.printStackTrace();
            return new Result(Result.STATUS_ERROR, "保存失败" + e.getMessage());
        }
    }

    /**
     * 获取一个url下面所有的留痕时间点
     * @param url
     * @return
     */
    public static List<Date> getKeepTimeList(String url) {
        List<Date> list = new ArrayList<Date>();
        try {
            KeepPointDao keepPointDao = DaoFactory.getKeepPointDao();
            Query query = Query.create("urlMd5", Signature.getMd5String(url));
            Iterator<KeepPoint> it = keepPointDao.iterator(query);
            while(it.hasNext()) {
                list.add(it.next().getKeepTime());
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static WebPage getWebPage(String url, Date keepTime, boolean nearby) throws DaoException{

        KeepPointDao keepPointDao = DaoFactory.getKeepPointDao();
        WebPageDao webPageDao = DaoFactory.getWebPageDao();

        String urlMd5 = Signature.getMd5String(url);
        KeepPoint point = keepPointDao.getByMd5KeepTime(urlMd5, keepTime);
        if(point == null) {
            if(nearby) {
                point = keepPointDao.getByMd5KeepTime(urlMd5, keepTime, nearby);
                if(point == null) {
                    throw new DaoException("该时间没有保留网页内容！");
                }
            } else {
                throw new DaoException("该时间没有保留网页内容！");
            }
        }

        WebPage page = webPageDao.getByMd5Signature(urlMd5, point.getSignature());
        if(page==null) {
            throw new DaoException("该时间留痕的记录内容不存在，请检查！");
        }

        return page;
    }

}
