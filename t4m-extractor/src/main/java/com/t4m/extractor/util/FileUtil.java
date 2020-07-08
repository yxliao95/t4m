package com.t4m.extractor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by Yuxiang Liao on 2020-06-17 06:01.
 */
public class FileUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 读取Java源文件内容，以字符串返回。默认文件编码为UTF-8
	 */
	public static String readStringFromJavaSourceFile(String absolutePath) {
		//TODO 考虑文件编码的影响
		String encoding = "UTF-8";
		File file = new File(absolutePath);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try (FileInputStream in = new FileInputStream(file)) {
			in.read(filecontent);
			return new String(filecontent, encoding);
		} catch (FileNotFoundException e) {
			LOGGER.error("Cannot find {}. [{}]", absolutePath, e.toString(), e);
		} catch (IOException e) {
			LOGGER.error("Error happened when retrieving file content. [{}]", e.toString(), e);
		}
		return null;
	}

	public static char[] readCharArrayFromJavaSourceFile(String absolutePath) {
		byte[] in = null;
		try {
			try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(absolutePath))) {
				in = new byte[bufferedInputStream.available()];
				bufferedInputStream.read(in);
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("Cannot find {}. [{}]", absolutePath, e.toString(), e);
		} catch (IOException e) {
			LOGGER.error("Error happened when retrieving file content. [{}]", e.toString(), e);
		}
		return new String(in).toCharArray();
	}

	/**
	 * 判断目录是否存储，如果不存在则创建目录
	 */
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

	public static void main(String[] args) {
		String path =
				"/Users/liao/myProjects/IdeaProjects/t4m/t4m-extractor/src/main/java/com/t4m/extractor/util/JavaFileUtil.java";
		String javaSource = FileUtil.readStringFromJavaSourceFile(path);
		System.out.println(javaSource);
	}
}