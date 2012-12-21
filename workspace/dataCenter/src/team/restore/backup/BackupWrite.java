package team.restore.backup;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import team.restore.opreate.GetWebPage;
import team.restore.tool.TypeConversion;
import team.restore.util.WebPage;

/*
 * 备份到文件中
 * uri
 * <timestamp,version>
 * if(timestamp==version){
 * content
 * contentType
 * title
 * }
 * 
 */
public class BackupWrite {
	private final static int MAX_BLOCK=15000;
	private long startTime;
	private long stopTime;
	private String path;
	//private FileWriter fw;
	private DataOutputStream dout;
	private HTable hTable;
	public BackupWrite(long startTime, String path, HTable hTable) throws IOException {
		super();
		this.startTime = startTime;
		this.stopTime = System.currentTimeMillis();
		this.path = path;
		this.hTable = hTable;
		FileOutputStream out=new FileOutputStream(this.path+this.startTime+"-"+this.stopTime);  
        BufferedOutputStream bout=new BufferedOutputStream(out);  
        this.dout=new DataOutputStream(bout);
		//this.fw = new FileWriter(this.path+this.startTime+"-"+this.stopTime+".txt");
	}
	public void write2File(){
		ResultScanner rs = null;
        try{
             Scan s = new Scan();
             s.addColumn("tp".getBytes(), "t".getBytes());
             s.setTimeRange(startTime, stopTime);
             rs = hTable.getScanner(s);
             int done=0;
             int failed=0;
             for(Result r:rs){
                 for(KeyValue kv : r.raw()){
                	 WebPage web = new WebPage();
                	 web.setUri(ByteBuffer.wrap(kv.getRow()));
                     web.setTimestamp(kv.getTimestamp());
                     web.setVersion(Bytes.toLong(kv.getValue()));
                     if(web.getTimestamp()==web.getVersion()){
                    	 GetWebPage gwp = new GetWebPage(hTable);
                    	 web = gwp.getWebPage(web);
                     }
                     if(write2File(web)){
                     	done++;
                     	//System.out.println("get a web"+i+"/"+TypeConversion.decode(web.getUri())+"/"+web.getTimestamp()+"/"+web.getContentType());
                     }else{
                    	 failed++;
                    	 System.out.println("failed:"+web.getUri());
                     }
                     System.out.println(done+" done,"+failed+" failed");
                 }
             }
        } catch (IOException e){
            e.printStackTrace();
        } 
	}
	public boolean write2File(WebPage web){
		try {
			dout.writeUTF(Bytes.toString(web.getUri().array()));
			dout.writeLong(web.getTimestamp());
			dout.writeLong(web.getVersion());
			if(web.getTimestamp()==web.getVersion()){
				try {
					dout.writeUTF(Bytes.toString(web.getContent().array()));
				} catch (UTFDataFormatException e) {
					String sc = Bytes.toString(web.getContent().array());
					int i = 1;
					for (; i < sc.length()/MAX_BLOCK+1; i++) {
					      try {
							dout.writeUTF(sc.substring(MAX_BLOCK*(i-1),MAX_BLOCK*i));
						} catch (UTFDataFormatException e1) {
							dout.writeUTF(sc.substring(MAX_BLOCK*(i-1),MAX_BLOCK*i/2));
							dout.writeBoolean(true);
							dout.writeUTF(sc.substring(MAX_BLOCK*i/2,MAX_BLOCK*i));
						}
					      dout.writeBoolean(true);
					     }
					dout.writeUTF(sc.substring(MAX_BLOCK*(i-1),sc.length()));
				}
			    dout.writeBoolean(false);
				dout.writeUTF(Bytes.toString(web.getTitle().array()));
				dout.writeUTF(web.getContentType());
			}
			
			
//			fw.write(TypeConversion.decode(web.getUri())+"\n");
//			fw.write("<"+web.getTimestamp()+","+web.getVersion()+">\n");
//			if(web.getTimestamp()==web.getVersion()){
//				fw.write(TypeConversion.decode(web.getContent())+"\n");
//				fw.write(web.getContentType()+"\n");
//				fw.write(TypeConversion.decode(web.getTitle())+"\n");
//			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
