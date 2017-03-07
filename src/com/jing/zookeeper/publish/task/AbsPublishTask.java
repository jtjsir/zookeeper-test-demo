package com.jing.zookeeper.publish.task;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.jing.zookeeper.data.Client;

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
}
