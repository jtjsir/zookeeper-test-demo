package com.jing.zookeeper.data;

import org.apache.zookeeper.Watcher;
import com.jing.zookeeper.data.watcher.DataUpdateWatcher;
import com.jing.zookeeper.path.PathVarConst;

/**
 * @author jingsir
 **
 *         monitor node status
 */
public class MonitorAppRun implements Runnable {

	private Client client;

	public MonitorAppRun(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			Watcher updateWatcher = new DataUpdateWatcher(client, PathVarConst.QUOTECONF_PATH, null);
			while (true) {
				// 注册数据更新事件
				if (null != this.client.getZooKeeper().exists(PathVarConst.QUOTECONF_PATH, null)) {
					byte[] data = this.client.getZooKeeper().getData(PathVarConst.QUOTECONF_PATH, updateWatcher, null);
					System.out.println("接收到的数据为: " + new String(data));
					Thread.sleep(10000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
