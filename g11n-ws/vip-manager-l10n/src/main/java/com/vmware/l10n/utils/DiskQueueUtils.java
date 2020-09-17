/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
/**
 * 
 *
 * @author shihu
 *
 */
public class DiskQueueUtils {
	public static final String L10N_TMP_PATH = "l10n" + File.separator + "tmp" + File.separator;
	private final static String SourceStr = "source_";
	private DiskQueueUtils() {}
    public static File createQueueFile(Map<String, ComponentSourceDTO> sources, String basePath) throws JsonGenerationException, JsonMappingException, IOException {
    	File tmpFile= null;
    	String tempFileName = null;
    	do {
    	     tempFileName = basePath + L10N_TMP_PATH+"tmp_"+System.currentTimeMillis()+"_"+sources.size()+".json";
    	     tmpFile = new File(tempFileName);
    	}while(tmpFile.exists());
    	
    	if(!tmpFile.getParentFile().exists()) {
    			tmpFile.getParentFile().mkdirs();
    	}
    	ObjectMapper objectMapper = new ObjectMapper();
    	
        objectMapper.writeValue(tmpFile,sources);
	
      File file = new File(tempFileName);
      
      if(file.exists()) {
    	  String resultFileName = tempFileName.replace("tmp_", SourceStr);
    	  File resultFile = new File(resultFileName);
    	  if(file.renameTo(resultFile)) {
    		  return resultFile;
    	  }else {
    		  throw new RuntimeException("disk queue rename file error!!");
    	  }
      }else {
    	  return null;
      }
    }
    
    
    
    public static List<File> listSourceQueueFile(String basePath){
    	File file = new File(basePath+L10N_TMP_PATH);
    	File[] files = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if(name.startsWith(SourceStr)) {
					return true;
				}else {
					return false;
				}
			}});
    	
    	if(files != null) {
    		return Arrays.asList(files);
    	}else {
    		return null;
    	}
    }
    
    
    public static Map<String, ComponentSourceDTO> getQueueFile2Obj(File file) throws JsonParseException, JsonMappingException, IOException {
    	ObjectMapper objectMapper = new ObjectMapper();
      
		@SuppressWarnings("unchecked")
		Map<String, Object>  resultMap = objectMapper.readValue(file, Map.class);
		Map<String, ComponentSourceDTO> result =  new HashMap<String, ComponentSourceDTO>();
        for(Entry<String, Object> entry: resultMap.entrySet()) {
        	ComponentSourceDTO dto = objectMapper.convertValue(entry.getValue(), ComponentSourceDTO.class);
        	result.put(entry.getKey(), dto);
        }
        
		return result;
    }
    
    
    public static void delQueueFile(File file) throws IOException {
    	Files.delete(file.toPath());
    }
    
	
}
