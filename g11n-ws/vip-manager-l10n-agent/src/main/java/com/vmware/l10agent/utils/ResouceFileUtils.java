/*
 * Copyright 2019-2022 VMware, Inc.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 *
 * @author shihu
 *
 */
public class ResouceFileUtils {
	private static Logger LOG = LoggerFactory.getLogger(ResouceFileUtils.class);
	public static String getLocalizedJSONFileName(String locale) {
		return PropertyContantKeys.DEFAULT_MSG_FILE_NAME +"_"
				+ locale + PropertyContantKeys.DEFAULT_SOURCE_TYPE;
	}
	
	
	
	public static void writerResouce(File file, ComponentSourceModel compnent) throws IOException {
		try(BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)){
		    String jsonStr = JSONObject.toJSONString(compnent);
		    LOG.info("Write local source: {}", jsonStr);
		    bw.write(jsonStr);
		    bw.flush();
		}
		
	}

	
	public static  ComponentSourceModel readerResource(File file) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    try(BufferedReader br = Files.newBufferedReader(file.toPath())){
	        String line=null;
	        
	        while((line = br.readLine())!= null) {
	            sb.append(line);
	        }        
	    }
	
		
		return JSONObject.parseObject(sb.toString(), ComponentSourceModel.class);
		
	}
	
	

 
	
	 public static void writerFile(File path, String keyStr) throws IOException {
	     try( BufferedWriter br = new BufferedWriter(new FileWriter(path))){
	         br.write(keyStr);
	         br.flush();
	     }
	
		 
	 }
	 
	 public static String readerFile2String(File file) throws IOException {
	     StringBuilder sb = new StringBuilder();
		 try(BufferedReader br = Files.newBufferedReader(file.toPath())){
		     
		     String line=null;
		     
		     while((line = br.readLine())!= null) {
		         sb.append(line);
		     }
		     
		 }
			return sb.toString();
	 }
	 
	 public static Map<String,String> readerFile2Map(File file) throws IOException {
	     Map<String, String> map = new HashMap<String, String>();
		try(BufferedReader br = Files.newBufferedReader(file.toPath())){
		    String line=null; 
		    while((line = br.readLine())!= null) {
		        String[] lines = line.split("=");
		        map.put(lines[0], lines[1]);
		    }

		}

			if(map.size()>0) {
				return map;
			}else {
				return null;
			}
			
	 }
		
		
}
