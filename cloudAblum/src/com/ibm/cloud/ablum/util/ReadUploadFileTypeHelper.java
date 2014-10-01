package com.ibm.cloud.ablum.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReadUploadFileTypeHelper {

	private static Properties properties = new Properties();
	static {
		try {
			properties.load(ReadUploadFileTypeHelper.class.getResourceAsStream("FileTypeConfig.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Check target file type
	 * 
	 * @param uploadfile
	 * @return
	 */
	public static Boolean readUploadFileType(File uploadfile) {
		if (uploadfile != null && uploadfile.length() > 0) {
			String ext = uploadfile.getName()
					.substring(uploadfile.getName().lastIndexOf(".") + 1)
					.toLowerCase();
			System.out.println("ext->" + ext);
			List<String> allowfiletype = new ArrayList<String>();
			for (Object key : properties.keySet()) {
				String value = (String) properties.get(key);
				System.out.println("key->"+ key.toString() + ", value->" + value);
				String[] values = value.split(",");
				for (String v : values) {
					allowfiletype.add(v.trim());
				}
			}
			//System.out.println("new MimetypesFileTypeMap().getContentType(uploadfile).toLowerCase()->" + new MimetypesFileTypeMap().getContentType(uploadfile).toLowerCase());
			//boolean isAllowedFileType = allowfiletype.contains(new MimetypesFileTypeMap().getContentType(uploadfile).toLowerCase());
			//System.out.println("isAllowedFileType->" + isAllowedFileType);
			boolean isPropertiesContained = properties.keySet().contains(ext);
			//System.out.println("isPropertiesContained->" + isPropertiesContained);
			// "Mime Type of gumby.gif is image/gif"
			//return isAllowedFileType && isPropertiesContained;
			
			return isPropertiesContained;
				
		}
		return true;
	}
	
	/*
	public static void main(String args[]){
		File file = new File("c:\\add_comments.png");
		boolean isFileTypeCorrect = ReadUploadFileTypeHelper.readUploadFileType(file);
		System.out.println("isFileTypeCorrect->" + isFileTypeCorrect);
	}*/

}
