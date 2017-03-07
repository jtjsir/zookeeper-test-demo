package com.jing.zookeeper.publish.task;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

import com.jing.zookeeper.data.Client;

/**
 * @author jingsir
 **
 *         xml文件发布任务
 *         <p>
 *         <p>
 *         xml配置文件示例：
 *         <p>
 *         ------
 *         <p>
 *         sample目录下的test.xml
 *         <p>
 *         <code><root>
 *<p>				<params name="testMan">
 *<p>					<param name="firstName">Tian</param>
 *<p>				<param name="lastName">Jing</param>
 *<p>				</params>
 *<p>				<params name="testWoman">
 *<p>					<param name="firstName">Feng</param>
 *<p>					<param name="lastName">Li</param>
 *<p>				</params>
 *<p>			</root></code>
 *         <p>
 *         <p>
 *         ------
 *         <p>
 *         zNode节点
 *         <p>
 *         ------
 *         <p>
 *         /root/config/sample/xml/test/testMan/firstName Tian
 *         <p>
 *         /root/config/sample/xml/test/testMan/lastName Jing
 *         <p>
 *         /root/config/sample/xml/test/testWoman/firstName Feng
 *         <p>
 *         /root/config/sample/xml/test/testWoman/lastName Li
 *         <p>
 *         ------
 */
public class XmlPublishTask extends AbsPublishTask {
	private Map<String, Document> xmlsMap;

	public XmlPublishTask(Map<String, Document> xmlsMap) {
		this.xmlsMap = xmlsMap;
	}

	@Override
	public void publish(Client zkClient) {
		Set<Map.Entry<String, Document>> docEntries = xmlsMap.entrySet();
		Element rootEle = null;
		try {
			for (Map.Entry<String, Document> docEntry : docEntries) {
				String zkPath = docEntry.getKey();
				Document xmlResource = docEntry.getValue();
				rootEle = xmlResource.getRootElement();
				List<Element> paramsList = rootEle.elements("params");
				for (Element paramEle : paramsList) {
					String paramName = paramEle.attributeValue("name");
					List<Element> paramList = paramEle.elements("param");
					for (Element param : paramList) {
						String paramKey = param.attributeValue("name");
						String content = param.getTextTrim();
						super.createZnode(zkClient, zkPath + "/" + paramName + "/" + paramKey, content);
					}

				}
			}

		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
}
