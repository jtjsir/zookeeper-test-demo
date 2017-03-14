package com.jing.zookeeper.path;

/**
 * @author jingsir
 **
 *         zk paths
 */
public class PathVarConst {
	// 根节点
	public static final String ROOT_PATH = "/root";
	// 本应用根节点
	public static final String ROOTCONF_PATH = "/root/config";

	public static final String QUOTECONF_PATH = "/root/config/quote_conf";
	// 发布包名
	public static final String PUBLISH_DIRECTORY = "publish-dir";
	// 节点自定义关键字
	public static final String[] PATH_KEYWORD = new String[] { "default", "properties", "xml" };
}
