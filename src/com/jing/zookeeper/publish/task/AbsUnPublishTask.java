package com.jing.zookeeper.publish.task;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jing.zookeeper.data.Client;

/**
 * @author jingsir
 **
 *         取消发布任务抽象类
 */
public abstract class AbsUnPublishTask {
	private static final Logger UNPUBLISH_LOGGER = LogManager.getLogger(AbsUnPublishTask.class);

	/**
	 * 取消发布接口
	 * 
	 * @param zkClient
	 *            zk对象
	 * @param unpublishPath
	 *            取消订阅的起始路径
	 */
	public abstract void canclePublish(Client zkClient, String unpublishPath);

	public static Logger getUnpublishLogger() {
		return UNPUBLISH_LOGGER;
	}
}
