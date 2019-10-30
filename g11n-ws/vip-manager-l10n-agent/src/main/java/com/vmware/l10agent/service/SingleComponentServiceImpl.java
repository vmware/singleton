/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmware.l10agent.base.PropertyContantKeys;
import com.vmware.l10agent.base.TaskSysnQueues;
import com.vmware.l10agent.conf.PropertyConfigs;
import com.vmware.l10agent.model.ComponentSourceModel;
import com.vmware.l10agent.model.RecordModel;
import com.vmware.l10agent.utils.ResouceFileUtils;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.exceptions.VIPHttpException;
import com.vmware.vip.common.http.HTTPRequester;

@Service
public class SingleComponentServiceImpl implements SingleComponentService{
	
	private static Logger logger = LoggerFactory.getLogger(SingleComponentServiceImpl.class);
	
	@Autowired
	private PropertyConfigs configs;

	private File getFileWithCreate(ComponentSourceModel sourceModel) {
		String path = configs.getSourceFileBasepath()+ sourceModel.getProduct()+ File.separator+
				sourceModel.getVersion()+ File.separator+sourceModel.getComponent()+ File.separator
				+ResouceFileUtils.getLocalizedJSONFileName(sourceModel.getLocale());
		
		File file = new File(path);
		
		if(!file.exists()) {
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
		
				logger.info(e.getMessage(), e);
			}
			
			return file;
			
		}else {
			file.deleteOnExit();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				logger.info(e.getMessage(), e);
			}
		     return file;	
		}
		
		
		
		
	}
	
	
	private File getTargetFile(RecordModel sourceModel) {
		String path = configs.getSourceFileBasepath()+ sourceModel.getProduct()+ File.separator+
				sourceModel.getVersion()+ File.separator+sourceModel.getComponent()+ File.separator
				+ResouceFileUtils.getLocalizedJSONFileName(sourceModel.getLocale());

		File file = new File(path);
		if(file.exists()) {
			return file;
		}else {
			return null;
		}
	}
	
	@Override
	public boolean writerComponentFile(ComponentSourceModel sourceModel) {
		// TODO Auto-generated method stub
		
		File file = getFileWithCreate(sourceModel);
		try {
			ResouceFileUtils.writerResouce(file, sourceModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		
			logger.info(e.getMessage(), e);
			return false;
		}
	
		return true;
	}

	@Override
	public ComponentSourceModel getSourceComponentFile(RecordModel record) {
		// TODO Auto-generated method stub
		File file =getTargetFile(record);
		if(file != null) {
			try {
				return ResouceFileUtils.readerResource(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.info(e.getMessage(), e);
				return null;
			}
		}
		return null;
	}


	@Override
	public boolean delSourceComponentFile(RecordModel record) {
		// TODO Auto-generated method stub
		File file =  getTargetFile(record);
		if(file != null && file.exists()) {
			logger.info("delete file:"+file.getAbsolutePath());
			file.delete();
		}
		return true;
	}


	@Override
	public boolean synchComponentFile2I18n(RecordModel record) {
		// TODO Auto-generated method stub
		
		 ComponentSourceModel model =  getSourceComponentFile(record);
		 
		 for(Entry<String, Object > entry: model.getMessages().entrySet()) {
			 try {
				boolean result = synchkey2VipI18n(record, entry.getKey(), (String)entry.getValue(), null, null);
			    if(!result) {
			    	try {
						TaskSysnQueues.SendComponentTasks.put(record);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					    Thread.currentThread().interrupt();
		
						logger.info(e.getMessage(), e);
					}
			    	return result;
			    }
			 } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
				return false;
			}
		 }
		  
		
		return true;
	}
	
	
	private boolean synchkey2VipI18n(RecordModel record, String key, String srcValue, String commentForSource, String sourceFormat) throws UnsupportedEncodingException {
		StringBuffer urlStr = new StringBuffer(configs.getVipBasei18nUrl());
		
		
        urlStr.append(PropertyContantKeys.I18n_Source_Collect_Url
                .replace("{" + APIParamName.PRODUCT_NAME + "}", record.getProduct())
                .replace("{" + APIParamName.VERSION + "}", record.getVersion())
                .replace("{" + APIParamName.COMPONENT + "}", record.getComponent())
                .replace("{" + APIParamName.LOCALE + "}", record.getLocale())
                .replace("{" + APIParamName.KEY + "}",
                        URLEncoder.encode(key, ConstantsUnicode.UTF8)));
        Map<String, String> param = new HashMap<String, String>();
        
         commentForSource = commentForSource == null ? ConstantsKeys.EMPTY_STRING
                : commentForSource;
        commentForSource = URLEncoder.encode(commentForSource,
                ConstantsUnicode.UTF8);
        
        
         sourceFormat = sourceFormat == null ? ConstantsKeys.EMPTY_STRING
                : sourceFormat;
        
        param.put("source",  URLEncoder.encode(srcValue, ConstantsUnicode.UTF8));
        logger.info("source:"+URLEncoder.encode(srcValue, ConstantsUnicode.UTF8));
        param.put("collectSource", "true");
        param.put(ConstantsKeys.COMMENT_FOR_SOURCE, commentForSource);
        param.put(ConstantsKeys.SOURCE_FORMAT,  sourceFormat);
        
        try {
            logger.info("-----Start to send source----------");
            logger.debug(key+"----------"+srcValue);
            HTTPRequester.postFormData(param, urlStr.toString());
        } catch (VIPHttpException e) {
            logger.error("Failed to send request for source collection", e);
            return false;
        }
        
        return true;
	}
	
	
	
	
	
	
	
	
	 

	 
	 
	 
    
	

}
