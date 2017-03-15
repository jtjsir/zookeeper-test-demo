package com.jing.zookeeper.main;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.subscribe.SubscriberManager;

/**
 * @author jingsir
 **
 *         订阅Main函数
 */
public class SubscribeMain {

	public static void main(String[] args) {
		Client client = new Client();
		SubscriberManager.getInstance().subscribe(client);
	}

}
