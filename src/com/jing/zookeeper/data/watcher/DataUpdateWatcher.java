package com.jing.zookeeper.data.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.publish.util.ZkNodeUtil;
import com.jing.zookeeper.util.ConfigUtil;
import com.jing.zookeeper.util.StringUtil;

/**
 * @author jingsir
 **
 * @since 1.5 数据更新监听器
 */
public class DataUpdateWatcher extends DefaultDataWatcher {

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
	public DataUpdateWatcher(Client client, String confPath, String nodeType) {
		this.client = client;
		this.confPath = confPath;
		this.nodeType = nodeType;
	}

	@Override
	public void process(WatchedEvent event) {
		EventType eventType = event.getType();
		try {
			switch (eventType) {
			case NodeDataChanged:
				String znodeData = ZkNodeUtil.getNodeValueAsString(confPath, client, 2);
				if (nodeType.equals("xml")) {
					ConfigUtil.writeToProperties(znodeData, StringUtil.nodePathToFile(confPath));
				} else if (nodeType.equals("properties")) {
					ConfigUtil.writeToXML(znodeData, StringUtil.nodePathToFile(confPath));
				} else if (nodeType.equals("default")) {
					ConfigUtil.writeToDefault(znodeData, StringUtil.nodePathToFile(confPath));
				}
				getDefaultLogger().info(this.confPath + " 路径在zookeeper上已经被修改");
				break;
			case NodeDeleted:
				ConfigUtil.deleteFile(StringUtil.nodePathToFile(confPath));
				getDefaultLogger().info(this.confPath + " 路径在zookeeper上已经被删除");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
