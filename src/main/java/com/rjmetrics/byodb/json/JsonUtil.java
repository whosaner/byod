package com.rjmetrics.byodb.json;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.rjmetrics.byodb.exception.DBException;
import com.rjmetrics.byodb.helpers.StatusCode;
import com.rjmetrics.byodb.util.FileUtil;

public class JsonUtil {

	//Since we are not storing any state in the factory. It is good to have a single global instance of the factory. This will enhance performance.
	private static JsonFactory jsonFactory = new JsonFactory();
	private JsonGenerator jsonGenerator = null;
	private JsonParser jsonParser = null;
	private static final String COMP_NAME = JsonUtil.class.getName();
	private static JsonUtil jsonReader = null;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private JsonUtil(){
		
	}
	
	public static JsonUtil getInstance(){
		if(jsonReader == null){
			synchronized(JsonUtil.class){
				if(jsonReader == null){
					jsonReader = new JsonUtil();
				}				
			}
		}
		return jsonReader;
	}
	
	/**
	 * Convenience method to flatten the json object into a json string.
	 * @param jsonMap
	 * @return
	 * @throws DBException
	 */
	public String jsonify(Object jsonObj) throws DBException{
		try{
			String jsonData = objectMapper.writeValueAsString(jsonObj);
			return jsonData;
		}catch (IOException e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside jsonify method", StatusCode.EXCEPTION_OCCURRED,e);
		}	
	}
	
	/**
	 * Convenience method to convert from json string to its object representation.
	 * @param convertFrom
	 * @return
	 * @throws DBException
	 */
	public Object convertTo(String convertFrom) throws DBException{
		try{
			Object jsonData = objectMapper.convertValue(convertFrom, Object.class);
			return jsonData;
		}catch (Exception e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside jsonify method", StatusCode.EXCEPTION_OCCURRED,e);
		}
	}
	
	/**
	 * Convenience method to get the json map from the underlying data store. 
	 * @param fileName
	 * @return
	 * @throws DBException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getJsonMap(String fileName) throws DBException {
		Path readFromPath  = FileUtil.getPath(fileName);
		Map<String,Object> data = null;
		
		try {
			if (Files.exists(readFromPath)) {				
				 data = objectMapper.readValue(readFromPath.toFile(), ConcurrentHashMap.class);
				/*if(data.isEmpty()){
					throw new DBException(COMP_NAME,"There is no data present. - "+readFromPath.toString(), StatusCode.NO_DATA_AVAILABLE);
				}*/
			} else {
				throw new DBException(COMP_NAME,"Resource not found at location - "+readFromPath.toString(), StatusCode.RESOURCE_NOT_FOUND);
			}
		} catch (IOException e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside getJsonMap method", StatusCode.EXCEPTION_OCCURRED,e);
		}		
		return data;
		
	}
	
	/**
	 * Convenience method to get the json list from the underlying data store. 
	 * @param fileName
	 * @return
	 * @throws DBException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getJsonList(String fileName) throws DBException {
		Path readFromPath  = FileUtil.getPath(fileName);
		List<String> data = null;
		
		try {
			if (Files.exists(readFromPath)) {				
				 data = objectMapper.readValue(readFromPath.toFile(), List.class);
				/*if(data.isEmpty()){
					throw new DBException(COMP_NAME,"There is no data present. - "+readFromPath.toString(), StatusCode.NO_DATA_AVAILABLE);
				}*/
			} else {
				throw new DBException(COMP_NAME,"Resource not found at location - "+readFromPath.toString(), StatusCode.RESOURCE_NOT_FOUND);
			}
		} catch (IOException e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside getJsonList method", StatusCode.EXCEPTION_OCCURRED,e);
		}		
		return data;
		
	}
	
	/**
	 * Convenience method which converts the given json string in a json map(key, value) pair.
	 * @param jsonContent
	 * @return
	 * @throws DBException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> parseJson(String jsonContent) throws DBException {
		Map<String,Object> data = null;
		try{			
			data = objectMapper.readValue(jsonContent, ConcurrentHashMap.class);
		}catch (IOException e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside parseJson method", StatusCode.EXCEPTION_OCCURRED,e);
		}	
		
		return data;
	}
	
	/**
	 * Convenience method that would get the json data as string from the underlying file.
	 * @param fileName
	 * @return
	 * @throws DBException
	 */
	public String getJsonString(String fileName) throws DBException {
		String jsonData = ""; 
		Map<String,Object> data = null;
		try {
			data = getJsonMap(fileName);
			jsonData = objectMapper.writeValueAsString(data);
		} catch (IOException e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside getJsonString method", StatusCode.EXCEPTION_OCCURRED,e);
		}		
		return jsonData;
		
	}
	
	public void writeJson(String fileName, Object jsonData) throws DBException{
		Path readFromPath  = FileUtil.getPath(fileName);
		try{
			if(Files.exists(readFromPath)){
				objectMapper.writeValue(readFromPath.toFile(), jsonData);
			}else{
				throw new DBException(COMP_NAME,"Resource does not exist at -"+readFromPath.toString(), StatusCode.RESOURCE_NOT_FOUND);
			}
		}catch (IOException e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside writeJson method", StatusCode.EXCEPTION_OCCURRED,e);
		}
	}
	
	/**
	 * Method which would stream the json output on the output stream.
	 * @param fileName
	 * @param outputStream
	 * @param writeToCache
	 * @return
	 * @throws DBException
	 */
	public String writeAsStream(String fileName, OutputStream outputStream, boolean writeToCache) throws DBException{
		Path readFromPath  = FileUtil.getPath(fileName);
		StringBuilder cache = new StringBuilder(); //building the cache for future references.
		try {
			if (Files.exists(readFromPath)) {
				
				jsonParser = jsonFactory.createParser(readFromPath.toFile());
				jsonGenerator = jsonFactory.createGenerator(outputStream);
				
				if(jsonParser.nextToken() == JsonToken.START_OBJECT){
					while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
						if (writeToCache) {
							cache.append(jsonParser.getValueAsString());
						}
						writeTo(jsonGenerator, jsonParser);
					}
				}else{
					//No use reading an invalid or empty file.
					throw new DBException(COMP_NAME,"There is no data present. - "+readFromPath.toString(), StatusCode.NO_DATA_AVAILABLE);
				}
			} else {
				throw new DBException(COMP_NAME,"Resource not found at location - "+readFromPath.toString(), StatusCode.RESOURCE_NOT_FOUND);
			}
		} catch (IOException e) {			
			throw new DBException(COMP_NAME,"IO Exception occurred inside write method", StatusCode.EXCEPTION_OCCURRED,e);
		}
		finally{
			try {
				if(jsonGenerator != null && jsonParser != null){
					//jsonGenerator.flush();
					jsonGenerator.close();
					jsonParser.close();
				}				
			} catch (IOException e) {				
				throw new DBException(COMP_NAME,"Exception occurred inside write method while cleaning up.", StatusCode.EXCEPTION_OCCURRED,e);
			}			
		}
		return cache.toString();
	}
	
	public static void writeTo(JsonGenerator jsonGenerator, JsonParser jsonParser) throws DBException{
		try {
			JsonToken name = jsonParser.getCurrentToken();
			switch (name) {
			case START_OBJECT: {
				jsonGenerator.writeStartObject();
				break;
			}

			case START_ARRAY: {
				jsonGenerator.writeStartArray();
				break;
			}

			case END_OBJECT: {
				jsonGenerator.writeEndObject();
				break;
			}

			case END_ARRAY: {
				jsonGenerator.writeEndArray();
				break;
			}			
			case FIELD_NAME: {
				jsonGenerator.writeFieldName(jsonParser.getCurrentName());
				jsonParser.nextToken();
				break;
			}
			default:
				jsonGenerator.writeObject(getValue(jsonParser));
				break;
			}		
		} catch (Exception e) {			
			throw new DBException(COMP_NAME, "Exception occurred while writing data on the output stream", StatusCode.EXCEPTION_OCCURRED);
		}

	}
	
	/**
	 * Convenience method to determine the data type of the jsonToken and then return that value.
	 * @param jsonParser
	 * @return
	 * @throws Exception
	 */
	public static Object getValue(JsonParser jsonParser) throws Exception{
		JsonToken jsonToken = jsonParser.getCurrentToken();
		Object objVal = null;
		
		if(JsonToken.VALUE_NUMBER_FLOAT.equals(jsonToken)){
			objVal = jsonParser.getFloatValue();
		}else if(JsonToken.VALUE_NUMBER_INT.equals(jsonToken)){
			objVal = jsonParser.getIntValue();			
		}else if(JsonToken.VALUE_STRING.equals(jsonToken)){
			objVal = jsonParser.getValueAsString();			
		}else if(JsonToken.VALUE_NULL.equals(jsonToken)){
			objVal = null;
		}else{
			objVal = jsonParser.getText();
		}
		return objVal;
	}
	
	
}
