package ruc.summer.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhinang.conf.Configuration;

public class ConfFactory {
	private static Logger LOG = LoggerFactory.getLogger(ConfFactory.class);
	private static Configuration conf = null;

	/**
	 * 获取全局的配置文件
	 * 
	 * @return
	 */
	public static Configuration getConf() {
		if(conf == null) {
            conf = new Configuration();
        }
        return conf;
    }

}
