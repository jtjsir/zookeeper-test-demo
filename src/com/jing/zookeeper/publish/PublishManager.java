package com.jing.zookeeper.publish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.publish.task.AllUnPublishTask;
import com.jing.zookeeper.publish.task.DefaultPublishTask;
import com.jing.zookeeper.publish.task.PropertiesPublishTask;
import com.jing.zookeeper.publish.task.XmlPublishTask;

/**
 * @author jingsir
 **
 *         初始发布文件到zookeeper
 */
public class PublishManager {

	private static final Logger MANAGERLOGGER = LogManager.getLogger(PublishManager.class);

	// properties文件存储集合
	private static HashMap<String, Properties> propertiesMap = new HashMap<>();

	// xml文件存储集合
	private static HashMap<String, Document> xmlsMap = new HashMap<>();

	// 普通文件存储集合
	private static HashMap<String, String> defaultMap = new HashMap<>();

	// 目录节点集合
	public static Set<String> directorySet = new HashSet<String>();

	private static PublishManager instance;

	// 私有化，避免外部初始化
	private PublishManager() {
		initConf();
	}

	// 双重检查
	public static PublishManager getInstance() {
		if (instance == null) {
			synchronized (PublishManager.class) {
				if (instance == null) {
					instance = new PublishManager();
				}
			}
		}

		return instance;
	}

	/**
	 * 初始化，遍历指定目录的所有文件进行存储
	 */
	private void initConf() {
		File zkFile = new File(PathVarConst.PUBLISH_DIRECTORY);
		if (zkFile.exists() && zkFile.isDirectory()) {
			parseDir(zkFile, PathVarConst.ROOTCONF_PATH + "/" + zkFile.getName());
		}
	}

	/**
	 * 发布接口
	 */
	public void publish(Client zkClient) {
		// 启动三个Task类来进行将配置文件部署在zk上
		new DefaultPublishTask(defaultMap).publish(zkClient);
		new XmlPublishTask(xmlsMap).publish(zkClient);
		new PropertiesPublishTask(propertiesMap).publish(zkClient);
	}

	/**
	 * 取消发布内容接口
	 * 
	 * @param zkClient
	 *            zookeeper对象
	 */
	public void unpublish(Client zkClient) {
		new AllUnPublishTask().canclePublish(zkClient,
				PathVarConst.ROOTCONF_PATH + "/" + PathVarConst.PUBLISH_DIRECTORY);
	}

	/**
	 * 目录解析接口
	 * 
	 * @param confDir
	 *            目录文件
	 * @param dirName
	 *            目录名
	 */
	private static void parseDir(File confDir, String dirName) {
		File[] confFiles = confDir.listFiles();
		for (File conf : confFiles) {
			if (conf.isDirectory()) {
				parseDir(conf, dirName + "/" + conf.getName());
			} else {
				parseFile(conf, dirName);
			}
		}
	}

	/**
	 * 文件解析接口
	 * 
	 * @param confFile
	 *            文件
	 * @param zkPath
	 *            对应的znode路径
	 */
	private static void parseFile(File confFile, String zkPath) {
		StringBuilder keyPath = new StringBuilder();
		int keywordIndex = 0;
		try {
			if ((keywordIndex = confFile.getName().indexOf(".properties")) != -1) {
				// properties文件存储
				Properties confPros = new Properties();
				confPros.load(new FileInputStream(confFile));

				keyPath.append(zkPath).append("/properties").append("/")
						.append(confFile.getName().substring(0, keywordIndex));
				propertiesMap.put(keyPath.toString(), confPros);
			} else if ((keywordIndex = confFile.getName().indexOf(".xml")) != -1) {
				// xml文件存储
				Document confDoc = new SAXReader().read(confFile);

				keyPath.append(zkPath).append("/xml").append("/").append(confFile.getName().substring(0, keywordIndex));
				xmlsMap.put(keyPath.toString(), confDoc);
			} else {
				keywordIndex = confFile.getName().lastIndexOf(".");
				// 默认存储
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(confFile)));
				StringBuilder defalutBuf = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					defalutBuf.append(line);
				}

				keyPath.append(zkPath).append("/default").append("/")
						.append(confFile.getName().substring(0, keywordIndex));
				defaultMap.put(keyPath.toString(), defalutBuf.toString());
				reader.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			MANAGERLOGGER.error(e.getMessage(), e);
		}
	}
}
