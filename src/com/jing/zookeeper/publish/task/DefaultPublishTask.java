package com.jing.zookeeper.publish.task;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import com.jing.zookeeper.data.Client;

/**
 * @author jingsir
 **
 *         默认发布任务，文件大小不可超过1M
 */
public class DefaultPublishTask extends AbsPublishTask {

	private Map<String, String> defaultMap;

	public DefaultPublishTask(Map<String, String> defaultMap) {
		this.defaultMap = Collections.unmodifiableMap(defaultMap);
	}

	@Override
	public void publish(Client zkClient) {
		Set<Map.Entry<String, String>> defaultEntries = defaultMap.entrySet();
		String zkPath = null;
		String normalValue = null;
		try {
			for (Map.Entry<String, String> normalFile : defaultEntries) {
				zkPath = normalFile.getKey();
				normalValue = normalFile.getValue();
				super.createZnode(zkClient, zkPath, normalValue);
			}

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
