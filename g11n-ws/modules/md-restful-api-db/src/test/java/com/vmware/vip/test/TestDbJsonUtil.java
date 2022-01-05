/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TestDbJsonUtil {
	
	
	
    public static InputStream getEn_USJson() {
    	ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(en_USJson.getBytes());
    	return tInputStringStream;
    }
    
    
   public static InputStream getZh_CNJson() {
	 	ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(zh_CNJson.getBytes());
    	return tInputStringStream;
    }
	
	
	public final static String en_USJson="{\r\n" + 
			"    \"product\":\"Testing\",\r\n" + 
			"    \"version\":\"1.0.0\",\r\n" + 
			"    \"component\" : \"default\",\r\n" + 
			"    \"messages\": {\r\n" + 
			"        \"test.contact.title\" : \"Contact Us\",\r\n" + 
			"        \"test.feedback.name\" : \"Your Name\",\r\n" + 
			"        \"test.link.download\" : \"Download Page\"\r\n" + 
			"    },\r\n" + 
			"    \"locale\" : \"en_US\"\r\n" + 
			"}";
	
	public final static String zh_CNJson="{\r\n" + 
			"   \"product\":\"Testing\",\r\n" + 
			"    \"version\":\"1.0.0\",\r\n" + 
			"    \"component\" : \"JAVA\",\r\n" + 
			"  \"messages\" : {\r\n" + 
			"    \"test.contact.title\" : \"联系我们\",\r\n" + 
			"    \"test.feedback.name\" : \"您的姓名\",\r\n" + 
			"    \"test.link.download\" : \"下载页面\"\r\n" + 
			"  },\r\n" + 
			"  \"locale\" : \"zh_CN\"\r\n" + 
			"}";
	

}
