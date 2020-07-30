package com.t4m.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by Yuxiang Liao on 2020-06-18 00:58.
 */
public class T4mPropertiesUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(T4mPropertiesUtil.class);
	private final String filePath;
	private Properties properties;

	public T4mPropertiesUtil(String filePath) {
		this.filePath = filePath;
		try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
			properties = new Properties();
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("",e);
		}
	}

	/**
	 * 如果该属性不存在，则返回null，如果存在但没有值，则为空""
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * 新建或覆盖原值
	 */
	public void setProperty(String key, String val) {
		properties.setProperty(key, val);
		try {
			try (OutputStream out = new FileOutputStream(new File(filePath))) {
				properties.store(out, String.format("Add property %s=%s", key, val));
				LOGGER.debug("Write property [{}={}] to [{}]", key, val, filePath);
			}
		} catch (Exception e) {
			LOGGER.error("Error happen when storing properties", e);
		}
	}

}
