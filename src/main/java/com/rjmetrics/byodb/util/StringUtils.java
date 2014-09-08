package com.rjmetrics.byodb.util;

public class StringUtils {

	public static boolean isNotNullOrEmpty(String arg0){
		boolean isNotNullOrEmpty = false;
		if(arg0 != null && arg0.trim().length() > 0){
			isNotNullOrEmpty = true;
		}
		return isNotNullOrEmpty;
	}
	
	public static boolean isNullOrEmpty(String arg0){
		boolean isNullOrEmpty = false;
		if(arg0 == null || arg0.trim().length() == 0){
			isNullOrEmpty = true;
		}
		return isNullOrEmpty;
	}
}
