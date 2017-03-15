package com.jing.zookeeper.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jing.zookeeper.path.PathVarConst;

/**
 * @author jingsir
 **
 *         处理zk节点的字符工具类
 */
public class StringUtil {

	/**
	 * 解析node路径为本地的文件全路径
	 * 
	 * 
	 * 示例：/root/test/default/file 解析为本地file的全路径
	 * 
	 * @param nodePath
	 *            必须以PathVarConst.ROOTCONF_PATH为开头
	 * 
	 * 
	 * @return 本地文件的全路径
	 */
	public static String nodePathToFile(String nodePath) {
		String filefullPath = null;
		// 获取目录key
		String dirKeyName = null;
		String fileName = null;
		for (String keyword : PathVarConst.PATH_KEYWORD) {
			if (nodePath.indexOf(keyword) != -1) {
				int keyIndex = nodePath.indexOf(keyword);
				dirKeyName = nodePath.substring(PathVarConst.ROOTCONF_PATH.length() + 1, keyIndex - 1);
				// 获取文件名
				int subIndex = nodePath.lastIndexOf("/");
				if (!keyword.equals("default")) {
					fileName = nodePath.substring(subIndex + 1, nodePath.length()) + "." + keyword;
				} else {
					fileName = nodePath.substring(subIndex + 1, nodePath.length());
				}
			}
		}

		// 从map中获取文件全路径
		Map<String, List<String>> localFilesMap = new HashMap<>();
		localFiles(localFilesMap, PathVarConst.PUBLISH_DIRECTORY);

		List<String> filesList = localFilesMap.get(dirKeyName);
		for (String file : filesList) {
			String simpleName = file.substring(file.lastIndexOf("/") + 1, file.length());
			if (simpleName.indexOf(fileName) != -1 || simpleName.equals(fileName)) {
				filefullPath = file;
			}
		}
		return filefullPath;
	}

	/**
	 * 获取本地的所有文件，示例为: publish-dir/keeper zooKeepers.xml全路径
	 * 
	 * @param localFilesMap
	 *            本地发布包集合
	 * 
	 * 
	 * @param dirName
	 *            目录名
	 */
	public static void localFiles(Map<String, List<String>> localFilesMap, String dirName) {
		File dirFile = new File(dirName);
		try {
			if (dirFile.isDirectory()) {
				File[] files = dirFile.listFiles();
				List<String> fileList = new ArrayList<>();
				for (File file : files) {
					fileList.add(file.getCanonicalPath());
				}

				// 先添加再遍历
				localFilesMap.put(dirName, fileList);

				for (File file : files) {
					localFiles(localFilesMap, dirName + "/" + file.getName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 将新增的zk节点转为本地存储地址
	public static String parsePathToLocal(String zpath) {
		String fullPath = null;
		try {
			String preffix = zpath.substring(
					zpath.indexOf(PathVarConst.PUBLISH_DIRECTORY) + PathVarConst.PUBLISH_DIRECTORY.length() + 1);
			for (String alias : PathVarConst.PATH_KEYWORD) {
				if (preffix.indexOf(alias) != -1) {
					String nodeType = alias;
					if (alias.equals("default")) {
						nodeType = "txt";
					}
					fullPath = new File(
							PathVarConst.PUBLISH_DIRECTORY + "/" + preffix.replace(alias + "/", "") + "." + nodeType)
									.getCanonicalPath();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fullPath;
	}

	public static void main(String[] args) {
		System.err.println(StringUtil.parsePathToLocal("/root/config/publish-dir/keeper/test/xml/test"));
	}
}
