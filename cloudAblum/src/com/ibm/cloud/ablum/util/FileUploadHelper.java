package com.ibm.cloud.ablum.util;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.cloud.ablum.beans.UserBean;
import com.ibm.cloud.ablum.dao.PhotoDao;

public class FileUploadHelper {
	private static String uploadPath = null;
	private static String tempPath = null;
	private static File uploadFile = null;
	private static File tempPathFile = null;
	private static int sizeThreshold = 1024;
	private static int sizeMax = 204800;
	
	static {
		sizeMax = Integer.parseInt(FileConstantHelper.getValue("sizeMax"));
		sizeThreshold = Integer.parseInt(FileConstantHelper
				.getValue("sizeThreshold"));
		uploadPath = FileConstantHelper.getValue("uploadPath");
		uploadFile = new File(uploadPath);
		if (!uploadFile.exists()) {
			uploadFile.mkdirs();
		}
		tempPath = FileConstantHelper.getValue("tempPath");
		tempPathFile = new File(tempPath);
		if (!tempPathFile.exists()) {
			tempPathFile.mkdirs();
		}
	}

	/**
	 * 
	 * @param request
	 * @return true 
	 */
	public static boolean upload(HttpServletRequest request, JSONArray json) {
		boolean flag = true;
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if (isMultipart) {
			
			try {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(sizeThreshold); // 
				factory.setRepository(tempPathFile);// 
				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setHeaderEncoding("UTF-8");// 
				upload.setSizeMax(sizeMax);//
				List<FileItem> items = upload.parseRequest(request);
				// 
				if (!checkFileType(items)) {
					return false;
				}

				Iterator<FileItem> itr = items.iterator();//
				PhotoDao dao = new PhotoDao();
				File savedFile = null;
				JSONObject jsono = new JSONObject();
				while (itr.hasNext()) {
					FileItem item = (FileItem) itr.next();//
					if (!item.isFormField()) {// 
						String name = item.getName();// 
						if (name != null) {
							File fullFile = new File(item.getName());
							savedFile = new File(uploadPath, fullFile.getName());
							item.write(savedFile);
							
							jsono.put("name", item.getName());
			                jsono.put("size", item.getSize());
						}
					} else {
						System.out.println(item.getName() + ":" + item.getString("UTF-8"));
					}
				}
				
				UserBean user = (UserBean)request.getSession().getAttribute("current_user");
				
				long photoId = dao.addPhoto(String.valueOf(user.getUserId()), savedFile.getAbsolutePath(), savedFile.getName(), null);
				jsono.put("id", photoId);
				
				
                jsono.put("url", "PhotosServlet?getfile=" + photoId);
                jsono.put("thumbnailUrl", "PhotosServlet?getthumb=" + photoId);
                jsono.put("deleteUrl", "PhotosServlet?delfile=" + photoId);
                jsono.put("deleteType", "GET");
                json.put(jsono);
                System.out.println(json.toString());
			} catch (FileUploadException e) {
				flag = false;
				e.printStackTrace();
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
			}
		} else {
			flag = false;
			System.out.println("the enctype must be multipart/form-data");
		}
		return flag;
	}
//
//	/**
//	 * 
//	 * @param filePath
//	 *            
//	 */
//	public static void deleteFile(String[] filePath) {
//		if (filePath != null && filePath.length > 0) {
//			for (String path : filePath) {
//				String realPath = uploadPath + path;
//				File delfile = new File(realPath);
//				if (delfile.exists()) {
//					delfile.delete();
//				}
//			}
//
//		}
//	}

	/**
	 * @param filePath
	 *          
	 */
	public static void deleteFile(String fileName, JSONArray json) {
		File file = new File(FileConstantHelper.getValue("uploadPath"), fileName);
		
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 
	 * @param items
	 * @return
	 */
	private static Boolean checkFileType(List<FileItem> items) {
		Iterator<FileItem> itr = items.iterator();// 
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();//
			if (!item.isFormField()) {// 
				String name = item.getName();// 
				if (name != null) {
					File fullFile = new File(item.getName());
					boolean isType = ReadUploadFileTypeHelper
							.readUploadFileType(fullFile);
					if (!isType)
						return false;
					break;
				}
			}
		}

		return true;
	}
}
