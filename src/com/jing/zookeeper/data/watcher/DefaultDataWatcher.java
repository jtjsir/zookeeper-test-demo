package com.jing.zookeeper.data.watcher;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @author jingsir
 **
 *         defalut watcher
 */
public class DefaultDataWatcher implements Watcher {

	private static final Logger DEFAULT_LOGGER = LogManager.getLogger("ZookeeperWatcher");

	@Override
	public void process(WatchedEvent event) {
		DEFAULT_LOGGER.info("DefaultEvent happen by: " + event.getPath() + " and the eventType is: " + event.getType());
	}

	public static Logger getDefaultLogger() {
		return DEFAULT_LOGGER;
	}

}
