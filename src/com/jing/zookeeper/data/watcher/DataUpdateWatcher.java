package com.jing.zookeeper.data.watcher;

import org.apache.zookeeper.WatchedEvent;

import com.jing.zookeeper.data.Client;

/**
 * @author jingsir
 **
 * @since 1.5 数据更新监听器
 */
public class DataUpdateWatcher extends DefaultDataWatcher {

	private Client client;

	public DataUpdateWatcher(Client client) {
		this.client = client;
	}

	@Override
	public void process(WatchedEvent event) {
		try {
			byte[] data = client.getZooKeeper().getData("/root/config/quote_conf", null, null) ;
			getDefaultLogger().info("将修改的结果输出到日志: " + new String(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
