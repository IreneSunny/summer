package team.restore.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import team.restore.conn.DbConnection;
import team.restore.opreate.Store;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DbConnection conn1 = new DbConnection("202.112.114.37","202.112.114.35,202.112.114.36");
		DbConnection conn2 = new DbConnection("202.112.114.33","202.112.114.34,202.112.114.2");
		
		Store s=new Store(conn1.getConf(), conn2.getConf(),
			"webpage2", "webpage");
		s.storeContent();

	}

}
