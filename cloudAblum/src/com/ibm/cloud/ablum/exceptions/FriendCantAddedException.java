package com.ibm.cloud.ablum.exceptions;

public class FriendCantAddedException extends Exception {
	/***/
	private static final long serialVersionUID = 1L;
	String exceptionInfo = null;
	public String getExceptionInfo() {
		return exceptionInfo;
	}
	public void setExceptionInfo(String exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}
	public FriendCantAddedException(String exception){
		super();
		setExceptionInfo(exception);
	}
}
