package com.jing.zookeeper.data.watcher;

import java.io.File;

import org.apache.zookeeper.WatchedEvent;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.publish.util.ZkNodeUtil;
import com.jing.zookeeper.util.ConfigUtil;
import com.jing.zookeeper.util.StringUtil;

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

		initConfFile();
	}

	@Override
	public void process(WatchedEvent event) {
		// super.process(event);
		getDefaultLogger().info("exsit path: " + event.getPath() + " and the event process is: " + event.getType());
	}

	// 文件未存在则从zk上down下来
	private void initConfFile() {
		String filePath = null;
		// 本地没有该文件才解析文件路径
		if ((filePath = StringUtil.nodePathToFile(confPath)) == null) {
			filePath = StringUtil.parsePathToLocal(confPath);
		}
		if(filePath!=null){
			File destFile = new File(filePath);
			if (!destFile.exists()) {
				try {
					String fileContent = ZkNodeUtil.getNodeValueAsString(confPath, client, 2);
					if (nodeType.equals("xml")) {
						ConfigUtil.writeToXML(fileContent, filePath);
					} else if (nodeType.equals("default")) {
						ConfigUtil.writeToDefault(fileContent, filePath);
					} else if (nodeType.equals("properties")) {
						ConfigUtil.writeToProperties(fileContent, filePath);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
