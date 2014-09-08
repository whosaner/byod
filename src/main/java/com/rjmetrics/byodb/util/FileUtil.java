package com.rjmetrics.byodb.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rjmetrics.byodb.exception.DBException;
import com.rjmetrics.byodb.helpers.Constants;
import com.rjmetrics.byodb.helpers.StatusCode;

public class FileUtil {

	private static final String OS = System.getProperty("os.name").toLowerCase();
	private static final String COMP_NAME = FileUtil.class.getName();
	/**
	 * Convenience method to check if the OS is windows or not.
	 * @return
	 */
	public static boolean isWindows(){
		if(OS.startsWith("win")){
			return true;
		}else{
			return false;
		}
	}
	
	public static Path getPath(String fileName){
		String os_dir = "";		
		if(isWindows()){
			os_dir = Constants.WINDOWS_DIR;
		}else{
			os_dir = Constants.LINUX_DIR;
		}
		return Paths.get(os_dir, fileName);
	}
	
	public static boolean exists(String fileName){
		Path path = getPath(fileName);
		if(Files.exists(path)){
			return true;
		}else{
			return false;
		}
	}
	
	public static void createFile(String fileName) throws DBException{
		Path path = getPath(fileName);
		try {
			Files.createFile(path);
		} catch (IOException e) {
			throw new DBException(COMP_NAME, "Exception occurred while creating a file -"+path.toString(),StatusCode.EXCEPTION_OCCURRED,e);
		}
	}
	
}
