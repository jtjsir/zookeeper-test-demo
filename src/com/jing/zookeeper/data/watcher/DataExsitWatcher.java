package com.jing.zookeeper.data.watcher;

import org.apache.zookeeper.WatchedEvent;

import com.jing.zookeeper.data.Client;

/**
 * @author jingsir
 **
 * 
 */
public class DataExsitWatcher extends DefaultDataWatcher {

	private Client client;

	private String confPath;

	private String nodeType;

	/**
	 * 
	 * @param client
	 *            客户端对象
	 * @param confPath
	 *            配置文件数据存放的路径
	 */
	public DataExsitWatcher(Client client, String confPath, String nodeType) {
		this.client = client;
		this.confPath = confPath;
		this.nodeType = nodeType;
	}

	@Override
	public void process(WatchedEvent event) {
		// super.process(event);
		getDefaultLogger().info("exsit path: " + event.getPath() + " and the event process is: " + event.getType());
	}

}
