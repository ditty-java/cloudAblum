package com.ibm.cloud.ablum.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ibm.cloud.ablum.beans.CategoryBean;
import com.ibm.cloud.ablum.beans.PhotoBean;
import com.ibm.cloud.ablum.dao.inf.PhotoDaoInf;
import com.ibm.cloud.ablum.exceptions.CategoryCantRemovedException;
import com.ibm.cloud.ablum.util.Constants;
import com.ibm.cloud.ablum.util.FileConstantHelper;
import com.ibm.cloud.ablum.util.SimplerJDBCHelper;

public class PhotoDao implements PhotoDaoInf{
	//SQL for photo insert
	private static final String SQL_ADD_PHOTO = "insert into "+ Constants.DB_SCHEMA_NAME + ".PHOTO_ALBUM (USER_ID, PHOTO_ID, PHOTO_NAME, PHOTO_DESC, P_CONTENT, P_URL, P_DATE_TIME) values (?,?,?,?,?,?,current_timestamp)";
	//SQL for photo delete
	private static final String SQL_REMOVE_PHOTO = "delete from "+ Constants.DB_SCHEMA_NAME + ".PHOTO_ALBUM where USER_ID=? AND PHOTO_ID=?";
	//SQL for photo update comment
	private static final String SQL_UPDATE_PHOTO_DESC = "update "+ Constants.DB_SCHEMA_NAME + ".PHOTO_ALBUM  set PHOTO_DESC = ? where USER_ID=? AND PHOTO_ID=?";
	//SQL for get photo by photoId
	private static final String SQL_GETPHOTO = "select A.PHOTO_ID, PHOTO_NAME, PHOTO_DESC, P_DATE_TIME, P_CONTENT, P_URL " +
			"from " + Constants.DB_SCHEMA_NAME + ".PHOTO_ALBUM A " +
			"left outer join " + Constants.DB_SCHEMA_NAME + ".SHARE_TO B " +
			"on A.PHOTO_ID = B.PHOTO_ID " +
			"where A.PHOTO_ID=? and (A.USER_ID=? or B.FRIENDS_ID=?) with ur";

	//SQL for get all photos by userId
	private static final String SQL_GETPHOTO_USER = "select distinct A.PHOTO_ID, PHOTO_NAME, PHOTO_DESC, P_DATE_TIME, P_URL, case when FRIENDS_ID=? then '1' else '0' end SHARE_FLG, C.USER_EMAIL SHARE_OWNER " +
			"from " + Constants.DB_SCHEMA_NAME + ".PHOTO_ALBUM A " +
			"left outer join " + Constants.DB_SCHEMA_NAME + ".SHARE_TO B " +
			"on A.PHOTO_ID = B.PHOTO_ID " +
			"inner join " +  Constants.DB_SCHEMA_NAME + ".USER C " +
			"on FRIENDS_ID=C.USER_ID " +
			"where (A.USER_ID=? or FRIENDS_ID=?) " +
			"order by SHARE_FLG, A.PHOTO_ID";
	//SQL for list photos of category
	private static final String SQL_GETPHOTO_CATEGORY = 
			"select A.PHOTO_ID, A.PHOTO_NAME, A.PHOTO_DESC, A.P_DATE_TIME, A.P_CONTENT, A.P_URL "
			+ "from "+ Constants.DB_SCHEMA_NAME + ".PHOTO_ALBUM A, "+ Constants.DB_SCHEMA_NAME + ".PHOTO_SHOP B "
			+ "where A.PHOTO_ID = B.PHOTO_ID and "
			+ "B.CATEGORY_ID=? "
			+ "with ur";

	//SQL for get category by categoryId
	private static final String SQL_GETCATEGORY = "SELECT CATEGORY_NAME, CATEGORY_DESC, CATEGORY_DATE, from " + Constants.DB_SCHEMA_NAME + ".CATEGORY where USER_ID=? AND CATEGORY_ID=? with ur";
	//SQL for get all categories by userId
	private static final String SQL_GETCATEGORY_USER = "select CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESC, CATEGORY_DATE from " + Constants.DB_SCHEMA_NAME + ".CATEGORY where USER_ID=? with ur";
	//SQL for add category
	private static final String SQL_ADD_CATEGORY = "insert into "+ Constants.DB_SCHEMA_NAME + ".CATEGORY (CATEGORY_ID, USER_ID, CATEGORY_NAME, CATEGORY_DESC, CATEGORY_DATE) values (?,?,?,?, current_timestamp)";
	//SQL for add category
	private static final String SQL_UPDATE_CATEGORY = "update "+ Constants.DB_SCHEMA_NAME + ".CATEGORY set CATEGORY_NAME=?, CATEGORY_DESC=?, CATEGORY_DATE=current_timestamp where CATEGORY_ID=?";
	//SQL for remove category
	private static final String SQL_REMOVE_CATEGORY = "delete from "+ Constants.DB_SCHEMA_NAME + ".CATEGORY where USER_ID=? AND CATEGORY_ID=?";
	//SQL for push photo into category
	private static final String SQL_PUSHPHOTO = "insert into "+ Constants.DB_SCHEMA_NAME + ".PHOTO_SHOP (CATEGORY_ID, PHOTO_ID) values (?,?)";
	//SQL for remove photo from category
	private static final String SQL_PULLPHOTO = "delete from "+ Constants.DB_SCHEMA_NAME + ".PHOTO_SHOP where CATEGORY_ID=? and PHOTO_ID=?)";
	//SQL for remove photo from category
	private static final String SQL_EMPTY_CATEGORY = "delete from "+ Constants.DB_SCHEMA_NAME + ".PHOTO_SHOP where CATEGORY_ID=?";
	//SQL for check photo contains
	private static final String SQL_CHECK_PHOTO_CONTAINS = "select count(*) CNT from "+ Constants.DB_SCHEMA_NAME + ".PHOTO_SHOP where CATEGORY_ID=? and PHOTO_ID = ?";
	//SQL for check category empty
	private static final String SQL_CHECK_CATEGORY_EMPTY = "select count(*) CNT from "+ Constants.DB_SCHEMA_NAME + ".PHOTO_SHOP where CATEGORY_ID=?";

	@Override
	public long addPhoto(String userId, String path, String photoName, String photoDesc) {
		long photoId = 0;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			photoId = getNextId();
			conn = SimplerJDBCHelper.getConn();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(SQL_ADD_PHOTO);
			pstmt.setString(1, String.valueOf(userId));
			pstmt.setString(2, String.valueOf(photoId));
			pstmt.setString(3, photoName);
			pstmt.setString(4, photoDesc);
			SimplerJDBCHelper.writeBlob(pstmt, 5, path);
			pstmt.setString(6, path);
			SimplerJDBCHelper.executeUpdate(pstmt);
			conn.commit();
			return photoId;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return photoId;
	}
	

	@Override
	public PhotoBean getPhotoInfoById(long userId, long photoId) {
		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(photoId));
		paramList.add(String.valueOf(userId));
		paramList.add(String.valueOf(userId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		PhotoBean pBean = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_GETPHOTO);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			if (result != null && result.next()) {
				long pId = result.getLong("PHOTO_ID");
				String pName = result.getString("PHOTO_NAME");
				String pDesc = result.getString("PHOTO_DESC");
				Date date = result.getDate("P_DATE_TIME");
				Blob blob = result.getBlob("P_CONTENT");
				pBean = new PhotoBean();
				pBean.setPhotoId(pId);
				pBean.setUserId(userId);
				pBean.setPhotoDate(date);
				pBean.setPhotoName(pName);
				pBean.setPhotoDesc(pDesc);
				pBean.setPhotoContent(blob);

				File photoFile = new File(FileConstantHelper.getValue("downloadPath"), String.valueOf(pId)+pName);
				FileOutputStream fout = new FileOutputStream(photoFile);  
	            fout.write(blob.getBytes(1, (int)blob.length()));  
	            fout.flush();
	            fout.close();
	            pBean.setPhotoURL(photoFile.getAbsolutePath());
	            System.out.println("download root:" + FileConstantHelper.getValue("downloadPath"));
	            System.out.println("file saved->" + pBean.getPhotoURL());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return pBean;
	}

	@Override
	public List<PhotoBean> getAllPhotosByUserId(long userId) {
		List<PhotoBean> photoList = new ArrayList<>();
		// Generate the parameters list
		ArrayList<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(userId));
		paramList.add(String.valueOf(userId));
		paramList.add(String.valueOf(userId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		PhotoBean pBean = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_GETPHOTO_USER);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			while (result != null && result.next()) {
				pBean = new PhotoBean();
				pBean.setPhotoId(result.getLong("PHOTO_ID"));
				pBean.setUserId(userId);
				pBean.setPhotoDate(result.getDate("P_DATE_TIME"));
				pBean.setPhotoName(result.getString("PHOTO_NAME"));
				pBean.setPhotoDesc(result.getString("PHOTO_DESC"));
				pBean.setPhotoURL(result.getString("P_URL"));
				pBean.setShareFlg(result.getString("SHARE_FLG"));
				pBean.setShareOwner(result.getString("SHARE_OWNER"));
				photoList.add(pBean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return photoList;
	}

	@Override
	public int removePhoto(String userId, String photoId) {
		
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();
		//Put user_Id;
		paramList.add(userId);
		paramList.add(photoId);
		
		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_REMOVE_PHOTO);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}

	@Override
	public int updatePhotoDesc(String userId, String photoId, String photoComment) {
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();
		//Put user_Id;
		paramList.add(photoComment);
		paramList.add(userId);
		paramList.add(photoId);

		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_UPDATE_PHOTO_DESC);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}

	@Override
	public long addCategory(long userId, String categoryName, String categoryDesc) {
		//Generate the parameters list
		ArrayList<String> paramList = new ArrayList<>();
		//generate categoryId
		long categoryId = getNextId();
		paramList.add(String.valueOf(categoryId));
		paramList.add(String.valueOf(userId));
		paramList.add(categoryName);
		paramList.add(categoryDesc);

		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_ADD_CATEGORY);
			conn.setAutoCommit(true);
			SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
			return categoryId;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return -100;
	}	
	
	@Override
	public CategoryBean getCategoryById(long userId, long categoryId) {
		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(userId));
		paramList.add(String.valueOf(categoryId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		CategoryBean cBean = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_GETCATEGORY);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			if (result != null && result.next()) {
				String categoryName = result.getString("CATEGORY_NAME");
				String categoryDesc = result.getString("CATEGORY_DESC");
				Date categoryDate = result.getDate("CATEGORY_DATE");

				cBean = new CategoryBean();
				cBean.setCategoryId(categoryId);
				cBean.setUserId(userId);
				cBean.setCategoryName(categoryName);
				cBean.setCategoryDesc(categoryDesc);
				cBean.setCategoryDate(categoryDate);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return cBean;
	}

	@Override
	public List<PhotoBean> getPhotosByCategory(long categoryId){
		// Generate the parameters list
		List<String> userParamList = new ArrayList<>();
		userParamList.add(String.valueOf(categoryId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		List<PhotoBean> resultList = new ArrayList<>();
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_GETPHOTO_CATEGORY);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(userParamList, conn, pstmt);
			while (result != null && result.next()) {
				long pId = result.getLong("PHOTO_ID");
				String pName = result.getString("PHOTO_NAME");
				String pDesc = result.getString("PHOTO_DESC");
				Date date = result.getDate("P_DATE_TIME");
				String url = result.getString("P_URL");

				PhotoBean pBean = new PhotoBean();
				pBean.setPhotoId(pId);
				pBean.setPhotoDate(date);
				pBean.setPhotoDesc(pDesc);
				pBean.setPhotoName(pName);
				pBean.setPhotoURL(url);

				resultList.add(pBean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return resultList;
	}
//	@Override
//	public PhotoAlbumBean retrieveAllPhotosWithCategoriesByUserId(long userId) {
//		//Define the SQL
//		List<Long> categoryIds = retrieveUserRelevantCategoryIds(userId);
//		Iterator<Long> iter = categoryIds.iterator();
//		PhotoAlbumBean paBean = new PhotoAlbumBean();
//		paBean.setUserId(userId);
//		Map<Long, List<PhotoBean>> paMap = new HashMap<>();
//		while(iter.hasNext()){
//			long cId = ((Long)iter.next()).longValue();
//			List<PhotoBean> photos = null;//retrievePhotosbyIds(userId, cId);
//			paMap.put(cId, photos);
//		}
//		paBean.setCategoryPhotoList(paMap);
//		return paBean;
//		
//	}
//
//	@Override
//	public List<Long> retrieveUserRelevantCategoryIds(long userId){
//		String sql = "select CR.CATEGORY_ID from CATEGORY_RELEVANT_PHOTO CR WHERE CR.USER_ID=?";
//		// Generate the parameters list
//		List<String> paramList = new ArrayList<>();
//		paramList.add(String.valueOf(userId));
//
//		// Define Connection, statement and resultSet
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet result = null;
//		List<Long> resultList = new ArrayList<>();
//		try {
//			conn = SimplerJDBCHelper.getConn();
//			pstmt = conn.prepareStatement(sql);
//			conn.setAutoCommit(true);
//			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
//			while (result != null && result.next()) {
//				long cId = result.getLong("CATEGORY_ID");
//				resultList.add(Long.valueOf(cId));
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		} finally {
//			SimplerJDBCHelper.close(conn, pstmt, result);
//		}
//		return resultList;
//	}

	
	public long getNextId(){
		//Using the system currentTimeMillis and Random prefix and suffix to ensure ID is generated unique.
		long ranNum1 = (long)(Math.random() * 9);
		long ranNum2 = (long)(Math.random() * 9);
		long ranNum = ranNum1 + ranNum2;
		
		long currentTimeMills = System.currentTimeMillis();
		String nextIdStr = String.valueOf(currentTimeMills) + String.valueOf(ranNum);
		long nextId = Long.parseLong(nextIdStr);
		return nextId;
	}







//	@Override
//	public int renamePhotoById(long userId, long photoId,String newName) {
//		String sql = "UPDATE A_ALBUM.PHOTO_ALBUM SET PHOTO_NAME=? WHERE USER_ID=? AND PHOTO_ID=?";
//		//Generate the parameters list
//		List<String> paramList = new ArrayList<>();
//		//Put user_Id;
//		paramList.add(newName);
//		paramList.add(String.valueOf(userId));
//		paramList.add(String.valueOf(photoId));
//		
//		int returnCd = -100;
//		//Define Connection and statement
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		try{
//			conn = SimplerJDBCHelper.getConn();
//			pstmt = conn.prepareStatement(sql);
//			conn.setAutoCommit(true);
//			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}finally{
//			//close connections.
//			SimplerJDBCHelper.close(conn, pstmt);
//		}
//		return returnCd;
//	}


	@Override
	public int removeCategory(long userId, long categoryId) throws CategoryCantRemovedException {
		//Check Photos added already.
		if (checkCategoryNotEmpty(categoryId)) {
			throw new CategoryCantRemovedException("Category can not be removed because not empty.");
		}
		
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();
		//Put user_Id;
		paramList.add(String.valueOf(userId));
		paramList.add(String.valueOf(categoryId));
		
		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_REMOVE_CATEGORY);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}

	@Override
	public int putPhotoUnderCategory(long categoryId, long photoId){
		
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();
		//Put user_Id;
		paramList.add(String.valueOf(categoryId));
		paramList.add(String.valueOf(photoId));
		
		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_PUSHPHOTO);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}
	@Override
	public int removePhotoFromCategory(long categoryId, long photoId) {
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();
		//Put user_Id;
		paramList.add(String.valueOf(categoryId));
		paramList.add(String.valueOf(photoId));
		
		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_PULLPHOTO);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}

	@Override
	public List<CategoryBean> getCategoriesByUser(long userId) {
		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(userId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		CategoryBean cBean = null;
		List<CategoryBean> categoryList = new ArrayList<>();
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_GETCATEGORY_USER);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			while (result != null && result.next()) {
				long categoryId = result.getLong("CATEGORY_ID");
				String categoryName = result.getString("CATEGORY_NAME");
				String categoryDesc = result.getString("CATEGORY_DESC");
				Date categoryDate = result.getDate("CATEGORY_DATE");

				cBean = new CategoryBean();
				cBean.setCategoryId(categoryId);
				cBean.setUserId(userId);
				cBean.setCategoryName(categoryName);
				cBean.setCategoryDesc(categoryDesc);
				cBean.setCategoryDate(categoryDate);
				categoryList.add(cBean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return categoryList;
	}

	public int emptyCategory(long categoryId) {
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();
		//Put user_Id;
		paramList.add(String.valueOf(categoryId));
		
		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_EMPTY_CATEGORY);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;

	}

	@Override
	public int updateCategory(long categoryId, String categoryName, String categoryDesc) {
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();

		//Put user_Id;
		paramList.add(categoryName);
		paramList.add(categoryDesc);
		paramList.add(String.valueOf(categoryId));

		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_UPDATE_CATEGORY);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}
	@Override
	public boolean checkCategoryNotEmpty(long categoryId) {
		boolean empty = true;
		
		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(categoryId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_CHECK_CATEGORY_EMPTY);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			if (result != null && result.next()) {
				long count = result.getLong("CNT");
				if (count > 0) {
					empty = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return empty;
	}

	@Override
	public boolean checkContainsPhoto(long categoryId, long photoId) {
		boolean contains = false;
		
		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(categoryId));
		paramList.add(String.valueOf(photoId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_CHECK_PHOTO_CONTAINS);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			if (result != null && result.next()) {
				long count = result.getLong("CNT");
				if (count > 0) {
					contains = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return contains;
	}
	@Override
	public int renamePhotoById(long userId, long photoId, String newName) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int renameCategoryById(long userId, long categoryId,String newName) {
		String sql = "UPDATE A_ALBUM.CATEGORY SET CATEGORY_NAME=? WHERE USER_ID=? AND CATEGORY_ID=?";
		//Generate the parameters list
		List<String> paramList = new ArrayList<>();
		//Put user_Id;
		paramList.add(newName);
		paramList.add(String.valueOf(userId));
		paramList.add(String.valueOf(categoryId));
		
		int returnCd = -100;
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}
}
