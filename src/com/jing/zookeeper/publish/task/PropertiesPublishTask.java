package com.jing.zookeeper.publish.task;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.jing.zookeeper.data.Client;

/**
 * @author jingsir
 **
 *         properties文件发布任务
 *<p>         
 *<p>         znode结构示例：
 *<p>         ------
 *<p>         sample目录下的test.properties:
 *<p>         		key1=value1
 *<p>         		key2=value2
 *<p>         		key3=value3
 *<p>         ------
 *<p>         znode节点结果
 *<p>         ------
 *<p>         /root/config/sample/properties/test/key1 value1
 *<p>         /root/config/sample/properties/test/key2 value2
 *<p>         /root/config/sample/properties/test/key3 value3
 *<p>         ------
 *<p>
 */
public class PropertiesPublishTask extends AbsPublishTask {

	private Map<String, Properties> propertiesMap;

	public PropertiesPublishTask(Map<String, Properties> prosMap) {
		this.propertiesMap = prosMap;
	}

	@Override
	public void publish(Client zkClient) {
		Set<Map.Entry<String, Properties>> prosEntries = propertiesMap.entrySet();
		String proPath = null;
		Set<Object> oneProKeys = null;
		try {
			for (Map.Entry<String, Properties> pros : prosEntries) {
				proPath = pros.getKey();
				oneProKeys = pros.getValue().keySet();
				for (Object proKey : oneProKeys) {
					super.createZnode(zkClient, proPath + "/" + (String) proKey,
							pros.getValue().getProperty((String) proKey));
				}
			}

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
