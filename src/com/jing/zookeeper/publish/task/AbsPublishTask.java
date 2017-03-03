package com.jing.zookeeper.publish.task;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.path.PathVarConst;

/**
 * @author jingsir
 **
 *         发布任务抽象接口
 */
public abstract class AbsPublishTask {

	private static final Logger PUBLISH_LOGGER = LogManager.getLogger(AbsPublishTask.class);

	/**
	 * 发布接口
	 */
	public abstract void publish(Client zkClient);

	public Logger getLogger() {
		return PUBLISH_LOGGER;
	}

	/**
	 * 创建节点在zookeeper上
	 * 
	 * @param zkClient
	 *            zk客户端对象
	 * @param znodePath
	 *            znode路径
	 * @param nodeValue
	 *            znode存放值
	 * @throws Exception
	 *             往上层抛异常
	 */
	public void createZnode(Client zkClient, String znodePath, String nodeValue) throws Exception {
		Stat stat = null;
		// 往zk塞数据
		stat = zkClient.getZooKeeper().exists(znodePath, false);
		if (null != stat) {
			// 更新数据
			zkClient.getZooKeeper().setData(znodePath, nodeValue.getBytes(), stat.getVersion());
		} else {
			// 创造数据
			if (znodePath.indexOf(PathVarConst.ROOTCONF_PATH) != -1) {
				// 遍历创造节点
				String createNodePaths = znodePath.substring(
						znodePath.indexOf(PathVarConst.ROOTCONF_PATH) + PathVarConst.ROOTCONF_PATH.length() + 1);
				int index = createNodePaths.indexOf("/");
				String nodePath = PathVarConst.ROOTCONF_PATH;
				while (index != -1) {
					nodePath += "/" + createNodePaths.substring(0, index);
					createNodePaths = createNodePaths.substring(index + 1);
					stat = zkClient.getZooKeeper().exists(nodePath, false);
					if (stat == null) {
						zkClient.getZooKeeper().create(nodePath, nodeValue.getBytes(), Ids.OPEN_ACL_UNSAFE,
								CreateMode.PERSISTENT);
					}

					index = createNodePaths.indexOf("/");
				}
				stat = zkClient.getZooKeeper().exists(nodePath + "/" + createNodePaths, false);
				if (stat == null) {
					zkClient.getZooKeeper().create(nodePath + "/" + createNodePaths, nodeValue.getBytes(),
							Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
			} else {
				throw new RuntimeException(znodePath + "-->节点路径不符合zookeeper约定");
			}
		}
	}
}
