package com.example.studentmanagement.api;

public class ResponseObject <T>{
	private String status;
	private String message;
	private T retObj;
	
	public ResponseObject() {}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public T getRetObj() {
		return retObj;
	}

	public void setRetObj(T retObj) {
		this.retObj = retObj;
	}
}
