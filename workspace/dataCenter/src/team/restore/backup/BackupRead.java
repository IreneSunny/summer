package team.restore.backup;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import team.restore.opreate.AddWebPage;
import team.restore.opreate.GetWebPage;
import team.restore.tool.TypeConversion;
import team.restore.util.Signature;
import team.restore.util.WebPage;

public class BackupRead {
	private long startTime;
	private long stopTime;
	private String path;
	private DataInputStream din;
	private HTable hTable;
	
	public BackupRead(long startTime, long stopTime, String path,
			HTable hTable) throws FileNotFoundException {
		super();
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.path = path;
		this.hTable = hTable;
		FileInputStream in=new FileInputStream(this.path+this.startTime+"-"+this.stopTime);  
        BufferedInputStream bin=new BufferedInputStream(in);  
        this.din = new DataInputStream(bin);
	}
	public void readFromFile() throws IOException{
		try {
		    while (true) {
		    	WebPage web = new WebPage();
		    	web.setUri(ByteBuffer.wrap(Bytes.toBytes(din.readUTF())));
				web.setTimestamp(din.readLong());
				web.setVersion(din.readLong());
				AddWebPage awp = new AddWebPage(hTable);
				if(web.getTimestamp()==web.getVersion()){
					StringBuffer sb = new StringBuffer();
					do{
						sb.append(din.readUTF());
					}while(din.readBoolean());
					web.setContent(ByteBuffer.wrap(Bytes.toBytes(sb.toString())));
					web.setSignature(ByteBuffer.wrap(Signature.calculate(web)));
					web.setTitle(ByteBuffer.wrap(Bytes.toBytes(din.readUTF())));
					web.setContentType(din.readUTF());
					awp.addRecord(web);
				} else {
					awp.addSameRecord(web);
				}
					
		    }
		} catch (EOFException e) {
		}
		din.close();
	}
}
