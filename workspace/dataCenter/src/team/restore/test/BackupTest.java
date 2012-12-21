package team.restore.test;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;

import team.restore.backup.BackupWrite;
import team.restore.conn.DbConnection;

public class BackupTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DbConnection conn = new DbConnection("202.112.114.37","202.112.114.35,202.112.114.36");
		HTablePool pool = new HTablePool(conn.getConf(), Integer.MAX_VALUE);
		HTable hTable = (HTable) pool.getTable("webpage2");
		BackupWrite bw = new BackupWrite(1351699200000L,"/home/sunny/",hTable);
		bw.write2File();
	}

}
