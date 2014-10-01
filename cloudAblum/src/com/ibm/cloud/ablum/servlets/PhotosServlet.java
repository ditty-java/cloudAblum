package com.ibm.cloud.ablum.servlets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.cloud.ablum.beans.PhotoBean;
import com.ibm.cloud.ablum.beans.UserBean;
import com.ibm.cloud.ablum.dao.FriendDao;
import com.ibm.cloud.ablum.dao.PhotoDao;
import com.ibm.cloud.ablum.util.FileUploadHelper;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet(name = "PhotosServlet", urlPatterns = { "/PhotosServlet" })
public class PhotosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PhotosServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserBean user = (UserBean)request.getSession().getAttribute("current_user");

		if (request.getParameter("getfile") != null
				&& !request.getParameter("getfile").isEmpty()) {
			getPhoto(request, response, user);
		} else if (request.getParameter("delfile") != null
				&& !request.getParameter("delfile").isEmpty()) {
			deletePhoto(request, response, user);
		} else if (request.getParameter("getthumb") != null
				&& !request.getParameter("getthumb").isEmpty()) {
			getThumb(request, response, user);
		} else if (request.getParameter("commentfile") != null
				&& !request.getParameter("commentfile").isEmpty()) {
			commentPhoto(request, response, user);
		}  else if (request.getParameter("getall") != null
				&& !request.getParameter("getall").isEmpty()) {
			getPhotoByUser(request, response, user);
		} else {
			PrintWriter writer = response.getWriter();
			writer.write("call POST with multipart form data");
		}
	}
	private void getPhoto(HttpServletRequest request, HttpServletResponse response, UserBean user) throws IOException {
		ServletOutputStream op = null;
		DataInputStream in = null;
		try {
			PhotoDao dao = new PhotoDao();
			long photoId = Long.parseLong(request.getParameter("getfile"));
			PhotoBean photo = dao.getPhotoInfoById(user.getUserId(), photoId);
			File file = new File(photo.getPhotoURL());
			
			if (file.exists()) {
				int bytes = 0;
				op = response.getOutputStream();
				response.setContentType(getMimeType(file));
				response.setContentLength((int) file.length());
				response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
	
				byte[] bbuf = new byte[1024];
				in = new DataInputStream(new FileInputStream(file));
	
				while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
					op.write(bbuf, 0, bytes);
				}
				op.flush();
			}
		} finally {
			if (in != null) in.close();
			if (op != null) {
				op.close();
			}
		}
	}

	private void deletePhoto(HttpServletRequest request, HttpServletResponse response, UserBean user) throws IOException {
		PrintWriter writer = response.getWriter();
		response.setContentType("application/json");
		JSONArray json = new JSONArray();
		try {
			String photoId = request.getParameter("delfile");
			String shareFlg = request.getParameter("shareFlg");
			if (shareFlg.equals("1")) {
				FriendDao dao = new FriendDao();
				dao.removeSharedPhoto(photoId, String.valueOf(user.getUserId()));
			} else {
				PhotoDao dao = new PhotoDao();
				
				dao.removePhoto(String.valueOf(user.getUserId()), photoId);
			}
		} finally {
			writer.write(json.toString());
			writer.close();
		}
	}

	private void getPhotoByUser(HttpServletRequest request, HttpServletResponse response, UserBean user) throws IOException {
		String userId = request.getParameter("getall");
		PrintWriter writer = response.getWriter();
		response.setContentType("application/json");
		JSONArray json = new JSONArray();
		try {
			PhotoDao dao = new PhotoDao();
			List<PhotoBean> photoList = dao.getAllPhotosByUserId(Long.valueOf(userId));
			for (PhotoBean photo : photoList) {
				JSONObject jsono = new JSONObject();
                jsono.put("id", photo.getPhotoId());
                jsono.put("size", 1024);
                jsono.put("name", photo.getPhotoName());
                jsono.put("comment", photo.getPhotoDesc());
                jsono.put("url", "PhotosServlet?getfile=" + photo.getPhotoId());
                jsono.put("thumbnailUrl", "PhotosServlet?getthumb=" + photo.getPhotoId());
                jsono.put("deleteUrl", "PhotosServlet?delfile=" + photo.getPhotoId() + "&shareFlg=" + photo.getShareFlg());
                jsono.put("shareFlg", photo.getShareFlg());
                jsono.put("shareOwner", photo.getShareOwner());
                jsono.put("deleteType", "GET");
                json.put(jsono);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writer.write(json.toString());
			writer.close();
		}
	}

	private void getThumb(HttpServletRequest request, HttpServletResponse response, UserBean user) throws IOException {
		PhotoDao dao = new PhotoDao();
		long photoId = Long.parseLong(request.getParameter("getthumb"));
		PhotoBean photo = dao.getPhotoInfoById(user.getUserId(), photoId);
		File file = new File(photo.getPhotoURL());
		if (file.exists()) {
			System.out.println(file.getAbsolutePath());
			String mimetype = getMimeType(file);
			if (mimetype.endsWith("png") || mimetype.endsWith("jpeg")
					|| mimetype.endsWith("jpg") || mimetype.endsWith("gif")) {
				BufferedImage im = ImageIO.read(file);
				if (im != null) {
					BufferedImage thumb = Scalr.resize(im, 75);
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					if (mimetype.endsWith("png")) {
						ImageIO.write(thumb, "PNG", os);
						response.setContentType("image/png");
					} else if (mimetype.endsWith("jpeg")) {
						ImageIO.write(thumb, "jpg", os);
						response.setContentType("image/jpeg");
					} else if (mimetype.endsWith("jpg")) {
						ImageIO.write(thumb, "jpg", os);
						response.setContentType("image/jpeg");
					} else {
						ImageIO.write(thumb, "GIF", os);
						response.setContentType("image/gif");
					}
					ServletOutputStream srvos = response.getOutputStream();
					response.setContentLength(os.size());
					response.setHeader("Content-Disposition",
							"inline; filename=\"" + file.getName() + "\"");
					os.writeTo(srvos);
					srvos.flush();
					srvos.close();
				}
			}
		}
	}

	private void commentPhoto(HttpServletRequest request, HttpServletResponse response, UserBean user) throws IOException {
		String photoId = request.getParameter("commentfile");
		String photoComment = request.getParameter("pohoto_comment");
		PrintWriter writer = response.getWriter();
		response.setContentType("application/json");
		JSONArray json = new JSONArray();
		try {
			PhotoDao dao = new PhotoDao();
			dao.updatePhotoDesc(String.valueOf(user.getUserId()), photoId, photoComment);
			JSONObject jsono = new JSONObject();
            jsono.put("id", photoId);
            jsono.put("comment", photoComment);
            json.put(jsono);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writer.write(json.toString());
			writer.close();
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("request->" + request.getRequestURL());

		PrintWriter writer = response.getWriter();
		response.setContentType("application/json");
		JSONArray json = new JSONArray();
		try {
			FileUploadHelper.upload(request, json);
		} finally {
			writer.write(json.toString());
			writer.close();
		}
	}
    private String getMimeType(File file) {
        String mimetype = "";
        if (file.exists()) {
            if (getSuffix(file.getName()).equalsIgnoreCase("png")) {
                mimetype = "image/png";
            }else if(getSuffix(file.getName()).equalsIgnoreCase("jpg")){
                mimetype = "image/jpg";
            }else if(getSuffix(file.getName()).equalsIgnoreCase("jpeg")){
                mimetype = "image/jpeg";
            }else if(getSuffix(file.getName()).equalsIgnoreCase("gif")){
                mimetype = "image/gif";
            }else {
                javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
                mimetype  = mtMap.getContentType(file);
            }
        }
        return mimetype;
    }



    private String getSuffix(String filename) {
        String suffix = "";
        int pos = filename.lastIndexOf('.');
        if (pos > 0 && pos < filename.length() - 1) {
            suffix = filename.substring(pos + 1);
        }
        return suffix;
    }
}
