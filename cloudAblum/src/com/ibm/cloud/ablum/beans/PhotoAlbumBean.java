package com.ibm.cloud.ablum.beans;


import java.util.Map;

public class PhotoAlbumBean {
	private long userId;
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	private Map categoryPhotosMap;

	public Map getCategoryPhotoList() {
		return categoryPhotosMap;
	}

	public void setCategoryPhotoList(Map categoryPhotosMap) {
		this.categoryPhotosMap = categoryPhotosMap;
	}
	
	
}
