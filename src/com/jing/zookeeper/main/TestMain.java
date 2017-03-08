package com.jing.zookeeper.main;

import com.jing.zookeeper.data.Client;
import com.jing.zookeeper.path.PathVarConst;
import com.jing.zookeeper.publish.util.ZkNodeUtil;

/**
 * @author jingsir
 **
 *         test zookeeper operation
 */
public class TestMain {

	public static void main(String[] args) {
		Client zkClient = new Client();
		try {
			ZkNodeUtil.deleteZnode(zkClient, PathVarConst.ROOTCONF_PATH + "/" + PathVarConst.PUBLISH_DIRECTORY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
