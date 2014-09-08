package com.rjmetrics.byodb.restful;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rjmetrics.byodb.exception.DBException;
import com.rjmetrics.byodb.helpers.CacheHelper;
import com.rjmetrics.byodb.helpers.Constants;
import com.rjmetrics.byodb.helpers.HttpResponseBuilder;
import com.rjmetrics.byodb.helpers.StatusCode;
import com.rjmetrics.byodb.json.JsonUtil;
import com.rjmetrics.byodb.util.FileUtil;
import com.rjmetrics.byodb.util.StringUtils;

@Path("/tables")
/**
 * This class defines all the HTTP methods allowed for the BYOD application. (GET,PUT,POST,DELETE)
 * The way this application works is, by persisting the data received in the POST requests in the underlying files. Each file corresponds to one table in the database.
 * 
 * By design each method triggered will first query the in memory cache to see if the contents are present, if the cache is empty it will read the data from the underlying file and update the cache.
 * Every new request would then result in a cache hit until the server is restarted again.
 * 
 * @return - HTTP Status Codes
 * 204 -  In case there is no data present for the requested resource.
 * 404 -  In case there is no resource with the given name.
 * 500 -  In case of an application exception.
 * 200 -  Success
 * 400 - Bad Request, If the incoming request is invalid (i.e no request body in POST would trigger this status code)
 * 
 * @author HRangwala
 *
 */
public class DBService {
	
	private static final String COMP_NAME = DBService.class.getName();
	
	/*@GET
	@Produces({MediaType.APPLICATION_JSON})	
	public Response get(){
		//ResponseHelper respHelper = null;
		CacheHelper cacheHelper = CacheHelper.getInstance();
		String tables = cacheHelper.getTables();
		if(StringUtils.isNotNullOrEmpty(tables)){
			return HttpResponseBuilder.buildSuccessResponse(tables);
		}else{
			StreamingOutput streamingOutput = new StreamingOutput() {				
				@Override
				public void write(OutputStream outputStream) throws IOException,
						WebApplicationException {					
					try {
						String data = JsonReader.getInstance().write(Constants.ALL_TABLES, outputStream, true);
						if(StringUtils.isNotNullOrEmpty(data)){
							//store it back in cache.
							cacheHelper.setTables(data);
						}
					} catch (DBException e) {
						//Response response = HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
						throw new WebApplicationException(e.getStatusCode().getValue());
					}
					
				}
			};
			return HttpResponseBuilder.buildStreamingResponse(streamingOutput);
		}
	}*/
	
	/**
	 * Default method which gets invoked when the http method is GET and the URL ends in /tables.
	 * The method will first query the cache to see if the contents are present, if the cache is empty it will read the data from the underlying file and update the cache.
	 * Every new request would then result in a cache hit. 
	 * 
	 * @return - HTTP Status Codes
	 * 204 -  In case there is no data present for the requested resource.
	 * 404 -  In case there is no resource with the given name.
	 * 500 -  In case of an application exception.
	 * 200 -  Success
	 * 400 - Bad Request, If the incoming request is invalid (i.e no request body in POST would trigger this status code)
	 * 
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})	
	public Response get(){
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		String data = "{}";
		
		try{
		//First get the data from cache.
	 	List<String> allTables  = cacheHelper.getTables();
		if(allTables == null || allTables.isEmpty()){
			if(FileUtil.exists(Constants.ALL_TABLES)){
				allTables = jsonReader.getJsonList(Constants.ALL_TABLES);
				//update the cache.
				cacheHelper.setTables(allTables);
			}													
		}
		data = jsonReader.jsonify(allTables);
		} catch (DBException e) {
			return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
		}		
		return HttpResponseBuilder.buildSuccessResponse(data);
	}
	
	/**
	 * This method would return the contents of the table passed in by the user, if the table exists. It would return a 404 if the table does not exist.	  
	 * 
	 * The method will first query the cache to see if the contents are present, if the cache is empty it will read the data from the underlying file and update the cache.
	 * Every new request would then result in a cache hit.
	 * 
	 * @param tableName
	 * @return
	 */
	@GET
	@Path("/{table}")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response get(@PathParam("table") String tableName){
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		//First get the data from cache.				
		Map<String,Object> jsonMap= cacheHelper.getTableContents(tableName);//lets first try to get the contents from the cache.
		String jsonData = "";
		String fileName = tableName + ".txt";
		try {
			if (jsonMap == null || jsonMap.isEmpty()) {
				// if data not available in cache get it from the underlying file.
				jsonMap = jsonReader.getJsonMap(fileName);
				cacheHelper.putTableContents(tableName, jsonMap); // store the data for future reference.
			}
			jsonData = jsonReader.jsonify(jsonMap);// convert it to json string.

		} catch (DBException e) {
			return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
		}
		return HttpResponseBuilder.buildSuccessResponse(jsonData);
	}

	/**
	 * This method would return the contents of the key present in the respective table.
	 * 
	 * The method will first query the cache to see if the contents are present, if the cache is empty it will read the data from the underlying file and update the cache.
	 * Every new request would then result in a cache hit.
	 * 
	 * @param tableName
	 * @param key
	 * @return
	 */
	
	@GET
	@Path("/{table}/{key}")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response get(@PathParam("table") String tableName, @PathParam("key") String key){
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		//First get the data from cache.				
		Map<String,Object> jsonMap= cacheHelper.getTableContents(tableName);//lets first try to get the contents from the cache.
		String jsonData = "";
		String fileName = tableName + ".txt";
		try {
			if (jsonMap == null || jsonMap.isEmpty()) {
				// if data not available in cache get it from the underlying file.
				jsonMap = jsonReader.getJsonMap(fileName);
				cacheHelper.putTableContents(tableName, jsonMap); // store the data for future reference.
			}
			
			//Lets try to find out the key from the Map.
			//Stupid Jersey does not support Java8 currently..cannot use below code.
			//List<Object> results = jsonMap.entrySet().parallelStream().filter(element -> element.getKey().equalsIgnoreCase(key)).collect(Collectors.toList());
			Object results = jsonMap.get(key);
			jsonData = jsonReader.jsonify(results);

		} catch (DBException e) {
			return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
		}
		return HttpResponseBuilder.buildSuccessResponse(jsonData);

	}
	
	/**
	 * This method would merge the contents of the POST message to the existing table. It would then update the cache for future use.
	 * 
	 * @param tableName
	 * @param postMessage
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{table}")
	public Response post(@PathParam("table") String tableName, String postMessage){
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		String fileName = tableName+".txt";
		try{
			if(StringUtils.isNotNullOrEmpty(postMessage)){
				//on success 201
				//check if file exists.
				  //get the contents of the file
				  //merge it with the existing contents
				  //update the cache
				
				//Lets check whether the table data is already present in the cache or not.
				 Map<String, Object> jsonMap = cacheHelper.getTableContents(tableName);
				 if(jsonMap == null){
					//Lets check if the file exists in the system
					 if(FileUtil.exists(fileName)){
						 //Lets read it from the file and store it in the cache for future references.
						 try{
								jsonMap = jsonReader.getJsonMap(fileName);
							}catch (DBException e) {						
								if(!e.getStatusCode().equals(StatusCode.NO_DATA_AVAILABLE)){
									//Anything other than NO DATA AVAILABLE is a problem
									return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
								}else{
									jsonMap = new ConcurrentHashMap<String, Object>();
								}						
							}
					 }else{
						 FileUtil.createFile(fileName);
						 jsonMap = new ConcurrentHashMap<String, Object>(); //creating a new map
						 
						   //Also we need to update the table cache and the underlying file as well.
						 	List<String> allTables  = cacheHelper.getTables();
							if(allTables == null || allTables.isEmpty()){
								if(FileUtil.exists(Constants.ALL_TABLES)){
									allTables = jsonReader.getJsonList(Constants.ALL_TABLES);
								}else{
									FileUtil.createFile(Constants.ALL_TABLES);
									allTables =  new ArrayList<String>();									
								}														
							}
							
							allTables.add(tableName);
							//update the cache.
							cacheHelper.setTables(allTables);
							//write it back to the file.
							jsonReader.writeJson(Constants.ALL_TABLES, allTables);
					 }					
				 }

					
	
				//Since we have an existing json map, lets merge with the one we got from the request.
				Map<String, Object> jsonMapReq = null;				
				jsonMapReq = jsonReader.parseJson(postMessage);
				//Lets merge the 2 json map's together.
				jsonMap.putAll(jsonMapReq);
				//Lets write it back to the file.
				jsonReader.writeJson(fileName, jsonMap);
				//Lets update the cache.
				cacheHelper.putTableContents(tableName, jsonMap);
			}else{
				//Error Response code - 500
				throw new DBException(COMP_NAME, "Incoming POST data is invalid (either null or empty)", StatusCode.BAD_REQUEST);
			}
		}catch (DBException e) {						
				return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());					
		}
		
		return HttpResponseBuilder.buildSuccessResponse("{\"msg\":\"Data updated successfully\"}");
	}
	
	/**
	 * This method would update the value of the key passed in the request body. After update it would then update the cache with the new value.
	 * @param tableName
	 * @param key
	 * @param jsonData
	 * @return
	 */
	@PUT
	@Path("/{table}/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@PathParam("table") String tableName, @PathParam("key") String key, String jsonData){
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		String fileName = tableName + ".txt";
		//First get the data from cache.				
		Map<String,Object> jsonMap= cacheHelper.getTableContents(tableName);//lets first try to get the contents from the cache.
		try {
			if(jsonMap == null){
				//Means either the cache is empty or this is a new table.
				if(FileUtil.exists(fileName)){					
					//Lets read it from the file and store it in the cache for future references.
					 try{
						 jsonMap = jsonReader.getJsonMap(fileName);
						}catch (DBException e) {						
							if(!e.getStatusCode().equals(StatusCode.NO_DATA_AVAILABLE)){
								//Anything other than NO DATA AVAILABLE is a problem
								return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
							}else{
								jsonMap = new ConcurrentHashMap<String, Object>();
							}						
						}
				}else{
					throw new DBException(COMP_NAME, "Table does not exist.", StatusCode.BAD_REQUEST);
				}
			}
			
			Object jsonObj = jsonReader.convertTo(jsonData);
			jsonMap.put(key, jsonObj); //Storing the data in the Map
			//Writing the data back to the file and updating the global cache.
			cacheHelper.putTableContents(tableName, jsonMap);
			jsonReader.writeJson(fileName, jsonMap);
			
		} catch (DBException e) {
			return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
		}
		return HttpResponseBuilder.buildSuccessResponse(jsonData);
	}
	
	/**
	 * This is the default method which will get invoked when the HTTP method is DELETE and when the URL ends in /tables.
	 * It will empty the contents of all the tables present in the database and update all the cache regions.
	 * @return
	 */
	@DELETE	
	public Response delete(){
		//Need to delete content of all the tables.
		//Get a list of all the tables.
		try{
			java.nio.file.Path path = FileUtil.getPath(""); //we need the location till the root.
			File dir = path.toFile();
			File[] files = dir.listFiles();
			boolean isFilesDeleted= false;
			if(files != null && files.length > 0){
				for (File file:files){			
					if(!file.getName().equalsIgnoreCase(Constants.ALL_TABLES)){
						//empty the contents of each file.
						deleteTable(file.getName());
						isFilesDeleted = true;
					}
				}
			}
			if(!isFilesDeleted){
				throw new DBException(COMP_NAME, "No tables exists in the database.", StatusCode.BAD_REQUEST);
			}
			
		}catch (DBException e) {
			return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
		}
		return HttpResponseBuilder.buildSuccessResponse("{\"msg\":\"Data deleted successfully\"}");
	}
	
	/**
	 * It would delete the contents of the named table if it exists. It will then update the cache.
	 * @param tableName
	 * @return
	 */
	@DELETE
	@Path("/{table}")
	public Response delete(@PathParam("table") String tableName){
		String fileName = tableName + ".txt";
		//check if file exists
		try{
			if(FileUtil.exists(fileName)){
				deleteTable(fileName);
			}else{
				throw new DBException(COMP_NAME, "Table does not exist.", StatusCode.BAD_REQUEST);
			}	
		}catch (DBException e) {
			return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
		}
		
		return HttpResponseBuilder.buildSuccessResponse("{\"msg\":\"Data from the table deleted successfully\"}");
	}
	
	/**
	 * It would remove the value associated with the given key, if the key is present.
	 * @param tableName
	 * @param key
	 * @return
	 */
	@DELETE
	@Path("/{table}/{key}")
	public Response delete(@PathParam("table") String tableName, @PathParam("key") String key){
		String fileName = tableName + ".txt";
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		//check if file exists
		try{
			if(FileUtil.exists(fileName)){
				//remove the value for the particular key (make it null or empty)
				Map<String, Object> jsonMap = cacheHelper.getTableContents(tableName);
				if(jsonMap == null){
					//Lets read it from the file and store it in the cache for future references.
					jsonMap = jsonReader.getJsonMap(fileName);
				}
				Object value = jsonMap.get(key);
				if(value != null){
					jsonMap.put(key, ""); //empty
					//update cache
					cacheHelper.putTableContents(tableName, jsonMap);
					//write it back to the file.
					jsonReader.writeJson(fileName, jsonMap);
				}else{
					throw new DBException(COMP_NAME, "key does not exist.", StatusCode.BAD_REQUEST);
				}
			}else{
				throw new DBException(COMP_NAME, "Table does not exist.", StatusCode.BAD_REQUEST);
			}	
		}catch (DBException e) {
			StatusCode statusCode = e.getStatusCode();
			if(statusCode.equals(StatusCode.NO_DATA_AVAILABLE)){
				//In this case we need to change the status code to BAD REQUEST.
				statusCode = StatusCode.BAD_REQUEST;
			}
			return HttpResponseBuilder.buildErrorResponse(statusCode.getValue(), e.getMessage());
		}
		
		return HttpResponseBuilder.buildSuccessResponse("{\"msg\":\"Data from the table deleted successfully\"}");
	}
	
	/**
	 * This method would search the key against all the tables present in the database. It would then return a list of all the matches.
	 * @param queryParam
	 * @return
	 */
	@GET
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})	
	public Response search(@QueryParam("q") String queryParam){
		JsonUtil jsonReader = JsonUtil.getInstance();
		String jsonData = "{}";
		List<Object> results = new ArrayList<Object>();
		try{
			
			java.nio.file.Path path = FileUtil.getPath(""); //we need the location till the root.
			File dir = path.toFile();
			File[] files = dir.listFiles();
			if(files != null && files.length > 0){
				for (File file:files){			
					if(!file.getName().equalsIgnoreCase(Constants.ALL_TABLES)){
						//Lets get it from cache.
						Object value = searchInTable(file.getName(), queryParam);
						if(value != null){
							//add it to a list.
							results.add(value);
						}
					}
				}
				jsonData = jsonReader.jsonify(results);
			}
			
		}catch (DBException e) {
			return HttpResponseBuilder.buildErrorResponse(e.getStatusCode().getValue(), e.getMessage());
		}
		return HttpResponseBuilder.buildSuccessResponse(jsonData);
	}
	
	
	public Object searchInTable(String fileName, String key) throws DBException{
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		String tableName = fileName.replace(".txt", "");
		Map<String, Object> map = cacheHelper.getTableContents(tableName);
		if(map == null){
			//get it from the underlying file store.
			map = jsonReader.getJsonMap(fileName);			
			//update the cache.
			cacheHelper.putTableContents(tableName,map);
		}
		return map.get(key);
	}
	
	/**
	 * Convenience method which will delete the contents of the file and update the cache.
	 * @param fileName
	 * @throws DBException
	 */
	public void deleteTable(String fileName) throws DBException{
		JsonUtil jsonReader = JsonUtil.getInstance();
		CacheHelper cacheHelper = CacheHelper.getInstance();
		//empty the contents of each file.
		jsonReader.writeJson(fileName, Collections.emptyMap());
		//Update the cache.
		String tableName = fileName.replace(".txt", "");
		Map<String, Object> map = new ConcurrentHashMap<String, Object>();
		cacheHelper.putTableContents(tableName, map);
	}
		
	
	
}
