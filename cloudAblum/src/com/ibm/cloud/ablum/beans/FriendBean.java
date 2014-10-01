package com.ibm.cloud.ablum.beans;

import java.util.Map;

public class FriendBean extends UserBean {
	private static final long serialVersionUID = 1L;

	private boolean addedFlg = false;

	public boolean getAddedFlg() {
		return addedFlg;
	}

	public void setAddedFlg(boolean addedFlg) {
		this.addedFlg = addedFlg;
	}

	public Map<String, String> toMap() {
		Map<String, String> map = super.toMap();
		map.put("AddedFlg", addedFlg ? "1" : "0");
		return map;
	}
}
