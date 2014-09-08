package com.rjmetrics.byodb.exception;

import com.rjmetrics.byodb.helpers.StatusCode;
import com.rjmetrics.byodb.util.Logger;

public class DBException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message = null;
	private StatusCode statusCode = null;
	 
    
	public DBException() {
        super();
    }
 
    public DBException(String compName,String message, StatusCode code) {
        super(message);
        this.message = message;
        this.statusCode = code;
        Logger.error(compName, message + ",status code is -"+code);
    }
    
    public DBException(String compName, String message, StatusCode code, Throwable cause) {
        super(cause); 
        this.message = message;
        this.statusCode = code;
        Logger.error(compName, message + ",status code is -"+code, cause);
    }
 
    
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
    
    public StatusCode getStatusCode() {
		return statusCode;
	}

    
    
}
