package com.rjmetrics.byodb.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class CacheHelper {

	private static CacheHelper cacheHelper = null;
	private List<String> tables = new ArrayList<String>(); //a cache to hold the list of all the tables in the database
	private Map<String,Map<String,Object>> tblContents =  new ConcurrentHashMap<String, Map<String,Object>>(); //cache to hold the entire contents of table in memory.
	
	private CacheHelper(){
		
	}
	
	/**
	 * Convenience method to get a singleton object of the class.
	 * @return
	 */
	public static CacheHelper getInstance(){
		if(cacheHelper == null){
			synchronized(CacheHelper.class){
				if(cacheHelper == null){
					cacheHelper = new CacheHelper();
				}
			}
		}
		return cacheHelper;		
	}
	
	
	public Map<String,Object> getTableContents(String tableName){
		return tblContents.get(tableName);
	}
	
	public List<String> getTables() {
		return tables;
	}
	
	public void setTables(List<String> tables){
		this.tables = tables;
	}
	
	public void addTable(String table){
		this.tables.add(table);
	}
	
	public void putTableContents(String tableName, Map<String, Object> jsonMap){
		this.tblContents.put(tableName, jsonMap);
	}
	
	
}

