package com.ibm.cloud.ablum.exceptions;

public class FriendCantRemovedException extends Exception {
	/***/
	private static final long serialVersionUID = 1L;
	
	String exceptionInfo = null;
	public String getExceptionInfo() {
		return exceptionInfo;
	}
	public void setExceptionInfo(String exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}
	public FriendCantRemovedException(String exception){
		super();
		setExceptionInfo(exception);
	}
}
