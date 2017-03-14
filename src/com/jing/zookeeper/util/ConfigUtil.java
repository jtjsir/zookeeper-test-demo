package com.jing.zookeeper.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author jingsir
 **
 *         read properties config
 */
public class ConfigUtil {

	/**
	 * 
	 * @param configpath
	 *            .properties配置文件classpath路径
	 * @return
	 */
	public static Properties readPro(String configpath) {
		Properties pros = new Properties();
		try {
			InputStream iStream = ConfigUtil.class.getClassLoader().getResourceAsStream(configpath);
			if (configpath.indexOf(".xml") != -1) {
				// pros.loadFromXML(iStream);
			} else if (configpath.indexOf(".properties") != -1) {
				pros.load(iStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pros;
	}

	/**
	 * string字符串转为.properties配置文件
	 * 
	 * @param properties
	 *            配置文件字符串
	 * @param storePath
	 *            保存文件路径,使用绝对路径
	 * @throws Exception
	 */
	public static void writeToProperties(String properties, String storePath) throws Exception {
		// store
		if (storePath.indexOf(".properties") != -1) {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(properties);
			JsonObject jsonObject = jsonElement.getAsJsonObject();

			Properties pros = new Properties();
			Set<Entry<String, JsonElement>> proObjs = jsonObject.entrySet();
			for (Entry<String, JsonElement> proObj : proObjs) {
				pros.setProperty(proObj.getKey(), proObj.getValue().getAsString());
			}
			pros.store(new FileOutputStream(new File(storePath)), null);
		} else {
			throw new Exception("保存文件路径必须以.properties结尾");
		}
	}

	/**
	 * 
	 * @param xmlPath
	 *            xml配置文件classpath路径
	 * @return xml配置文件内容
	 */
	public static String readXML(String xmlPath) {
		StringBuffer xmlBuffer = new StringBuffer();
		SAXReader reader = new SAXReader();
		try {
			Document xmlDoc = reader.read(ConfigUtil.class.getClassLoader().getResourceAsStream(xmlPath));
			xmlBuffer.append(xmlDoc.asXML());
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return xmlBuffer.toString();
	}

	/**
	 * 输出string为xml文件
	 * 
	 * @param xmlText
	 *            xml内容
	 * @param storePath
	 *            保存路径
	 * @throws Exception
	 */
	public static void writeToXML(String xmlText, String storePath) throws Exception {
		try {
			Document doc = DocumentHelper.parseText(xmlText);
			FileOutputStream xmlOs = new FileOutputStream(new File(storePath));
			byte[] data = xmlText.getBytes();
			xmlOs.write(data, 0, data.length);
			xmlOs.flush();

			xmlOs.close();
		} catch (Exception e) {
			throw new Exception("输入的xml文本有问题或者传入的路径有出入", e);
		}
	}

	/**
	 * 默认输出
	 * 
	 * @param nodeData
	 *            数据
	 * @param storePath
	 *            本地文件路径
	 * @throws Exception
	 */
	public static void writeToDefault(String nodeData, String storePath) throws Exception {
		try {
			FileOutputStream xmlOs = new FileOutputStream(new File(storePath));
			byte[] data = nodeData.getBytes();
			xmlOs.write(data, 0, data.length);
			xmlOs.flush();
			xmlOs.close();
		} catch (Exception e) {
			throw new Exception("写入数据到 <" + storePath + "> 失败", e);
		}
	}

	/**
	 * 抽离出oldKeys与nowKeys不同的key集合
	 * 
	 * @param nowKeys
	 *            新keys集合
	 * @param oldKeys
	 *            老keys集合
	 * @return
	 */
	public static Set<String> detectOutData(Set<String> nowKeys, Set<String> oldKeys) {
		//
		Set<String> outKeys = new HashSet<>();
		int size = nowKeys.size();

		for (String oldKey : oldKeys) {
			int i = 1;
			for (String nowKey : nowKeys) {
				if (!nowKey.equals(oldKey) && i == size) {
					outKeys.add(oldKey);
				} else if (nowKey.equals(oldKey)) {
					break;
				}
				i++;
			}
		}

		return outKeys;
	}

	/**
	 * 从指定的xml文件返回在zookeeper中的key集合
	 * 
	 * @param fromDoc
	 *            源xml文件
	 * @param znodePath
	 *            znode路径前缀
	 * @return
	 */
	public static Set<String> getKeysInXML(Document fromDoc, String znodePath) {
		Set<String> xmlKeys = new HashSet<>();

		Element rootEle = fromDoc.getRootElement();
		List<Element> paramsList = rootEle.elements("params");
		for (Element paramEle : paramsList) {
			String paramName = paramEle.attributeValue("name");
			List<Element> paramList = paramEle.elements("param");
			for (Element param : paramList) {
				String paramKey = param.attributeValue("name");
				xmlKeys.add(znodePath + "/" + paramName + "/" + paramKey);
			}
		}
		return xmlKeys;
	}

	/**
	 * 删除指定的文件
	 * 
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		File deleteFile = new File(filePath);
		if (deleteFile.exists()) {
			deleteFile.delete();
		}
	}

	public static void main(String[] args) {
		Set<String> nowKeys = new HashSet<>();
		nowKeys.add("2");
		nowKeys.add("3");
		nowKeys.add("4");
		nowKeys.add("5");
		nowKeys.add("6");

		Set<String> oldKeys = new HashSet<>();
		oldKeys.add("2");
		oldKeys.add("3");
		oldKeys.add("4");
		oldKeys.add("5");
		oldKeys.add("1");
		oldKeys.add("8");

		Set<String> outKeys = ConfigUtil.detectOutData(nowKeys, oldKeys);
		for (String outKey : outKeys) {
			System.out.println(outKey);
		}
	}
}
