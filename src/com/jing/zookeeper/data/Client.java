package com.jing.zookeeper.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.jing.zookeeper.data.watcher.DefaultDataWatcher;
import com.jing.zookeeper.path.PathVarConst;

/**
 * @author jingsir
 **
 *         客户端连接对象
 */
public class Client {

	private static final Logger LOGGER = LogManager.getLogger(Client.class);
	private static final String ZK_CONFIG = "com/jing/zookeeper/resource/zk_conf.properties";
	private static final String ZK_DEFAULT_PORT = "2181";

	/**
	 * address 连接zookeeper服务的字符串 host:port形式，执行多服务，以','分隔
	 * 
	 * client在连接的时候便会去连接其中的某个服务，不成功继续连下个服务直至成功
	 */
	private String address;
	private ZooKeeper zooKeeper;

	public Client() {
		init();
	}

	/**
	 * 建立zk连接，并初始化root和config节点
	 */
	private void init() {
		loadLocalProperties();
		try {
			this.zooKeeper = new ZooKeeper(address, 1000000, new DefaultDataWatcher());
			Stat stat = this.zooKeeper.exists(PathVarConst.ROOT_PATH, null);
			if (stat == null) {
				this.zooKeeper.create(PathVarConst.ROOT_PATH, "root".getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				this.zooKeeper.create(PathVarConst.ROOTCONF_PATH, "config".getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			LOGGER.error("zookeeper连接配置出错，请检查配置文件", e);
		}
	}

	private void loadLocalProperties() {
		Properties zkPros = new Properties();

		// read resource
		InputStream zkResource = Client.class.getClassLoader().getResourceAsStream(ZK_CONFIG);

		try {
			zkPros.load(zkResource);

			address = new StringBuilder().append(zkPros.getProperty("zk.host")).append(":")
					.append(zkPros.getProperty("zk.port", ZK_DEFAULT_PORT)).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 启动监视
	public void startMonitor() {

	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ZooKeeper getZooKeeper() {
		return zooKeeper;
	}

	public void setZooKeeper(ZooKeeper zooKeeper) {
		this.zooKeeper = zooKeeper;
	}
}
