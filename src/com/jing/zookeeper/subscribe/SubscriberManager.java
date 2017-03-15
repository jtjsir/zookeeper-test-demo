package com.jing.zookeeper.subscribe;

import java.util.Timer;
import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.publish.util.ZkNodeUtil;

/**
 * @author jingsir
 **
 *         订阅者管理类,后台启动
 */
public class SubscriberManager {

	private static SubscriberManager instance;

	private SubscriberManager() {

	}

	public static SubscriberManager getInstance() {
		if (instance == null) {
			synchronized (SubscriberManager.class) {
				if (instance == null) {
					instance = new SubscriberManager();
				}
			}
		}

		return instance;
	}

	public void subscribe(Client zkClient) {
		Timer subscribeTimer = new Timer();
		// 1秒后启动，每隔5秒执行一次
		subscribeTimer.schedule(new SubscribeTask(zkClient), 1000, 10000);
	}

	public static void main(String[] args) {
		Client zkClient = new Client();
		try {
			ZkNodeUtil.getAllNodesFromZK(zkClient, PathVarConst.ROOTCONF_PATH);
			for (String key : ZkNodeUtil.allNodesMap.keySet()) {
				System.err.println(">>>>" + key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
