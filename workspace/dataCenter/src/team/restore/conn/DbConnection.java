package team.restore.conn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public class DbConnection {
	private Configuration conf = null;
    
    /**
     * 初始化数据库连接配置
     * 用于伪分布
     * @param ip 主机ip
     */
	public DbConnection(String ip){
		Configuration HBASE_CONFIG = new Configuration();
        HBASE_CONFIG.set("hbase.master", ip+":600000");
        HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
        conf = HBaseConfiguration.create(HBASE_CONFIG);
	}
	
	/*
	 * 初始化数据库连接配置
     * 用于全分布
     * @param ip master的ip
     * @param ip slaver的ip
	 */
	public DbConnection(String ip,String zip){
		Configuration HBASE_CONFIG = new Configuration();
        HBASE_CONFIG.set("hbase.zookeeper.quorum", zip);
        HBASE_CONFIG.set("hbase.master", ip+":600000");
        HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
        conf = HBaseConfiguration.create(HBASE_CONFIG);
	}
	
	/*
	 * 获取连接的configuration对象
	 */
	public Configuration getConf() {
		return conf;
	}

}
