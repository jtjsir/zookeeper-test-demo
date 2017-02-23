package com.jing.zookeeper.main;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.data.watcher.DataExsitWatcher;

/**
 * @author jingsir
 **
 *         test zookeeper operation
 */
public class TestMain {

	public static void main(String[] args) {
		Client client = new Client();
		// register watcher
		Watcher watcher = new DataExsitWatcher();
		try {
			client.getZooKeeper().exists("/root/config/quote_conf", watcher);
			client.getZooKeeper().create("/root/config/quote_conf", "quote_conf".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			client.getZooKeeper().exists("/root/config/quote_conf", true);
			client.getZooKeeper().delete("/root/config/quote_conf", -1);
			
			client.getZooKeeper().close();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
