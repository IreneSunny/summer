package team.restore.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.util.Bytes;

import team.restore.conn.DbConnection;
import team.restore.opreate.AddWebPage;
import team.restore.util.Signature;
import team.restore.util.WebPage;

public class Write2Hbase {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileName = "/home/sunny/xx.html";
//        FileWriter writer = new FileWriter("/home/sunny/2.txt", true);
//        writer.write(selfReadFile(fileName));
//        writer.close();
        WebPage web = new WebPage();
        web.setUri(ByteBuffer.wrap(Bytes.toBytes("cn.edu.ruc.www:http/")));
        web.setTimestamp(1346169600000L);
        web.setVersion(web.getTimestamp());
        web.setContent(ByteBuffer.wrap(Bytes.toBytes(selfReadFile(fileName))));
        web.setSignature(ByteBuffer.wrap(Signature.calculate(web)));
        web.setContentType("text/html; charset=utf-8");
        web.setTitle(ByteBuffer.wrap(Bytes.toBytes("中国人民大学")));
        DbConnection conn = new DbConnection("202.112.114.37","202.112.114.35,202.112.114.36");
		HTablePool pool = new HTablePool(conn.getConf(), Integer.MAX_VALUE);
		HTable hTable = (HTable) pool.getTable("webpage2");
        AddWebPage awp = new AddWebPage(hTable);
        awp.addRecord(web);
	}

	 public static String selfReadFile(String strFileName){
		 StringBuffer   buf=null; 
		 BufferedReader   breader = null;
		 try {  
		     breader = new BufferedReader(new InputStreamReader(new FileInputStream((strFileName)),Charset.forName("utf-8")));
		     buf = new StringBuffer();  
		     while(breader.ready())    
		         buf.append((char)breader.read());  
		     breader.close();  
		 } catch (Exception e) {  
		     e.printStackTrace();
		 }  
		 return buf.toString();  
	}

}
