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

	private String confPath;

	/**
	 * 
	 * @param client
	 *            客户端对象
	 * @param confPath
	 *            配置文件数据存放的路径
	 */
	public DataUpdateWatcher(Client client, String confPath) {
		this.client = client;
		this.confPath = confPath;
	}

	@Override
	public void process(WatchedEvent event) {
		try {
			byte[] data = client.getZooKeeper().getData(this.confPath, null, null);
			getDefaultLogger().info("将修改的结果输出到日志: " + new String(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
