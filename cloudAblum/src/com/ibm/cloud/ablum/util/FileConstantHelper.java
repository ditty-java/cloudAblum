package com.ibm.cloud.ablum.util;

import java.io.IOException;
import java.util.Properties;

public class FileConstantHelper {

	private static Properties properties = new Properties();
	static {
		try {
			properties.load(ReadUploadFileTypeHelper.class.getResourceAsStream("FileConstantConfig.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getValue(String key) {
		String value = (String) properties.get(key);
		return value.trim();
	}
	/*
	public static void main(String args[]){
		String sizeThrehold = FileConstantHelper.getValue("sizeThreshold");
		System.out.println("sizeThrehold->" + sizeThrehold);
	}*/
}
