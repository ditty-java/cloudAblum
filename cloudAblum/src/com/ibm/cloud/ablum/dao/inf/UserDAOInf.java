package com.ibm.cloud.ablum.dao.inf;

import com.ibm.cloud.ablum.beans.UserBean;
import com.ibm.cloud.ablum.exceptions.ProfileUpdateFailedException;
import com.ibm.cloud.ablum.exceptions.UserLoginFailedException;
import com.ibm.cloud.ablum.exceptions.UserRegisteredException;

/**
 * Interface method for UserRegister
 * @author dengs
 *
 */
public interface UserDAOInf {
	public int registerUser(UserBean user) throws UserRegisteredException;
	public int updateUser(UserBean user) throws ProfileUpdateFailedException;
	public UserBean login(String email, String password) throws UserLoginFailedException;
	public long getNextUserId();
	public boolean isUserExisted(String email);
	public UserBean querybyEmail(String email);
	
}
