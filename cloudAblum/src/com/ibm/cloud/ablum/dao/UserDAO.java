package com.ibm.cloud.ablum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.ibm.cloud.ablum.beans.UserBean;
import com.ibm.cloud.ablum.dao.inf.UserDAOInf;
import com.ibm.cloud.ablum.exceptions.ProfileUpdateFailedException;
import com.ibm.cloud.ablum.exceptions.UserLoginFailedException;
import com.ibm.cloud.ablum.exceptions.UserRegisteredException;
import com.ibm.cloud.ablum.util.Constants;
import com.ibm.cloud.ablum.util.SimplerJDBCHelper;

public class UserDAO implements UserDAOInf {
	//SQL for user's email existed
	private final static String SQL_USER_EXISTED = "select 1 from " + Constants.DB_SCHEMA_NAME + ".USER WHERE USER_EMAIL=? WITH UR";

	//SQL for user register
	private final static String SQL_USER_INSERT = "insert into " + Constants.DB_SCHEMA_NAME + ".USER"
			+ "(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, USER_MOBILE, USER_HOME_PHONE, USER_QQ, USER_NOTE, USER_SELF_DESCRIPTION, USER_IMG_URL)"
		    + "values (?,?,?,?,?,?,?,?,?,?)";

	//SQL for user profile update
	private final static String SQL_USER_UPDATE = "update " + Constants.DB_SCHEMA_NAME + ".USER"
			+ " set USER_PASSWORD = ?, USER_MOBILE = ?, USER_HOME_PHONE = ?, USER_QQ = ?, USER_NOTE = ?, USER_SELF_DESCRIPTION = ?, USER_IMG_URL = ?"
			+ " where USER_EMAIL = ?";

	//SQL for user login
	private final static String SQL_LOGIN = "select * from " + Constants.DB_SCHEMA_NAME + ".USER WHERE USER_EMAIL=? AND USER_PASSWORD=?";
	
	@Override
	public int registerUser(UserBean user) throws UserRegisteredException {
		
		String email = user.getEmail();
		if(isUserExisted(email)){
			throw new UserRegisteredException("Your email address ->" + email+ " was registered, pls login directly!" );
		}
		
		int returnCd = -100;

		//Generate the parameters list
		ArrayList<String> userParamList = new ArrayList<>();
		//Put user_Id;
		userParamList.add(String.valueOf(getNextUserId()));
		userParamList.add(user.getUserName());
		userParamList.add(user.getEmail());
		userParamList.add(user.getPassword());
		userParamList.add(user.getMobile());
		userParamList.add(user.getHomePhoneNum());
		userParamList.add(user.getQQ());
		userParamList.add(user.getNote());
		userParamList.add(user.getSelfIntroduction());
		userParamList.add(user.getImageBluePgUrl());
		
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_USER_INSERT);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(userParamList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		
		return returnCd;
		
	}

	@Override
	public int updateUser(UserBean user) throws ProfileUpdateFailedException {
		int returnCd = -100;

		//Generate the parameters list
		ArrayList<String> userParamList = new ArrayList<>();

		
		userParamList.add(user.getPassword());
		userParamList.add(user.getMobile());
		userParamList.add(user.getHomePhoneNum());
		userParamList.add(user.getQQ());
		userParamList.add(user.getNote());
		userParamList.add(user.getSelfIntroduction());
		userParamList.add(user.getImageBluePgUrl());
		userParamList.add(user.getEmail());
		
		//Define Connection and statement
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_USER_UPDATE);
			conn.setAutoCommit(true);
			returnCd = SimplerJDBCHelper.executeUpdate(userParamList, conn, pstmt);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			//close connections.
			SimplerJDBCHelper.close(conn, pstmt);
		}
		
		return returnCd;
		
	}

	@Override
	/**
	 * login code is not 1, user can't login
	 * only when user name and password are well matched in database, system can allow user login.
	 */
	public UserBean login(String email, String password) throws UserLoginFailedException{
		//Generate the parameters list
		ArrayList<String> userParamList = new ArrayList<>();
		userParamList.add(email);
		userParamList.add(password);
	
		//Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		UserBean user = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_LOGIN);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(userParamList, conn, pstmt);
			
			if(result != null && result.next()){
				user = new UserBean();
				long userId = Long.parseLong(result.getString("USER_ID"));
				String userName = result.getString("USER_NAME");
				String mobile = result.getString("USER_MOBILE");
				String homePhone = result.getString("USER_HOME_PHONE");
				String QQ = result.getString("USER_QQ");
				String note = result.getString("USER_NOTE");
				String selfDesc = result.getString("USER_SELF_DESCRIPTION");
				
				user.setUserId(userId);
				user.setUserName(userName);
				user.setEmail(email);
				user.setMobile(mobile);
				user.setHomePhoneNum(homePhone);
				user.setQQ(QQ);
				user.setNote(note);
				user.setSelfIntroduction(selfDesc);
			} else {
				throw new UserLoginFailedException("Your email and passoword are not matched in the system, please try it again later.");
			}
		}catch(UserLoginFailedException ue){
			throw ue;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			SimplerJDBCHelper.close(conn, pstmt,result);
		}
		
		return user;
	}
	
	public long getNextUserId() {
		//Using the system currentTimeMillis and Random prefix and suffix to ensure ID is generated unique.
		long ranNum1 = (long)(Math.random() * 9);
		long ranNum2 = (long)(Math.random() * 9);
		long ranNum = ranNum1 + ranNum2;
				
		long currentTimeMills = System.currentTimeMillis();
		String nextIdStr = String.valueOf(currentTimeMills) + String.valueOf(ranNum);
		long nextId = Long.parseLong(nextIdStr);
		return nextId;
	}

	@Override
	public boolean isUserExisted(String email) {
		boolean returnCode = false;
			   
		//Generate the parameters list
		ArrayList<String> userParamList = new ArrayList<>();
		userParamList.add(email);
	
		//Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(SQL_USER_EXISTED);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(userParamList, conn, pstmt);
			if(result != null && result.next()){
				returnCode = true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			SimplerJDBCHelper.close(conn, pstmt,result);
		}
		return returnCode;
	
	}

	@Override
	public UserBean querybyEmail(String email) {
		UserBean user = null;
		//Define the SQL
		String sql = "select * from B_ALBUM.USER WHERE USER_EMAIL=? WITH UR";
			   
		//Generate the parameters list
		ArrayList<String> userParamList = new ArrayList<>();
		userParamList.add(email);
	
		//Define Connection, statement and resultSet
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try{
			conn = SimplerJDBCHelper.getConn();
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(true);
			result = SimplerJDBCHelper.query(userParamList, conn, pstmt);
			if(result != null && result.next()){
				user = new UserBean();
				String userName = result.getString("USER_NAME");
				//String userEmail = result.getString("USER_EMAIL");
				String mobile = result.getString("USER_MOBILE");
				String homePhone = result.getString("USER_HOME_PHONE");
				String QQ = result.getString("USER_QQ");
				String note = result.getString("USER_NOTE");
				String selfDesc = result.getString("USER_SELF_DESCRIPTION");
				
				user.setUserName(userName);
				user.setEmail(email);
				user.setMobile(mobile);
				user.setHomePhoneNum(homePhone);
				user.setQQ(QQ);
				user.setNote(note);
				user.setSelfIntroduction(selfDesc);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			SimplerJDBCHelper.close(conn, pstmt,result);
		}
		return user;
	}
	
}
