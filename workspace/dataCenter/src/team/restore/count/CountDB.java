package team.restore.count;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import team.restore.opreate.GetWebPage;
import team.restore.tool.TypeConversion;
import team.restore.util.Signature;
import team.restore.util.WebPage;

public class CountDB {
	private HTable inTable;
	private String column;
	private String quality;
	public CountDB(HTable inTable, String column, String quality) throws IOException {
		super();
		this.inTable = inTable;
		this.column = column;
		this.quality = quality;
	}
	public HashMap<String,Integer> countByType(){
		HashMap<String,Integer> sum = new HashMap<String,Integer>();
		ResultScanner rs = null;
        try{
             Scan s = new Scan();
             s.addColumn(column.getBytes(), quality.getBytes());
             rs = inTable.getScanner(s);
             for(Result r:rs){
                 for(KeyValue kv : r.raw()){
                	 String str = new String(kv.getValue(),"gb2312");
                	 if(str.contains("image"))str="image";
                	 else if(str.contains("text/html"))str="text/html";
                	 else if(str.contains("javascript"))str="javascript";
                	 else if(str.contains("css"))str="css";
                	 else str="other";
                	 if(sum.containsKey(str)){
                		 int i=sum.get(str);
                		 i++;
                		 sum.put(str, i);
                	 } else {
                		 sum.put(str, 1);
                		 System.out.println(str);
                	 }
                 }
             }
        } catch (IOException e){
            e.printStackTrace();
        } 
        return sum;
	}
	public void printMap(HashMap<String,Integer> map){
		Iterator iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}

}
