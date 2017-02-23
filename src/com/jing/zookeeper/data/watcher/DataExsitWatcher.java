package com.jing.zookeeper.data.watcher;

import org.apache.zookeeper.WatchedEvent;

/**
 * @author jingsir
 **
 * 
 */
public class DataExsitWatcher extends DefaultDataWatcher {

	@Override
	public void process(WatchedEvent event) {
		// super.process(event);
		getDefaultLogger().info("exsit path: " + event.getPath() + " and the event process is: " + event.getType());
	}

}
