package com.rjmetrics.byodb.test;

import java.util.List;
import java.util.Map;

import com.rjmetrics.byodb.exception.DBException;
import com.rjmetrics.byodb.helpers.Constants;
import com.rjmetrics.byodb.json.JsonUtil;

import junit.framework.TestCase;

public class JsonReaderTest extends TestCase {

	//test for no file present
	public void testJsonStringNoFilePresent(){
		try {
			String json = JsonUtil.getInstance().getJsonString("noFilePresent.txt");
			assertFalse(true); //should not go here.
		} catch (DBException e) {
			assertEquals(e.getStatusCode().getValue(), 404);			
		}
	}
	
	  //test for no data in file.
		public void testJsonStringNoData(){
			try {
				String json = JsonUtil.getInstance().getJsonString("tables_2.txt");
				assertFalse(true); //should not go here.
			} catch (DBException e) {
				assertEquals(e.getStatusCode().getValue(), 500);			
			}
	    }
		
	// test for no data in file.
	public void testJsonStringNoData1() {
		try {
			String json = JsonUtil.getInstance().getJsonString("tables_1.txt");
			assertFalse(true); // should not go here.
		} catch (DBException e) {
			assertEquals(e.getStatusCode().getValue(), 404);
		}
	}

	// test for no data in file.
		public void testJsonString() {
			try {
				String json = JsonUtil.getInstance().getJsonString(Constants.ALL_TABLES);
				assertNotNull(json);
				System.out.println("Json String is - "+json);
			} catch (DBException e) {
				assertFalse("Valid json...should not go to catch block", true);
			}
		}
		
		public void testJsonMap(){
			try {
				Map<String, Object> jsonMap= JsonUtil.getInstance().getJsonMap(Constants.ALL_TABLES);
				System.out.println("Json String is - "+jsonMap);
				List<Object>  list = (List<Object>)jsonMap.get("key7");
				if(!list.get(0).toString().equals("arVal7")){
					assertFalse("Actual output "+list.toString()+",is not equal to expected output - arrVal7",true);
				}				
				
			} catch (DBException e) {
				e.printStackTrace();
				assertFalse("Valid json...should not go to catch block", true);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		public void testWriteTo(){
			try{
				Map<String, Object> jsonMap= JsonUtil.getInstance().getJsonMap(Constants.ALL_TABLES);
				jsonMap.put("key7", "New Value");
				JsonUtil.getInstance().writeJson(Constants.ALL_TABLES, jsonMap);
				jsonMap= JsonUtil.getInstance().getJsonMap(Constants.ALL_TABLES);
				assertEquals(jsonMap.get("key7"),"New Value");			
			}catch (DBException e) {
				e.printStackTrace();
				assertFalse("Valid json...should not go to catch block", true);
			}
		}
		
}
