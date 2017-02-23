package com.jing.zookeeper.data;

import org.apache.zookeeper.Watcher;
import com.jing.zookeeper.data.watcher.DataUpdateWatcher;

/**
 * @author jingsir
 **
 *         monitor node status
 */
public class MonitorAppRun implements Runnable {

	private static final String ROOT_PATH = "/root";
	private static final String ROOTCONF_PATH = "/root/config";
	private static final String QUOTECONF_PATH = "/root/config/quote_conf";
	private Client client;

	public MonitorAppRun(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			Watcher updateWatcher = new DataUpdateWatcher(client);
			while (true) {
				// 注册数据更新事件
				if (null != this.client.getZooKeeper().exists(QUOTECONF_PATH, null)) {
					byte[] data = this.client.getZooKeeper().getData(QUOTECONF_PATH, updateWatcher, null);
					System.out.println("接收到的数据为: " + new String(data));
					Thread.sleep(10000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
