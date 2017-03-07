package com.jing.zookeeper.publish.task;

import java.util.List;

import org.apache.zookeeper.data.Stat;

import com.jing.zookeeper.data.Client;

/**
 * @author jingsir
 **
 *         取消全部内容订阅
 */
public class AllUnPublishTask extends AbsUnPublishTask {

	@Override
	public void canclePublish(Client zkClient, String unpublishPath) {
		Stat stat = null;
		List<String> childNodes = null;
		try {
			stat = zkClient.getZooKeeper().exists(unpublishPath, false);
			if (null != stat) {
				childNodes = zkClient.getZooKeeper().getChildren(unpublishPath, false);
				if (childNodes != null && childNodes.size() > 0) {
					for (String childNode : childNodes) {
						// 递归遍历
						canclePublish(zkClient, unpublishPath + "/" + childNode);
					}
				}
				zkClient.getZooKeeper().delete(unpublishPath, -1);

			}
		} catch (Exception e) {
			getUnpublishLogger().error("全局删除<" + unpublishPath + ">下节点失败", e);
			e.printStackTrace();
		}
	}

}
