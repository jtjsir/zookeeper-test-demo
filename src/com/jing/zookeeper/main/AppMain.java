package com.jing.zookeeper.main;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.data.MonitorAppRun;

/**
 * @author jingsir
 **
 *         接收修改数据Main
 */
public class AppMain {

	public static void main(String[] args) throws Exception {
		Client client = new Client();
		MonitorAppRun appRun = new MonitorAppRun(client);
		Thread appThread = new Thread(appRun, "appThread");
		appThread.start();
	}
}
