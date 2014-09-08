package com.rjmetrics.byodb.helpers;


public enum StatusCode {

	RESOURCE_NOT_FOUND(404), EXCEPTION_OCCURRED(500), NO_DATA_AVAILABLE(204), BAD_REQUEST(400);
	
	private  int code ;
	
	private StatusCode(int statusCode){
		this.code = statusCode;
	}
	
	public int getValue(){
		return this.code;
	}
	
}
