package team.restore.opreate;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import team.restore.tool.TypeConversion;
import team.restore.util.Signature;
import team.restore.util.WebPage;

public class Store {
	private HTablePool outPool;
	private HTablePool inPool;
	private String outTableName;//nutch的数据库表
	private String inTableName;//新表
	
	public Store(Configuration inconf, Configuration outconf,
			String inTableName, String outTableName) throws IOException {
		super();
		this.outPool = new HTablePool(outconf, Integer.MAX_VALUE);
		this.inPool = new HTablePool(inconf, Integer.MAX_VALUE);
		this.outTableName = outTableName;
		this.inTableName = inTableName;
	}
	
	/*
	 * 转存程序
	 */
	public void storeContent () {
		HTable outTable = null;
		ResultScanner rs = null;
        try{
        	 outTable = (HTable)outPool.getTable(outTableName);
             Scan s = new Scan();
             s.addColumn("f".getBytes(), "cnt".getBytes());
             s.setMaxVersions();
             rs = outTable.getScanner(s);
             int i=0;
             for(Result r:rs){
                 for(KeyValue kv : r.raw()){
                	 WebPage web = new WebPage();
                	 web.setUri(ByteBuffer.wrap(kv.getRow()));
                     web.setContent(ByteBuffer.wrap(kv.getValue()));
                     web.setTimestamp(kv.getTimestamp());
                     web.setSignature(ByteBuffer.wrap(Signature.calculate(web)));
                     GetWebPage gwp = new GetWebPage((HTable)inPool.getTable(inTableName));
                     WebPage last_web = gwp.getSameVersion(web);
                     if(last_web == null){
                    	 web.setContentType(getConType(web.getUri(),web.getTimestamp()));
                    	 web.setTitle(ByteBuffer.wrap(getTitle(web.getUri(),web.getTimestamp())));
                    	 web.setVersion(kv.getTimestamp());
                    	 addRecord(web);
                     } else if(last_web.getVersion()!=web.getTimestamp()) {
                    	 web.setVersion(last_web.getVersion());
                    	 addSameRecord(web);
                     }
                     i++;
                     System.out.println("get a web"+i+"/"+TypeConversion.decode(web.getUri())+"/"+web.getTimestamp()+"/"+web.getContentType());
                 }
             }
        } catch (IOException e){
            e.printStackTrace();
        } 
//        finally {
//        	rs.close();
//        }
    }
	
	/*
	 * 添加一条完整记录
	 */
	public void addRecord (WebPage web) {
        try {
        	HTable inTable = (HTable)inPool.getTable(inTableName);
        	if(web!=null){
        		Put put = new Put(Bytes.toBytes(web.getUri()));
        		if(web.getContent()!=null)put.add(Bytes.toBytes("c"),Bytes.toBytes("cnt"),web.getTimestamp(),Bytes.toBytes(web.getContent()));
        		if(web.getContentType()!=null)put.add(Bytes.toBytes("m"),Bytes.toBytes("ct"),web.getTimestamp(),Bytes.toBytes(web.getContentType()));
        		if(web.getTitle()!=null)put.add(Bytes.toBytes("m"),Bytes.toBytes("t"),web.getTimestamp(),Bytes.toBytes(web.getTitle()));
        		if(web.getSignature()!=null)put.add(Bytes.toBytes("c"),Bytes.toBytes("sig"),web.getTimestamp(),Bytes.toBytes(web.getSignature()));
        		put.add(Bytes.toBytes("tp"),Bytes.toBytes("t"),web.getTimestamp(),Bytes.toBytes(web.getVersion()));
        		inTable.put(put);
        		System.out.println("insert recored ok.");
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
        	HTable inTable = (HTable)inPool.getTable(inTableName);
        	if(web!=null){
        		Put put = new Put(Bytes.toBytes(web.getUri()));
        		put.add(Bytes.toBytes("tp"),Bytes.toBytes("t"),web.getTimestamp(),Bytes.toBytes(web.getVersion()));
        		inTable.put(put);
        		System.out.println("insert same recored ok.");
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	/*
	 * 获取当前处理内容对应content-type
	 */
	public String getConType(ByteBuffer rowKey,long time){
		HTable outTable = null;
        try{
        	outTable = (HTable)outPool.getTable(outTableName);
            Get get = new Get(rowKey.array());
            get.addColumn("h".getBytes(), "Content-Type".getBytes());
            get.setTimeRange(time, time+86400000);
            Result r = outTable.get(get);
            for(KeyValue kv : r.raw()){
                 return new String(kv.getValue());
             }
        } catch (IOException e){
            e.printStackTrace();
        }
		return "";
	}
	/*
	 * 获取当前处理内容对应title
	 */
	public byte[] getTitle(ByteBuffer rowKey,long time){
		HTable outTable = null;
        try{
        	outTable = (HTable)outPool.getTable(outTableName);
            Get get = new Get(rowKey.array());
            get.addColumn("p".getBytes(), "t".getBytes());
            get.setTimeRange(time, time+86400000);
            Result r = outTable.get(get);
            for(KeyValue kv : r.raw()){
                 return kv.getValue();
             }
        } catch (IOException e){
            e.printStackTrace();
        }
		return "".getBytes();
	}
	

}