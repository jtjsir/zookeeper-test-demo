package com.jing.zookeeper.main;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.publish.PublishManager;

/**
 * @author jingsir
 **
 *         取消发布Main函数
 */
public class CanclePublishMain {

	public static void main(String[] args) {
		Client zkClient = new Client();
		PublishManager.getInstance().unpublish(zkClient);
	}

}
