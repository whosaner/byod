package com.rjmetrics.byodb.test;

import com.rjmetrics.byodb.exception.DBException;
import com.rjmetrics.byodb.helpers.Constants;
import com.rjmetrics.byodb.json.JsonUtil;


public class JsonUtilTest{

	
	public void testJsonString(){
		try {
			String json = JsonUtil.getInstance().getJsonString(Constants.ALL_TABLES);
		} catch (DBException e) {
			//
			
			
		}
	}
}
