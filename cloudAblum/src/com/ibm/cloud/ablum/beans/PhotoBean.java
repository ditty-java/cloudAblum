package com.ibm.cloud.ablum.beans;

import java.sql.Blob;
import java.sql.Date;

public class PhotoBean {
	public long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getPhotoName() {
		return photoName;
	}
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}
	public String getPhotoDesc() {
		return photoDesc;
	}
	public void setPhotoDesc(String photoDesc) {
		this.photoDesc = photoDesc;
	}
	public Date getPhotoDate() {
		return photoDate;
	}
	public void setPhotoDate(Date photoDate) {
		this.photoDate = photoDate;
	}
	public String getPhotoURL() {
		return photoURL;
	}
	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}
	public Blob getPhotoContent() {
		return photoContent;
	}
	public void setPhotoContent(Blob photoContent) {
		this.photoContent = photoContent;
	}
	public String getShareFlg() {
		return shareFlg;
	}
	public void setShareFlg(String shareFlg) {
		this.shareFlg = shareFlg;
	}
	public String getShareOwner() {
		return shareOwner;
	}
	public void setShareOwner(String shareOwner) {
		this.shareOwner = shareOwner;
	}

	private long photoId;
	private long userId;
	private String photoName;
	private String photoDesc;
	private Date photoDate;
	private String photoURL;
	private Blob photoContent;
	private String shareFlg;
	private String shareOwner;
}
