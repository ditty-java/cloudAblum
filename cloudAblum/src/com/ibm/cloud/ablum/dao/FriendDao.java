package com.ibm.cloud.ablum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.cloud.ablum.beans.FriendBean;
import com.ibm.cloud.ablum.beans.PhotoBean;
import com.ibm.cloud.ablum.dao.inf.FriendsDAOInf;
import com.ibm.cloud.ablum.exceptions.FriendCantAddedException;
import com.ibm.cloud.ablum.exceptions.FriendCantRemovedException;
import com.ibm.cloud.ablum.util.Constants;
import com.ibm.cloud.ablum.util.SimplerJDBCHelper;

public class FriendDao implements FriendsDAOInf {
	// SQL for user/friends search
	private static final String SQL_SEARCH_USERS = 
		"select U.USER_ID, U.USER_NAME, U.USER_EMAIL, U.USER_SELF_DESCRIPTION, U.USER_QQ, U.USER_MOBILE, "
		+ "(CASE WHEN (F.FRIENDS_ID is null) THEN 0 ELSE 1 END) ADDEDFLG "
		+ "from B_ABLUM.USER U left outer join B_ABLUM.FRIENDS F on U.USER_ID=F.FRIENDS_ID and F.USER_ID = ? where U.USER_ID <> ? ";
	// SQL for user/friends search
	private static final String SQL_SEARCH_FRIENDS = 
		"select U.USER_ID, U.USER_NAME, U.USER_EMAIL, U.USER_SELF_DESCRIPTION, U.USER_QQ, U.USER_MOBILE, 1 ADDEDFLG "
		+ "from B_ABLUM.USER U inner join B_ABLUM.FRIENDS F "
		+ "on U.USER_ID=F.FRIENDS_ID where F.USER_ID = ?";
	//
	private static final String SQL_SEARCH_FRIENDS_FETCH = "fetch first ? rows only with ur";
	// SQL for check friend added
	private static final String SQL_FRIEND_ADDED = "select 1 from B_ABLUM.USER U, B_ABLUM.FRIENDS F where U.USER_ID=F.USER_ID  and U.USER_ID=? and F.FRIENDS_ID=?";
	// SQL for add friend
	private static final String SQL_ADD_FRIEND = "insert into B_ABLUM.FRIENDS (USER_ID,FRIENDS_ID) values (?, ?)";
	// SQL for remove friend
	private static final String SQL_REMOVE_FRIEND = "delete from B_ABLUM.FRIENDS where USER_ID=? and FRIENDS_ID=?";
	// SQL for share photo to friend
	private static final String SQL_SHAREPHOTO = "insert into "+ Constants.DB_SCHEMA_NAME + ".SHARE_TO (PHOTO_ID, FRIENDS_ID) values (?,?)";
	// SQL for check photo already shared to friend
	private static final String SQL_CHECK_PHOTOSHARED = "select 1 from "+ Constants.DB_SCHEMA_NAME + ".SHARE_TO where PHOTO_ID =? and FRIENDS_ID=?";
	// SQL for remove shared photo
	private static final String SQL_REMOVE_SHARED_PHOTO = "delete from "+ Constants.DB_SCHEMA_NAME + ".SHARE_TO where PHOTO_ID =? and FRIENDS_ID=?";

	@Override
	public List<Map<String, String>> getFriends(String userId, String rowLimit, boolean allFlg) {
		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(userId));
		
		String sql = SQL_SEARCH_FRIENDS + SQL_SEARCH_FRIENDS_FETCH.replace("?", rowLimit);
		if (allFlg) {
			paramList.add(String.valueOf(userId));
			sql = SQL_SEARCH_USERS + SQL_SEARCH_FRIENDS_FETCH.replace("?", rowLimit);
		}
		
		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;

		List<Map<String, String>> friendList = new ArrayList<>();
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			while (result != null && result.next()) {
				long uId = result.getLong("USER_ID");
				String uName = result.getString("USER_NAME");
				String uEmail = result.getString("USER_EMAIL");
				String uDesc = result.getString("USER_SELF_DESCRIPTION");
				String uQQ = result.getString("USER_QQ");
				String uMobile = result.getString("USER_MOBILE");
				boolean addedFlg = result.getBoolean("ADDEDFLG");

				FriendBean friend = new FriendBean();
				friend.setUserId(uId);
				friend.setUserName(uName);
				friend.setEmail(uEmail);
				friend.setMobile(uMobile);
				friend.setQQ(uQQ);
				friend.setSelfIntroduction(uDesc);
				friend.setAddedFlg(addedFlg);
				friendList.add(friend.toMap());

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}

		return friendList;
	}

	@Override
	public int addFriend(String userId, String friendUserId)
			throws FriendCantAddedException {
		if (isFriendAdded(userId, friendUserId)) {
			throw new FriendCantAddedException(
					"FriendUserId->"
							+ friendUserId
							+ " can't be added to user->"
							+ userId
							+ " as a friend, because the friend was already added to the user.");
		}

		// Generate the parameters list
		ArrayList<String> paramList = new ArrayList<>();
		// Put user_Id;
		paramList.add(userId);
		paramList.add(friendUserId);

		int returnCd = -100;
		// Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_ADD_FRIEND);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}

	@Override
	public boolean isFriendAdded(String userId, String friendUserId) {
		// Generate the parameters list
		ArrayList<String> paramList = new ArrayList<>();
		paramList.add(userId);
		paramList.add(friendUserId);

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		boolean isFriendAdded = false;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_FRIEND_ADDED);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			if (result != null && result.next()) {
				isFriendAdded = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return isFriendAdded;
	}

	@Override
	public int removeFriend(String userId, String friendUserId) throws FriendCantRemovedException {
		if (!isFriendAdded(userId, friendUserId)) {
			throw new FriendCantRemovedException(
					"FriendUserId->"
							+ friendUserId
							+ " can't be removed from user->"
							+ userId
							+ " as a friend, because the friend was already removed from the user.");
		}

		// Generate the parameters list
		ArrayList<String> paramList = new ArrayList<>();
		// Put user_Id;
		paramList.add(userId);
		paramList.add(friendUserId);

		int returnCd = -100;
		// Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_REMOVE_FRIEND);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}

	@Override
	public int sharePhoto2Friend(String photoId, String friendId) {
		// Generate the parameters list
		ArrayList<String> paramList = new ArrayList<>();
        paramList.add(String.valueOf(photoId));
        paramList.add(String.valueOf(friendId));
        
        int returnCd = -100;
		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_SHAREPHOTO);
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
	public int sharePhoto2Friend(String[] photoIds, String friendId) {
		int returnCd = 0;
		if (photoIds == null || photoIds.length == 0) {
			return returnCd;
		}

		for (String photoId : photoIds) {
			if (isPhotoShared(photoId, friendId) == false) {
				returnCd += sharePhoto2Friend(photoId, friendId);
			}
		}

		return returnCd;
	}

	public boolean isPhotoShared(String photoId, String friendUserId) {
		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		paramList.add(String.valueOf(photoId));
		paramList.add(String.valueOf(friendUserId));

		// Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		boolean isPhotoShared = false;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_CHECK_PHOTOSHARED);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(paramList, conn, pstmt);
			if (result != null && result.next()) {
				isPhotoShared = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			SimplerJDBCHelper.close(conn, pstmt, result);
		}
		return isPhotoShared;
	}

	@Override
	public int shareCategory2Friend(String categoryId, String friendId) {
		PhotoDao dao = new PhotoDao();
		List<PhotoBean> photos = dao.getPhotosByCategory(Long.parseLong(categoryId));
		int returnCd = 0;
		for (PhotoBean photo : photos) {
			if (isPhotoShared(String.valueOf(photo.getPhotoId()), friendId) == false) {
				returnCd += sharePhoto2Friend(String.valueOf(photo.getPhotoId()), friendId);
			}
		}

		return returnCd;
	}

	@Override
	public int shareCategory2Friend(String[] categoryIds, String friendId) {
		int returnCd = 0;
		if (categoryIds == null || categoryIds.length == 0) {
			return returnCd;
		}

		for (String categoryId : categoryIds) {
			returnCd += shareCategory2Friend(categoryId, friendId);
		}
		return returnCd;
	}

	@Override
	public int sharePhoto2Friends(String photoId, String[] friendIds) {
		int returnCd = 0;
		if (friendIds == null || friendIds.length == 0) {
			return returnCd;
		}

		for (String friendId : friendIds) {
			returnCd += sharePhoto2Friend(photoId, friendId);
		}
		return returnCd;
	}

	@Override
	public int sharePhoto2Friends(String[] photoIds, String[] friendIds) {
		int returnCd = 0;
		if (friendIds == null || friendIds.length == 0) {
			return returnCd;
		}

		for (String friendId : friendIds) {
			returnCd += sharePhoto2Friend(photoIds, friendId);
		}
		return returnCd;
	}

	@Override
	public int shareCategory2Friends(String[] categoryIds, String[] friendIds) {
		int returnCd = 0;
		if (friendIds == null || friendIds.length == 0) {
			return returnCd;
		}

		for (String friendId : friendIds) {
			returnCd += shareCategory2Friend(categoryIds, friendId);
		}
		return returnCd;
	}

	@Override
	public long getNextId() {
		// Using the system currentTimeMillis and Random prefix and suffix to
		// ensure ID is generated unique.
		long ranNum1 = (long) (Math.random() * 9);
		long ranNum2 = (long) (Math.random() * 9);
		long ranNum = ranNum1 + ranNum2;

		long currentTimeMills = System.currentTimeMillis();
		String nextIdStr = String.valueOf(currentTimeMills)
				+ String.valueOf(ranNum);
		long nextId = Long.parseLong(nextIdStr);
		return nextId;
	}

	public int removeSharedPhoto(String photoId, String friendUserId) {
		if (this.isPhotoShared(photoId, friendUserId) == false) {
			return 0;
		}

		// Generate the parameters list
		List<String> paramList = new ArrayList<>();
		// Put user_Id;
		paramList.add(photoId);
		paramList.add(friendUserId);

		int returnCd = -100;
		// Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_REMOVE_SHARED_PHOTO);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(paramList, conn, pstmt);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		return returnCd;
	}
}
