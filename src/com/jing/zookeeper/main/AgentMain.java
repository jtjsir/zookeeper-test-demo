package com.jing.zookeeper.main;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.data.MonitorAgentRun;

/**
 * @author jingsir
 **
 *         修改数据Main
 */
public class AgentMain {

	public static void main(String[] args) {
		Thread agentThread = new Thread(new MonitorAgentRun(new Client()), "agentThread");
		agentThread.start();
	}
}
