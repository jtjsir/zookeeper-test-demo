package com.jing.zookeeper.publish.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.publish.PublishManager;

/**
 * @author jingsir
 **
 *         zk节点工具类
 */
public class ZkNodeUtil {

	// 文件前缀集合
	public static String[] ALIASCONST = new String[] { "properties", "xml", "default" };

	// 全部节点map集合
	public static Map<String, List<String>> allNodesMap = new HashMap<>();

	/**
	 * 获取节点路径下的子节点
	 * 
	 * @param rootPath
	 *            节点路径
	 * @param zkClient
	 *            zk对象
	 * @return
	 * @throws Exception
	 */
	public static List<String> getNodesFromZK(String rootPath, Client zkClient) throws Exception {
		List<String> childNodes = null;
		try {
			Stat stat = zkClient.getZooKeeper().exists(rootPath, false);
			if (stat != null) {
				childNodes = zkClient.getZooKeeper().getChildren(rootPath, false);
			}

		} catch (Exception e) {
			throw new Exception(rootPath + " 节点不存在", e);
		}
		return childNodes;
	}

	/**
	 * 遍历获取所有的节点
	 * 
	 * @param zkClient
	 *            zookpeer对象
	 * @param startPath
	 *            根节点
	 * @throws Exception
	 */
	public static void getAllNodesFromZK(Client zkClient, String startPath) throws Exception {
		// 非指定节点计数
		int notAliasConst = 0;
		List<String> childNodes = null;
		for (String alias : ALIASCONST) {
			if (startPath.indexOf(alias) != -1) {
				childNodes = getNodesFromZK(startPath, zkClient);
				allNodesMap.put(startPath, childNodes);

				break;
			} else {
				notAliasConst++;
			}
		}
		if (notAliasConst >= 3) {
			childNodes = getNodesFromZK(startPath, zkClient);
			allNodesMap.put(startPath, childNodes);
			for (String child : childNodes) {
				getAllNodesFromZK(zkClient, startPath + "/" + child);
			}
		}

	}

	/**
	 * 创建父节点以及节点在zookeeper上
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
	public static void createZnode(Client zkClient, String znodePath, String nodeValue) throws Exception {
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

	/**
	 * 删除节点以及子节点在zookeeper上
	 * 
	 * @param zkClient
	 *            zk客户端对象
	 * @param znodePath
	 *            znode路径
	 * @throws Exception
	 *             往上层抛异常
	 */
	public static void deleteZnode(Client zkClient, String znodePath) throws Exception {
		Stat stat = null;
		List<String> childNodes = null;
		// 多份节点检查
		stat = zkClient.getZooKeeper().exists(znodePath, false);
		if (null != stat) {
			childNodes = zkClient.getZooKeeper().getChildren(znodePath, false);
			if (null != childNodes && childNodes.size() > 0) {
				for (String childNodePath : childNodes) {
					zkClient.getZooKeeper().delete(znodePath + "/" + childNodePath, -1);
				}
			}
			zkClient.getZooKeeper().delete(znodePath, stat.getVersion());
		}

	}

	/**
	 * 获取文件节点的文件内容
	 * 
	 * @param znodePath
	 *            节点路径
	 * @param client
	 *            zookeeper对象
	 * @return 文件节点内容
	 * 
	 * @throws Exception
	 */
	public static String getNodeValueAsString(String znodePath, Client client) throws Exception {
		StringBuilder resultBuilder = new StringBuilder();
		if (client.getZooKeeper().exists(znodePath, false) != null) {
			if (znodePath.indexOf("properties") != -1) {
				resultBuilder.append(PublishManager.getInstance().getPropertiesMap().get(znodePath).toString());
			} else if (znodePath.indexOf("xml") != -1) {
				resultBuilder.append(PublishManager.getInstance().getXmlsMap().get(znodePath).asXML());
			} else if (znodePath.indexOf("default") != -1) {
				resultBuilder.append(PublishManager.getInstance().getDefaultMap().get(znodePath));
			}
		}
		return resultBuilder.toString();
	}

}
