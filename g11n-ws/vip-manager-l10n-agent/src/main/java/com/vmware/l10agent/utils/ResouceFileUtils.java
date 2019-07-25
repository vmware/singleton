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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vmware.l10agent.base.PropertyContantKeys;
import com.vmware.l10agent.model.ComponentSourceModel;


/**
 * 
 *
 * @author shihu
 *
 */
public class ResouceFileUtils {
	public static String getLocalizedJSONFileName(String locale) {
		return PropertyContantKeys.DEFAULT_MSG_FILE_NAME +"_"
				+ locale + PropertyContantKeys.DEFAULT_SOURCE_TYPE;
	}
	
	
	
	public static void writerResouce(File file, ComponentSourceModel compnent) throws IOException {
		BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
		String jsonStr = JSONObject.toJSONString(compnent);
		//System.out.println(jsonStr);;
		bw.write(jsonStr);
		bw.flush();
		bw.close();
		
		
	}

	
	public static  ComponentSourceModel readerResource(File file) throws IOException {
		BufferedReader br = Files.newBufferedReader(file.toPath());
		String line=null;
		StringBuilder sb = new StringBuilder();
		
		while((line = br.readLine())!= null) {
			sb.append(line);
		}
		
		br.close();
		
		return JSONObject.parseObject(sb.toString(), ComponentSourceModel.class);
		
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
