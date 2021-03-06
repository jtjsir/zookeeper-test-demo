package com.jing.zookeeper.data;

import java.util.Scanner;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.util.ConfigUtil;

/**
 * @author jingsir
 **
 *         create znode and update config
 */
public class MonitorAgentRun implements Runnable {

	private Client client;

	public MonitorAgentRun(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			// 判断根节点是否存在
			Stat stat = client.getZooKeeper().exists(PathVarConst.ROOT_PATH, null);
			if (stat == null) {
				client.getZooKeeper().create(PathVarConst.ROOT_PATH, "root".getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				client.getZooKeeper().create(PathVarConst.ROOTCONF_PATH, "config".getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			} else {
				String quoteconfData = ConfigUtil.readPro("com/jing/zookeeper/resource/quote_conf.properties")
						.toString();
				client.getZooKeeper().create(PathVarConst.QUOTECONF_PATH, quoteconfData.getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL);

				// test
				testCommand(client, PathVarConst.QUOTECONF_PATH);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.getZooKeeper().close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void testCommand(Client client, String childPath) {
		String newData = null;
		try {
			// receive the command
			System.out.println("[quote_conf.properties] content is: "
					+ new String(client.getZooKeeper().getData(childPath, false, null)));
			System.out.println(
					"Please input the command line for [quote_conf.properties] 格式为add[key=value] or update[key=value]");
			Scanner scanner = new Scanner(System.in);
			while (scanner.hasNext()) {
				System.out.println("[quote_conf.properties] content is: "
						+ new String(client.getZooKeeper().getData(childPath, false, null)));
				System.out.println(
						"Please input the command line for [quote_conf.properties] 格式为add[key=value] or update[key=value]");
				String commandLine = scanner.nextLine();
				if (commandLine.indexOf("add") == 0) {
					String originContent = new String(client.getZooKeeper().getData(childPath, false, null));
					String addContent = commandLine.substring(4, commandLine.length() - 1);
					newData = new StringBuffer().append(originContent).deleteCharAt(originContent.length() - 1)
							.append(",").append(addContent).append("}").toString();
					client.getZooKeeper().setData(childPath, newData.getBytes(), -1);
				} else {
					break;
				}
			}

			scanner.close();
		} catch (Exception e) {
		}
	}

}
