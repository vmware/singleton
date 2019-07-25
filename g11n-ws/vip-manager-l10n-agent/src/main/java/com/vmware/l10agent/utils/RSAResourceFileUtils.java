/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 * @author shihu
 *
 */
public class RSAResourceFileUtils {
	public static File getFilefromRoot(String keyFile) {

		String basepath = ResouceFileUtils.class.getClassLoader().getResource(".").getPath();
		String subpath =basepath+ File.separator +keyFile;
		File file = new File(subpath);
		if(file.exists()) {
			return file;
		}else {
			return null;
		}
	}
 
	public static File getFilefromRootWithCreate(String keyFile) throws IOException {

		String basepath = ResouceFileUtils.class.getClassLoader().getResource(".").getPath();
		String subpath =basepath+ File.separator +keyFile;
		File file = new File(subpath);
		if(file.exists()) {
			return file;
		}else {
			if(file.getParent().isEmpty()) {
				file.getParentFile().mkdirs();
			}
	
			file.createNewFile();
			
			return file;
			
		}
	}
	
	
	
	 public static void writerFile(File path, String keyStr) throws IOException {
		 BufferedWriter br = new BufferedWriter(new FileWriter(path));
		 br.write(keyStr);
		 br.flush();
		 br.close();
		 
	 }
	 
	 public static String readerFile2String(File file) throws IOException {
		 BufferedReader br = Files.newBufferedReader(file.toPath());
			String line=null;
			StringBuilder sb = new StringBuilder();
			
			while((line = br.readLine())!= null) {
				sb.append(line);
			}
			
			br.close();
			
			return sb.toString();
	 }
	 
	 public static Map<String,String> readerFile2Map(File file) throws IOException {
		 BufferedReader br = Files.newBufferedReader(file.toPath());
			String line=null;
			Map<String, String> map = new HashMap<String, String>();
			
			while((line = br.readLine())!= null) {
				String[] lines = line.split("=");
				
				
				map.put(lines[0], lines[1]);
			}
			
			br.close();
			
			if(map.size()>0) {
				return map;
			}else {
				return null;
			}
			
	 }
		
	
}
