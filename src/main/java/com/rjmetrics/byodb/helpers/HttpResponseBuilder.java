package com.rjmetrics.byodb.helpers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

public class HttpResponseBuilder {
	
	public static Response buildErrorResponse(int status, String errMessage){
		Response response  =  Response.status(status).entity("{errMessage:"+errMessage+"}").build();
		return response;
	}
	
	public static Response buildSuccessResponse(String json){
		Response response  =  Response.ok(json, MediaType.APPLICATION_JSON).build();
		return response;
	}
	
	public static Response buildStreamingResponse(StreamingOutput stream){
		Response response  =  Response.ok().entity(stream).type(MediaType.APPLICATION_JSON).build();
		return response;
	}
	
	/*public static Response buildResponse(ResponseHelper responseHelper){
		Response response = null;
		if(!responseHelper.isError()){
			response = HttpResponseBuilder.buildSuccessResponse(responseHelper.getOutput());
		}else{
			response = HttpResponseBuilder.buildErrorResponse(responseHelper.getStatus(),responseHelper.getOutput());
		}
		//Need to log before sending back the response.
		
		return response;
	}*/
	
	
}
