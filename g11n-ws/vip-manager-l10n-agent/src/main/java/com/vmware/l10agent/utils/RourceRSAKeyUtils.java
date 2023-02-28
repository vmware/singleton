/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 *
 * @author shihu
 *
 */
public class RourceRSAKeyUtils {
 
 private static Logger logger = LoggerFactory.getLogger(RourceRSAKeyUtils.class);
 public static String getPrivateKey(InputStream input) {
	 try {
		 logger.info("reader RAS  private keys!!!!");
		return getKeysString(input);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		logger.error("no RAS  private keys files!!!!");
		return null;
	}
 }
 
 public static String getPubKey(InputStream input) {
	 try {
		logger.info("reader RAS  pub keys!!!!");
		return getKeysString(input);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		logger.error("no RAS  pub keys files!!!!");
		return null;
	}
 }
 
 
 
 public static String getPrivateKey(File file) {
	 try {
		 logger.info("reader RAS  private keys!!!!");
		return getKeysString(file);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		logger.error("no RAS  private keys files!!!!");
		return null;
	}
 }
 
 public static String getPubKey(File file) {
	 try {
		 logger.info("reader RAS  pub keys!!!!");
		return getKeysString(file);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		 logger.error("no RAS  pub keys files!!!!");
		return null;
	}
 }
 
 

 
 private static String getKeysString(File path) throws IOException {
     StringBuilder sb = new StringBuilder();
	 try(BufferedReader br = new BufferedReader(new FileReader(path))){
	     String str = null;
	     while((str = br.readLine()) != null){
	         if(str.charAt(0)=='-'){
	             continue;
	         }else {
	             
	             sb.append(str+"\r");
	         }
	     }
	     
	 }     
     return sb.toString();
     
}
	 
 
 private static String getKeysString(InputStream inputStream) throws IOException {
     StringBuilder sb = new StringBuilder();
	try( BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))){
	    String str = null;
	    while((str = br.readLine()) != null){
	        if(str.charAt(0)=='-'){
	            continue;
	        }else {
	            
	            sb.append(str+"\r");
	        }
	    }
	}

     return sb.toString();
     
}
 
 
 public static void writerKeyFile(File path, String keyStr) throws IOException {
	try(BufferedWriter br = new BufferedWriter(new FileWriter(path))){
	    br.write(keyStr);
	    br.flush();   
	}
	 
 }
	 

 
	
	

}
