package com.ibm.cloud.ablum.beans;

import java.util.HashMap;
import java.util.Map;

import com.ibm.commons.util.io.json.JsonReference;

public class UserBean extends JsonReference {
	private static final long serialVersionUID = 1L;

	private long userId;
	private String userName = null;
	private String password = null;
	private String email = null;
	private String mobile = null;
	private String homePhoneNum = null;
	private String qq = null;
	private String selfIntroduction = null;
	private String note = null;
	private String userSN = null;
	private String imageBluePgUrl = null;

	public long getUserId() {
		return userId;
	}
	public void setUserId(long ueserId) {
		this.userId = ueserId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getHomePhoneNum() {
		return homePhoneNum;
	}
	public void setHomePhoneNum(String homePhoneNum) {
		this.homePhoneNum = homePhoneNum;
	}
	public String getQQ() {
		return qq;
	}
	public void setQQ(String qq) {
		this.qq = qq;
	}
	public String getSelfIntroduction() {
		return selfIntroduction;
	}
	public void setSelfIntroduction(String selfIntroduction) {
		this.selfIntroduction = selfIntroduction;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getUserSN() {
		return userSN;
	}
	public void setUserSN(String userSN) {
		this.userSN = userSN;
	}
	public String getImageBluePgUrl() {
		return imageBluePgUrl;
	}
	public void setImageBluePgUrl(String imageBluePgUrl) {
		this.imageBluePgUrl = imageBluePgUrl;
	}

	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<>();
		map.put("UserId", String.valueOf(userId));
		map.put("UserName", userName);
		map.put("UserEmail", email);
		map.put("UserQQ", String.valueOf(qq));
		map.put("UserMobile", String.valueOf(mobile));
		//todo: ..
		return map;
	}
}
