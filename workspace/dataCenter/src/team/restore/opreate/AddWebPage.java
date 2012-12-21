package team.restore.opreate;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import team.restore.util.WebPage;

public class AddWebPage {
	private HTable inTable;
	/*
	 * 从inTable取数据，实例化inTable
	 */
	public AddWebPage(HTable inTable) throws IOException {
		super();
		this.inTable = inTable;
	}
	/*
	 * 添加一条完整记录
	 */
	public void addRecord (WebPage web) {
        try {
        	if(web!=null){
        		Put put = new Put(Bytes.toBytes(web.getUri()));
        		if(web.getContent()!=null)put.add(Bytes.toBytes("c"),Bytes.toBytes("cnt"),web.getTimestamp(),Bytes.toBytes(web.getContent()));
        		if(web.getContentType()!=null)put.add(Bytes.toBytes("m"),Bytes.toBytes("ct"),web.getTimestamp(),Bytes.toBytes(web.getContentType()));
        		if(web.getTitle()!=null)put.add(Bytes.toBytes("m"),Bytes.toBytes("t"),web.getTimestamp(),Bytes.toBytes(web.getTitle()));
        		if(web.getSignature()!=null)put.add(Bytes.toBytes("c"),Bytes.toBytes("sig"),web.getTimestamp(),Bytes.toBytes(web.getSignature()));
        		put.add(Bytes.toBytes("tp"),Bytes.toBytes("t"),web.getTimestamp(),Bytes.toBytes(web.getVersion()));
        		inTable.put(put);
        		//System.out.println("insert recored ok.");
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	/*
	 * 该内容已存在，只保存<抓取时间，版本时间>
	 */
	public void addSameRecord (WebPage web) {
        try {
        	if(web!=null){
        		Put put = new Put(Bytes.toBytes(web.getUri()));
        		put.add(Bytes.toBytes("tp"),Bytes.toBytes("t"),web.getTimestamp(),Bytes.toBytes(web.getVersion()));
        		inTable.put(put);
        		//System.out.println("insert same recored ok.");
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
