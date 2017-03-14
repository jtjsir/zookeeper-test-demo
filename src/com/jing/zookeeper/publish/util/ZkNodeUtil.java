package com.jing.zookeeper.publish.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.publish.PublishManager;
import com.jing.zookeeper.util.ConfigUtil;

/**
 * @author jingsir
 **
 *         zk节点工具类
 */
public class ZkNodeUtil {

	// 全部节点map集合，key为目录节点，value为相应节点的子节点
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
	 * 遍历获取所有的节点，保存至map中
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
		for (String alias : PathVarConst.PATH_KEYWORD) {
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
	 * 创建父节点以及节点在zookeeper上/对存在的节点值进行更新
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
	public static void createOrUpdateZnode(Client zkClient, String znodePath, String nodeValue) throws Exception {
		Stat stat = null;
		// 往zk塞数据
		stat = zkClient.getZooKeeper().exists(znodePath, false);
		if (null != stat) {
			// 更新数据
			zkClient.getZooKeeper().setData(znodePath, nodeValue.getBytes(), stat.getVersion());
		} else {
			// 创造数据
			if (znodePath.indexOf(PathVarConst.ROOTCONF_PATH) != -1) {
				// 遍历创造父节点，父节点存的值为自己的全路径
				String createNodePaths = znodePath.substring(
						znodePath.indexOf(PathVarConst.ROOTCONF_PATH) + PathVarConst.ROOTCONF_PATH.length() + 1);
				int index = createNodePaths.indexOf("/");
				String nodePath = PathVarConst.ROOTCONF_PATH;
				while (index != -1) {
					nodePath += "/" + createNodePaths.substring(0, index);
					createNodePaths = createNodePaths.substring(index + 1);
					stat = zkClient.getZooKeeper().exists(nodePath, false);
					if (stat == null) {
						zkClient.getZooKeeper().create(nodePath, nodePath.getBytes(), Ids.OPEN_ACL_UNSAFE,
								CreateMode.PERSISTENT);
					}

					index = createNodePaths.indexOf("/");
				}
				// 最后创建目的节点
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
					deleteZnode(zkClient, znodePath + "/" + childNodePath);
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
	 * 
	 * 
	 * @param client
	 *            zookeeper对象
	 * 
	 * @param caller
	 *            前台调用1/后台调用2
	 * 
	 * 
	 * @return 文件节点内容
	 * 
	 * @throws Exception
	 * 
	 * @comment 前台/客户端调用接口
	 */
	public static String getNodeValueAsString(String znodePath, Client client, int caller) throws Exception {
		StringBuilder resultBuilder = new StringBuilder();
		if (client.getZooKeeper().exists(znodePath, false) != null) {
			if (caller == 1) {
				if (znodePath.indexOf("properties") != -1) {
					resultBuilder.append(PublishManager.getInstance().getPropertiesMap().get(znodePath).toString());
				} else if (znodePath.indexOf("xml") != -1) {
					resultBuilder.append(PublishManager.getInstance().getXmlsMap().get(znodePath).asXML());
				} else if (znodePath.indexOf("default") != -1) {
					resultBuilder.append(PublishManager.getInstance().getDefaultMap().get(znodePath));
				}
			} else if (caller == 2) {
				// properties生成
				if (znodePath.indexOf("properties") != -1) {
					List<String> childs = client.getZooKeeper().getChildren(znodePath, false);
					Properties properties = new Properties();
					for (String child : childs) {
						properties.put(znodePath + "/" + child,
								new String(client.getZooKeeper().getData(znodePath + "/" + child, false, null)));
					}

					resultBuilder.append(properties.toString());
				} else if (znodePath.indexOf("xml") != -1) {
					// xml生成
					List<String> childParams = client.getZooKeeper().getChildren(znodePath, false);
					Document doc = DocumentHelper.createDocument();
					Element rootEle = doc.addElement("root");
					for (String childParam : childParams) {
						Element paramsEle = rootEle.addElement("params");
						paramsEle.addAttribute("name", childParam);
						List<String> params = client.getZooKeeper().getChildren(znodePath + "/" + childParam, false);
						for (String param : params) {
							Element paramEle = paramsEle.addElement("param");
							paramEle.addAttribute("name", param);
							String value = new String(client.getZooKeeper()
									.getData(znodePath + "/" + childParam + "/" + param, false, null));
							paramEle.setText(value);
						}
					}

					resultBuilder.append(doc.asXML());

				} else if (znodePath.indexOf("default") != -1) {
					resultBuilder.append(new String(client.getZooKeeper().getData(znodePath, false, null)));
				}
			}
		}
		return resultBuilder.toString();
	}

	/**
	 * 修改文件数据
	 * 
	 * @param client
	 *            zookeeper对象
	 * @param znodePath
	 *            文件路径,结构为{prefix}/[default,properties,xml]/fileName
	 * @param nodeValue
	 *            修改的数据，可为Document/Properties的string值
	 * @throws Exception
	 * 
	 * @comment 前台调用接口
	 */
	public static void modifyNodeValue(Client client, String znodePath, String nodeValue) throws Exception {
		// 删除原先的数据
		// deleteZnode(client, znodePath);

		if (znodePath.indexOf("properties") != -1) {
			// 返回格式为{key=value}
			Properties nodeProperties = new Properties();
			JsonParser jsonParser = new JsonParser();
			JsonElement elementObj = jsonParser.parse(nodeValue);
			for (Entry<String, JsonElement> proEntry : elementObj.getAsJsonObject().entrySet()) {
				nodeProperties.put(proEntry.getKey(), proEntry.getValue().getAsString());
				// 重新创建节点
				createOrUpdateZnode(client, znodePath + "/" + proEntry.getKey(), proEntry.getValue().getAsString());
			}
			// 删除老key
			Set<String> oldKeys = PublishManager.getInstance().getPropertiesMap().get(znodePath).stringPropertyNames();
			Set<String> outKeys = ConfigUtil.detectOutData(nodeProperties.stringPropertyNames(), oldKeys);
			for (String outKey : outKeys) {
				deleteZnode(client, znodePath + "/" + outKey);
			}

			// 更新到指定的map中
			PublishManager.getInstance().getPropertiesMap().put(znodePath, nodeProperties);

			// 重新更新下主节点的数据，以提醒订阅者更新数据
			createOrUpdateZnode(client, znodePath, znodePath);
		} else if (znodePath.indexOf("xml") != -1) {
			Document xmlResource = DocumentHelper.parseText(nodeValue);
			Element rootEle = xmlResource.getRootElement();
			List<Element> paramsList = rootEle.elements("params");
			for (Element paramEle : paramsList) {
				String paramName = paramEle.attributeValue("name");
				List<Element> paramList = paramEle.elements("param");
				for (Element param : paramList) {
					String paramKey = param.attributeValue("name");
					String content = param.getTextTrim();
					createOrUpdateZnode(client, znodePath + "/" + paramName + "/" + paramKey, content);
				}
			}

			// 删除老key
			Set<String> nowKeys = ConfigUtil.getKeysInXML(xmlResource, znodePath);
			Set<String> oldKeys = ConfigUtil.getKeysInXML(PublishManager.getInstance().getXmlsMap().get(znodePath),
					znodePath);
			Set<String> outKeys = ConfigUtil.detectOutData(nowKeys, oldKeys);
			for (String outKey : outKeys) {
				deleteZnode(client, outKey);
			}
			// 更新
			PublishManager.getInstance().getXmlsMap().put(znodePath, xmlResource);

			// 重新更新下主节点的数据，以提醒订阅者更新数据
			createOrUpdateZnode(client, znodePath, znodePath);
		} else if (znodePath.indexOf("default") != -1) {
			createOrUpdateZnode(client, znodePath, nodeValue);

			// 更新
			PublishManager.getInstance().getDefaultMap().put(znodePath, nodeValue);
		} else {
			throw new RuntimeException("<" + znodePath + ">不符合文件格式，请传入正确的文件格式");
		}

	}
}
