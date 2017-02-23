package com.jing.zookeeper.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author jingsir
 **
 *         read properties config
 */
public class ProConfigUtil {

	/**
	 * 
	 * @param configpath
	 *            .properties配置文件classpath路径
	 * @return
	 */
	public static Properties readPro(String configpath) {
		Properties pros = new Properties();
		try {
			InputStream iStream = ProConfigUtil.class.getClassLoader().getResourceAsStream(configpath);
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
			Document xmlDoc = reader.read(ProConfigUtil.class.getClassLoader().getResourceAsStream(xmlPath));
			xmlBuffer.append(xmlDoc.asXML());
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return xmlBuffer.toString();
	}

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

	public static void main(String[] args) {
//		String proStr = ProConfigUtil.readPro("com/jing/zookeeper/resource/quote_conf.properties").toString();
		try {
//			ProConfigUtil.writeToProperties(proStr, new File("test.properties").getAbsolutePath());
			String xmlClassPath = "com/jing/zookeeper/resource/server_addr.xml" ;
			String xmlStr = ProConfigUtil.readXML(xmlClassPath) ;
			System.out.println(xmlStr);
			ProConfigUtil.writeToXML(xmlStr, "test.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
