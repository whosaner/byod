package com.rjmetrics.byodb.util;

public class Logger {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Logger.class);
	public static void info(String className, String message){
		System.out.println("INFO :"+className+" : "+message);
		log.info(className+" : "+message);
		
	}
	
	public static void error(String className, String message, Throwable ex){
		System.out.println(className+" : "+message+ " : "+ex.getMessage());
		log.error(className+" : "+message, ex);
	}
	
	public static void error(String className, String message){
		System.out.println("ERROR :"+className+" : "+message);
		log.error(className+" : "+message);
	}
}
