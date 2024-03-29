package com.t4m.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Yuxiang Liao on 2020-06-17 06:01.
 */
public class FileUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	public static boolean checkAndMakeDirectory(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			return dir.mkdirs();
		} else {
			return true;
		}
	}

	public static boolean checkAndMakeDirectory(File dir) {
		if (!dir.exists()) {
			return dir.mkdirs();
		} else {
			return true;
		}
	}

	public static boolean checkDirectory(String dirPath) {
		File dir = new File(dirPath);
		return dir.exists();
	}

	public static boolean checkDirectory(File dir) {
		return dir.exists();
	}


}
