package com.jing.zookeeper.subscribe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.apache.zookeeper.Watcher;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.data.watcher.DataExsitWatcher;
import com.jing.zookeeper.data.watcher.DataUpdateWatcher;
import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.publish.util.ZkNodeUtil;
import com.jing.zookeeper.util.ConfigUtil;

/**
 * @author jingsir
 **
 *         监视启动任务
 */
public class SubscribeTask extends TimerTask {
	// 全路径znode监视器集合
	private Map<String, Watcher> updateWatcherMap = new HashMap<>();
	private Map<String, Watcher> exsitWatcherMap = new HashMap<>();

	private Client zkClient;

	public SubscribeTask(Client client) {
		this.zkClient = client;
	}

	@Override
	public void run() {
		initWatcher();
		initWatch();
	}

	// 初始Watcher
	private void initWatcher() {
		try {
			// 获取全部的文件路径
			ZkNodeUtil.getAllNodesFromZK(zkClient, PathVarConst.ROOTCONF_PATH);
			Set<String> nowKeys = new HashSet<>();
			for (String dir : ZkNodeUtil.allNodesMap.keySet()) {
				if (dir.indexOf(PathVarConst.PATH_KEYWORD[0]) != -1 || dir.indexOf(PathVarConst.PATH_KEYWORD[1]) != -1
						|| dir.indexOf(PathVarConst.PATH_KEYWORD[2]) != -1) {
					List<String> files = ZkNodeUtil.allNodesMap.get(dir);
					for (String file : files) {
						nowKeys.add(dir + "/" + file);
					}

				}
			}
			// 判断是否为初始化
			String nodeType = null;
			int size = updateWatcherMap.size();
			if (size > 0) {
				Set<String> outKeys = ConfigUtil.detectOutData(nowKeys, updateWatcherMap.keySet());
				for (String outKey : outKeys) {
					updateWatcherMap.remove(outKey);
					exsitWatcherMap.remove(outKey);
				}

				for (String inKey : nowKeys) {
					if (!updateWatcherMap.containsKey(inKey)) {
						if (inKey.indexOf("xml") != -1) {
							nodeType = "xml";
						} else if (inKey.indexOf("properties") != -1) {
							nodeType = "properties";
						} else if (inKey.indexOf("default") != -1) {
							nodeType = "default";
						}
						updateWatcherMap.put(inKey, new DataUpdateWatcher(zkClient, inKey, nodeType));
						exsitWatcherMap.put(inKey, new DataExsitWatcher(zkClient, inKey, nodeType));
					}
				}
			} else {
				for (String inKey : nowKeys) {
					if (inKey.indexOf("xml") != -1) {
						nodeType = "xml";
					} else if (inKey.indexOf("properties") != -1) {
						nodeType = "properties";
					} else if (inKey.indexOf("default") != -1) {
						nodeType = "default";
					}
					updateWatcherMap.put(inKey, new DataUpdateWatcher(zkClient, inKey, nodeType));
					exsitWatcherMap.put(inKey, new DataExsitWatcher(zkClient, inKey, nodeType));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 监听打开
	private void initWatch() {
		// 获取线上zk上的所有节点
		try {
			ZkNodeUtil.getAllNodesFromZK(zkClient, PathVarConst.ROOTCONF_PATH);
			for (String keywordChild : ZkNodeUtil.allNodesMap.keySet()) {
				for (int i = 0; i < PathVarConst.PATH_KEYWORD.length; i++) {
					if (keywordChild.indexOf(PathVarConst.PATH_KEYWORD[i]) != -1) {
						// 订阅该目录下的节点
						List<String> childFiles = ZkNodeUtil.allNodesMap.get(keywordChild);
						for (String childFile : childFiles) {
							String nodePath = keywordChild + "/" + childFile;
							zkClient.getZooKeeper().exists(nodePath, exsitWatcherMap.get(nodePath));
							zkClient.getZooKeeper().getData(nodePath, updateWatcherMap.get(nodePath), null);

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
