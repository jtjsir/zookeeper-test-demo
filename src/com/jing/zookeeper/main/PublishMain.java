package com.jing.zookeeper.main;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.publish.PublishManager;

/**
 * @author jingsir
 **
 *         程序一启动就会发布文件的入口函数
 */
public class PublishMain {

	public static void main(String[] args) {
		Client zkClient = new Client();
		PublishManager.getInstance().publish(zkClient);
	}

}
