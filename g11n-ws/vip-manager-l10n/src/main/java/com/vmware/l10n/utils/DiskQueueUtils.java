/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
/**
 * This class use to process the source cached files
 *
 *
 */
public class DiskQueueUtils {
	private static Logger logger = LoggerFactory.getLogger(DiskQueueUtils.class);
	
	private static final String L10N_TMP_DIR = "l10n" + File.separator + "tmp" + File.separator;
	
	//source cached files Directory
	private static final String L10N_TMP_SOURCE_PATH = L10N_TMP_DIR+ "source" + File.separator;
	//GRM source cached files Directory
	public static final String L10N_TMP_GRM_PATH = L10N_TMP_DIR + "grm" + File.separator;
	//Singleton source cached files Directory
	public static final String L10N_TMP_I18N_PATH = L10N_TMP_DIR + "i18n" + File.separator;
	//backup source cached files Directory
	public static final String L10N_TMP_BACKUP_PATH = L10N_TMP_DIR + "backup" + File.separator;
	//exception source cached files Directory
	private static final String L10N_TMP_EXCEP_PATH = L10N_TMP_DIR + "excep" + File.separator;
	private final static String SourceStr = "source_";
	
	private DiskQueueUtils() {}
	
	/**
	 * This method use to create the cached source file in L10N_TMP_SOURCE_PATH Directory
	 * @param sources
	 * @param basePath
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
    public static File createQueueFile(Map<String, ComponentSourceDTO> sources, String basePath) throws JsonGenerationException, JsonMappingException, IOException {
    	String tmpFileDir= basePath + L10N_TMP_SOURCE_PATH;
        return createJsonFile(tmpFileDir, sources, sources.size());
    }
    
    private static File createJsonFile(String fileDir, Object obj, int sourceSize) throws JsonGenerationException, JsonMappingException, IOException {
    	File tmpFile= null;
    	String tempFileName = null;
    	do {
    	     tempFileName = fileDir+"tmp_"+System.currentTimeMillis()+"_"+sourceSize+".json";
    	     tmpFile = new File(tempFileName);
    	}while(tmpFile.exists());
    	
    	if(!tmpFile.getParentFile().exists()) {
    			tmpFile.getParentFile().mkdirs();
    	}
    	ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(tmpFile,obj);
	
      File file = new File(tempFileName);
      
      if(file.exists()) {
    	  String resultFileName = tempFileName.replace("tmp_", SourceStr);
    	  System.out.println("resultFileName=" + resultFileName);
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
    
    
   
    /**
     * List the all files in Directory of L10N_TMP_SOURCE_PATH 
     * @param basePath
     * @return
     */
    public static List<File> listSourceQueueFile(String basePath){
    	File file = new File(basePath+L10N_TMP_SOURCE_PATH);
    	return listQueueFiles(file);
    }
    
    /**
     * List the all file in Directory
     * @param dir
     * @return
     */
    public static List<File> listQueueFiles(File dir){
    	File[] files = dir.listFiles(new FilenameFilter() {

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
    
    /**
     * Read the source cached file and convert to Map<String, ComponentSourceDTO> object
     * @param file
     * @return
     * @throws IOException
     */
    public static Map <String, ComponentSourceDTO> getQueueFile2Obj(File file) throws IOException {
    	ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("file=" + file);
		@SuppressWarnings("unchecked")
		Map<String, Object>  resultMap = objectMapper.readValue(file, Map.class);
		System.out.println("resultMap=" + resultMap.toString());
		Map<String, ComponentSourceDTO> result =  new HashMap<String, ComponentSourceDTO>();
        for(Entry<String, Object> entry: resultMap.entrySet()) {
        	ComponentSourceDTO dto = objectMapper.convertValue(entry.getValue(), ComponentSourceDTO.class);
        	result.put(entry.getKey(), dto);
        }
        
		return result;
    }
    
    
    /**
     * Move the source cached file to target file
     * @param source
     * @param target
     * @throws IOException
     */
    public static void moveQueueFile(File source, File target) throws IOException {
    	if(!target.getParentFile().exists()) {
    		target.getParentFile().mkdirs();
    	}
    	Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    
    /**
     * Move the source cached file to L10N_TMP_EXCEP_PATH Directory and add the prefix in target file name
     * @param basePath
     * @param source
     * @param prefix
     */
    public static void moveFile2ExceptPath(String basePath, File source, String prefix){
    	System.out.println("moveFile2ExceptPath=" + source);
    	String targetFileName = basePath+L10N_TMP_EXCEP_PATH+source.getName().replace(SourceStr, SourceStr+prefix+ConstantsChar.UNDERLINE);
    	File file = new File(targetFileName);
    	try {
			moveQueueFile(source, file);
		} catch (IOException e) {
			logger.error("move file to Exception path error:", e);
		}
    }
	/**
	 * copy the source cached file to L10N_TMP_EXCEP_PATH Directory and add the prefix in target file name
	 * @param basePath
	 * @param source
	 * @param prefix
	 */
	public static void copyFile2ExceptPath(String basePath, File source, String prefix){
		String targetFileName = basePath+L10N_TMP_EXCEP_PATH+source.getName().replace(SourceStr, SourceStr+prefix+ConstantsChar.UNDERLINE);
		File target = new File(targetFileName);
		try {
			if(!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
			}
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("move file to Exception path error:", e);
		}
	}
    /**
     * List the all files in Directory of L10N_TMP_EXCEP_PATH 
     * @param basePath
     * @return
     */
    public static List<File> listExceptQueueFile(String basePath){
    	File file = new File(basePath+L10N_TMP_EXCEP_PATH);
    	return listQueueFiles(file);
    }
    
    /**
     * Move the source cached file to L10N_TMP_GRM_PATH Directory 
     * @param basePath
     * @param source
     * @throws IOException
     */
    public static void moveFile2GRMPath(String basePath, File source) throws IOException {
    	String targetFileName = basePath+L10N_TMP_GRM_PATH+source.getName();
    	File file = new File(targetFileName);
    	moveQueueFile(source, file);
    }
    
    /**
     * Move the source cached file to L10N_TMP_I18N_PATH Directory 
     * @param basePath
     * @param source
     * @throws IOException
     */
    public static void moveFile2I18nPath(String basePath, File source) throws IOException {
    	String targetFileName = basePath+L10N_TMP_I18N_PATH+source.getName();
    	File file = new File(targetFileName);
    	moveQueueFile(source, file);
    }
    
    /**
     * Move the source cached file to L10N_TMP_BACKUP_PATH Directory 
     * and change the "source_" prefix to "{fromPath}_" prefix 
     * @param basePath
     * @param source
     * @param fromPath
     * @throws IOException
     */
    public static void moveFile2IBackupPath(String basePath, File source, String fromPath) throws IOException {
    	String targetFileName = basePath+L10N_TMP_BACKUP_PATH+(source.getName().replace(SourceStr, SourceStr+fromPath+ConstantsChar.UNDERLINE));
    	File file = new File(targetFileName);
    	moveQueueFile(source, file);
    }
    
    /**
     * Delete the source cached file in parameter
     * @param file
     * @throws IOException
     */
    public static void delQueueFile(File file) throws IOException {
    	Files.delete(file.toPath());
    }
    
	
}
