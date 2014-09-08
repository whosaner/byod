package com.rjmetrics.byodb.helpers;

import javax.ws.rs.core.Response.Status;

public class ResponseHelper {

	private String output;
	private Status status;
	private boolean isError;
		
	public ResponseHelper(String output, Status status, boolean isError) {
		super();
		this.output = output;
		this.status = status;
		this.isError = isError;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}
	
	
}
