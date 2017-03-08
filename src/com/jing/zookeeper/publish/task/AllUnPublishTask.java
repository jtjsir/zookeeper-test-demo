package com.jing.zookeeper.publish.task;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.publish.util.ZkNodeUtil;

/**
 * @author jingsir
 **
 *         取消全部内容订阅
 */
public class AllUnPublishTask extends AbsUnPublishTask {

	@Override
	public void canclePublish(Client zkClient, String unpublishPath) {
		try {
			ZkNodeUtil.deleteZnode(zkClient, PathVarConst.ROOTCONF_PATH + "/" + PathVarConst.PUBLISH_DIRECTORY);
		} catch (Exception e) {
			getUnpublishLogger().error("全局删除<" + unpublishPath + ">下节点失败", e);
			e.printStackTrace();
		}
	}

}
