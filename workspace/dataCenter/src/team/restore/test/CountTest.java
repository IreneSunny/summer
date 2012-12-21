package team.restore.test;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;

import team.restore.conn.DbConnection;
import team.restore.count.CountDB;

public class CountTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		DbConnection conn = new DbConnection("202.112.114.37","202.112.114.35,202.112.114.36");
//		HTablePool pool = new HTablePool(conn.getConf(), Integer.MAX_VALUE);
//		HTable hTable = (HTable) pool.getTable("webpage2");
//		CountDB c = new CountDB(hTable,"m","ct");
		DbConnection conn = new DbConnection("202.112.114.33","202.112.114.34,202.112.114.2");
		HTablePool pool = new HTablePool(conn.getConf(), Integer.MAX_VALUE);
		HTable hTable = (HTable) pool.getTable("webpage");
		CountDB c = new CountDB(hTable,"h","Content-Type");
		c.printMap(c.countByType());
		

	}

}
