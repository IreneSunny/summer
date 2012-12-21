package team.restore.opreate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import team.restore.tool.TypeConversion;
import team.restore.util.Signature;
import team.restore.util.WebPage;

public class GetWebPage {
	private HTable inTable;
	/*
	 * 从inTable取数据，实例化inTable
	 */
	public GetWebPage(HTable inTable) throws IOException {
		super();
		this.inTable = inTable;
		
		
	}
	
	/*
	 * 获取与当前web.signature相同的版本的时间戳，
	 * 有相同的版本则返回其时间戳last_web.version作为web的版本号存入tp:t
	 * @param web必须有web.uri和web.signature值
	 */
	public WebPage getSameVersion(WebPage web){
		try {
			WebPage last_web = new WebPage();
			Get get = new Get(web.getUri().array());
			get.addColumn("c".getBytes(), "sig".getBytes());
			Result r = inTable.get(get);
			for(KeyValue kv : r.raw()){
				int cmp = 0;
			    cmp = Signature.signatureComparator(web.getSignature().array(), kv.getValue());
			    if(cmp == 0){
			    	last_web.setVersion(kv.getTimestamp());
			    	return last_web;
			    }
			 }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 获取web的所有<抓取时间，版本时间>
	 * 抓取时间可用于展示所有留痕点
	 * 版本时间是留痕时得到的内容如果与之前版本一样就把当前web的版本时间设为之前版本的抓取时间，否则和抓取时间一样
	 * 版本时间用于getConType(WebPage web)；getTitle(WebPage web)；getContent(WebPage web)；getWebPage(WebPage web)
	 * @param web必须有web.uri值
	 */
	public HashMap<Long,Long> getAllVersion(WebPage web){
		HashMap<Long,Long> version = new HashMap<Long,Long>();
		try {
			Get get = new Get(web.getUri().array());
			get.addColumn("tp".getBytes(), "t".getBytes());
			Result r = inTable.get(get);
			for(KeyValue kv : r.raw()){
				version.put(kv.getTimestamp(), Bytes.toLong(kv.getValue()));
			 }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	/*
	 * 获取某一天的所有版本
	 */
	public HashMap<Long,Long> getOneVersion(WebPage web,long time){
		HashMap<Long,Long> version = new HashMap<Long,Long>();
		try {
			Get get = new Get(web.getUri().array());
			get.addColumn("tp".getBytes(), "t".getBytes());
			get.setTimeRange(time, time+1000*3600*24);
			Result r = inTable.get(get);
			for(KeyValue kv : r.raw()){
				version.put(kv.getTimestamp(), Bytes.toLong(kv.getValue()));
			 }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	
	/*
	 * 获取web特定版本的content-type
	 * @param web必须有web.uri和web.version值
	 */
	public String getConType(WebPage web){
        try{
            Get get = new Get(web.getUri().array());
            get.addColumn("m".getBytes(), "ct".getBytes());
            get.setTimeStamp(web.getVersion());
            Result r = inTable.get(get);
            for(KeyValue kv : r.raw()){
                 return new String(kv.getValue());
             }
        } catch (IOException e){
            e.printStackTrace();
        }
		return "";
	}
	/*
	 * 获取web特定版本的title
	 * @param web必须有web.uri和web.version值
	 */
	public byte[] getTitle(WebPage web){
        try{
            Get get = new Get(web.getUri().array());
            get.addColumn("m".getBytes(), "t".getBytes());
            get.setTimeStamp(web.getVersion());
            Result r = inTable.get(get);
            for(KeyValue kv : r.raw()){
                 return kv.getValue();
             }
        } catch (IOException e){
            e.printStackTrace();
        }
		return "".getBytes();
	}
	/*
	 * 获取web特定版本的content
	 * @param web必须有web.uri和web.version值
	 */
	public byte[] getContent(WebPage web){
        try{
            Get get = new Get(web.getUri().array());
            get.addColumn("c".getBytes(), "cnt".getBytes());
            get.setTimeStamp(web.getVersion());
            Result r = inTable.get(get);
            for(KeyValue kv : r.raw()){
                 return kv.getValue();
             }
        } catch (IOException e){
            e.printStackTrace();
        }
		return "".getBytes();
	}
	/*
	 * 获取web特定版本的content-type，content，title
	 * @param web必须有web.uri和web.version值
	 */
	public WebPage getWebPage(WebPage web){
		web.setContent(ByteBuffer.wrap(getContent(web)));
		web.setContentType(getConType(web));
		web.setTitle(ByteBuffer.wrap(getTitle(web)));
		return web;
	}
	

}
