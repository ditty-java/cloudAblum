package com.ibm.cloud.ablum.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.cloud.ablum.beans.CategoryBean;
import com.ibm.cloud.ablum.beans.PhotoBean;
import com.ibm.cloud.ablum.beans.UserBean;
import com.ibm.cloud.ablum.dao.PhotoDao;
import com.ibm.cloud.ablum.util.Constants;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet(name = "CategoryServlet", urlPatterns = { "/CategoryServlet" })
public class CategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static String KEY_ACTION = "action";
	private final static String KEY_CATEGORY_NAME = "category_name";
	private final static String KEY_CATEGORY_DESC = "category_desc";
	private final static String KEY_CATEGORY_ID = "category_id";
	private final static String KEY_CATEGORY_PHOTOS = "category_photos";

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CategoryServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserBean user = (UserBean) request.getSession().getAttribute(Constants.SESSION_KEY_CURRENT_USER);

		String action = request.getParameter(KEY_ACTION);

		if (action == null || action.isEmpty()) {
			return;
		}

		PrintWriter writer = response.getWriter();
		response.setContentType("application/json");
		JSONArray json = new JSONArray();

		try {
			PhotoDao dao = new PhotoDao();
			switch (action) {
			case "listCategory":
				List<CategoryBean> categoryList = dao.getCategoriesByUser(user.getUserId());
				for (CategoryBean category : categoryList) {
					JSONObject jsono = new JSONObject();
					jsono.put("CategoryId", category.getCategoryId());
					jsono.put("CategoryName", category.getCategoryName());
					jsono.put("CategoryDesc", category.getCategoryDesc());
					jsono.put("CategoryDate", format.format(category.getCategoryDate()));
					json.put(jsono);
				}
				break;
			case "createCategory":
				/*long categoryId = */dao.addCategory(user.getUserId(), 
						request.getParameter(KEY_CATEGORY_NAME), 
						request.getParameter(KEY_CATEGORY_DESC));
				//CategoryBean category = dao.getCategoryById(user.getUserId(), categoryId);
				break;
			case "removeCategory":
				try {
				dao.removeCategory(user.getUserId(), 
						Long.parseLong(request.getParameter(KEY_CATEGORY_ID)));
				} catch (Exception e) {
					JSONObject jsono = new JSONObject();
					jsono.put("fail", e.getMessage());
				}
				break;
			case "emptyCategory":
				dao.emptyCategory(Long.parseLong(request.getParameter(KEY_CATEGORY_ID)));
				break;
			case "editCategory":
				dao.updateCategory(
						Long.parseLong(request.getParameter(KEY_CATEGORY_ID)),
						request.getParameter(KEY_CATEGORY_NAME),
						request.getParameter(KEY_CATEGORY_DESC));
				break;
			case "listPhotos":
				List<PhotoBean> photoList = dao.getPhotosByCategory(
						Long.parseLong(request.getParameter(KEY_CATEGORY_ID)));
				for (PhotoBean photo : photoList) {
					JSONObject jsono = new JSONObject();
					jsono.put("PhotoId", photo.getPhotoId());
					jsono.put("PhotoURL", "PhotosServlet?getfile=" + photo.getPhotoId());
					jsono.put("PhotoThumbnailURL", "PhotosServlet?getthumb=" + photo.getPhotoId());
					jsono.put("PhotoName", photo.getPhotoName());
					jsono.put("PhotoDesc", photo.getPhotoDesc());
					jsono.put("PhotoDate", format.format(photo.getPhotoDate()));
					json.put(jsono);
				}
				break;
			case "addPhotos":
				String photos = request.getParameter(KEY_CATEGORY_PHOTOS);
				long selectedCategoryId = Long.parseLong(request.getParameter(KEY_CATEGORY_ID));
				String[] photoIds = photos.split(",");
				for (String photoId : photoIds) {
					if (dao.checkContainsPhoto(selectedCategoryId, Long.parseLong(photoId))) {
						continue;
					}
					dao.putPhotoUnderCategory(selectedCategoryId, Long.parseLong(photoId));
				}
				break;
			default:
				// NOP
			}
		} catch(JSONException e) {
			
		} finally {
			writer.write(json.toString());
			writer.close();
		}
	}
}
