package com.rjmetrics.byodb.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DBService {

	public static void main(String[] args) {
		//Lets try to find out the key from the Map.
		Map<String, Object> jsonMap = new ConcurrentHashMap<String, Object>();
		jsonMap.put("1", 1);
		jsonMap.put("2", 2);
		jsonMap.put("3", 3);
		jsonMap.put("4", 4);
		jsonMap.put("5", 5.2);
		jsonMap.put("6", "6");
		jsonMap.put("7", "7");
		jsonMap.put("8", "8");
		
		String key="5";
		
		List<Object> results = jsonMap.entrySet().parallelStream().filter(element -> element.getKey().equalsIgnoreCase(key)).collect(Collectors.toList());
		for(Object obj:results){
			System.out.println(obj);
		}

	}
}
