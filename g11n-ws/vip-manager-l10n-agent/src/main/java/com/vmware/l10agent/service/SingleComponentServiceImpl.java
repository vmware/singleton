/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.vmware.l10agent.model.KeySourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
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
	/**
	 * write the update source content to locale file
	 */
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
    /**
     * read the update source content from locale file
     */
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

    /**
     * delete the sync source content from locale file
     */
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

	
	private boolean syncKey2VipI18n(RecordModel record, String key, String srcValue, String commentForSource, String sourceFormat) throws UnsupportedEncodingException {
		if(configs.getVipBasei18nUrl().equalsIgnoreCase(PropertyContantKeys.LOCAL)) {
			return false;
		}
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

	private boolean syncKey2VipL10n(RecordModel record,  String key, String srcValue, String sourceFormatVal) throws UnsupportedEncodingException{
		if(configs.getVipBaseL10nUrl().equalsIgnoreCase(PropertyContantKeys.LOCAL)) {
			return false;
		}
		StringBuffer urlStr = new StringBuffer(configs.getVipBaseL10nUrl());
        urlStr.append(PropertyContantKeys.L10n_Source_Collect_Url
                .replace("{" + APIParamName.PRODUCT_NAME + "}", record.getProduct())
                .replace("{" + APIParamName.VERSION + "}", record.getVersion())
                .replace("{" + APIParamName.COMPONENT + "}", record.getComponent())
                .replace("{" + APIParamName.LOCALE + "}", record.getLocale())
                .replace("{" + APIParamName.KEY + "}", key));
          logger.info("source:{}", srcValue);
		  if (sourceFormatVal != null){
			  urlStr.append("?sourceFormat=");
			  urlStr.append(URLEncoder.encode(sourceFormatVal, ConstantsUnicode.UTF8));
		  }
        try {
            logger.info("-----Start to send source----------");
            logger.debug("{}----------{}", key, srcValue);
            String response = postData(srcValue, urlStr.toString().replaceAll(" ", "%20"));
            logger.info(response);
          JSONObject resultJsonObj = JSONObject.parseObject(response);
   		  int responseCode = resultJsonObj.getJSONObject("response").getInteger("code");
           if(responseCode != 200) {
        	   logger.error("Failed to sync the source to l10n -> {} : {}", key, srcValue);
        	   return false;
           }
        } catch (Exception e) {
            logger.warn("Failed to sync the source to l10n -> {} : {}", key, srcValue);
            return false;
        }
        
        return true;
	}



	/**
	 * sync the local source content to internal l10n or i18n
	 */
	@Override
	public boolean synchComponentFile2Internal(RecordModel record) {

		ComponentSourceModel model =  getSourceComponentFile(record);
		if (this.configs.isSyncBatchEnable()){
			return syncBatchSource(record, model);
		}else {
		  return syncSingleSource(record, model);
		}

	}

	private String postData(String source, String urlStr)  throws VIPHttpException {
		String response =  HTTPRequester.postData(source, urlStr, "application/json", "POST", null);
		if(response == null || response.equalsIgnoreCase("")) {
			throw new VIPHttpException("postFormData error.");
		}
		return response;
	}

	private boolean  syncSingleSource(RecordModel record, ComponentSourceModel model){
		if (model != null) {
			String sourceFormat = null;

			for (Entry<String, Object> entry : model.getMessages().entrySet()) {
				try {
					String sourceVal = (String)entry.getValue();
					if (configs.isBase64Enable()){
						sourceFormat = PropertyContantKeys.BASE64_REQ_PARAMETER;
						sourceVal = Base64.getEncoder().encodeToString(sourceVal.getBytes("UTF-8"));
					}
					boolean result = syncKey2VipL10n(record, entry.getKey(), sourceVal,sourceFormat);
					if (!result) {
						result = syncKey2VipI18n(record, entry.getKey(), sourceVal, null, sourceFormat);
						if (!result) {
							try {
								if (record.getStatus() < 5) {
									record.setStatus(record.getStatus() + 1);
									TaskSysnQueues.SendComponentTasks.put(record);
								}
							} catch (InterruptedException e) {

								Thread.currentThread().interrupt();

								logger.info(e.getMessage(), e);
							}
							return result;
						}
					}
				} catch (UnsupportedEncodingException e) {

					logger.error(e.getMessage(), e);
					return false;
				}
			}
		}

		return true;
	}

	private  boolean syncBatchSource(RecordModel record, ComponentSourceModel model){
		if (model != null ){
			boolean l10nResult = syncBatchKey2VipL10n(record, model);
			boolean i18nResult = syncBatchKey2VipI18n(record, model);

			if (l10nResult && i18nResult){
				return true;
			}else {
				try {
					if (record.getStatus() < 5) {
						record.setStatus(record.getStatus() + 1);
						TaskSysnQueues.SendComponentTasks.put(record);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logger.error(e.getMessage(), e);
				}
				return false;
			}

		}
		return true;
	}


	private boolean syncBatchKey2VipI18n(RecordModel record,  ComponentSourceModel model ) {

		if(configs.getVipBasei18nUrl().equalsIgnoreCase(PropertyContantKeys.LOCAL)) {
			return true;
		}

		StringBuffer urlStr = new StringBuffer(configs.getVipBasei18nUrl());
		urlStr.append(PropertyContantKeys.I18N_SOURCE_COLLECT_SET_URL
				.replace("{" + APIParamName.PRODUCT_NAME + "}", record.getProduct())
				.replace("{" + APIParamName.VERSION + "}", record.getVersion())
				.replace("{" + APIParamName.COMPONENT + "}", record.getComponent())
				.replace("{" + APIParamName.LOCALE + "}", record.getLocale()));

		return prepareBatchDataAndSend(urlStr.toString(), model);
	}


	private boolean syncBatchKey2VipL10n(RecordModel record,  ComponentSourceModel model ){
		if(configs.getVipBaseL10nUrl().equalsIgnoreCase(PropertyContantKeys.LOCAL)) {
			return true;
		}

		StringBuffer urlStr = new StringBuffer(configs.getVipBaseL10nUrl());
		urlStr.append(PropertyContantKeys.L10N_SOURCE_COLLECT_SET_URL
				.replace("{" + APIParamName.PRODUCT_NAME + "}", record.getProduct())
				.replace("{" + APIParamName.VERSION + "}", record.getVersion())
				.replace("{" + APIParamName.COMPONENT + "}", record.getComponent())
				.replace("{" + APIParamName.LOCALE + "}", record.getLocale()));

		return prepareBatchDataAndSend(urlStr.toString(), model);
	}



	private boolean prepareBatchDataAndSend(String urlStr, ComponentSourceModel model){
		List<KeySourceModel> sourceModels = new ArrayList<>();
		int count =0;
		for (Entry<String, Object> entry : model.getMessages().entrySet() ) {
			KeySourceModel keySourceModel = new KeySourceModel();
			keySourceModel.setKey(entry.getKey());
			keySourceModel.setSource((String) entry.getValue());
			if (configs.isBase64Enable()){
				keySourceModel.setSourceFormat(PropertyContantKeys.BASE64_REQ_PARAMETER);
				try {
					byte[] bytes = ((String) entry.getValue()).getBytes("UTF-8");
					keySourceModel.setSource(Base64.getEncoder().encodeToString(bytes));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(), e);
				}
			}else {
				keySourceModel.setSource((String) entry.getValue());
			}
			sourceModels.add(keySourceModel);
			count++;
			byte[] valByte = JSONArray.toJSONBytes(sourceModels);
			if (sourceModels.size() > configs.getSyncBatchSize() || count == model.getMessages().size() || valByte.length > configs.getSyncReqBodySize()) {
				if(valByte.length < configs.getSyncReqBodySize() || sourceModels.size() == 1){
					boolean reqResult = postBatchData(sourceModels, urlStr);
					if (reqResult){
						sourceModels = new ArrayList<>();
					}else {
						return  false;
					}
				}else {
					KeySourceModel lastItem = sourceModels.remove(sourceModels.size()-1);
					valByte = JSONArray.toJSONBytes(sourceModels);
					boolean reqResult = postBatchData(sourceModels, urlStr);
					if (reqResult){
						sourceModels = new ArrayList<>();
						sourceModels.add(lastItem);
					}else {
						return  false;
					}

				}
				if (valByte.length > configs.getSyncReqBodySize()){
					logger.warn("sync bigger than request body key:{},  batch size: {}, request body byte size: {}",sourceModels.get(0).getKey(), sourceModels.size(), valByte.length);
				}
			}

		}
		if (sourceModels.size() > 0){
			byte[] valByte = JSONArray.toJSONBytes(sourceModels);
			if (valByte.length > configs.getSyncReqBodySize()){
				logger.warn("sync bigger than request body key:{},  batch size: {}, request body byte size: {}",sourceModels.get(0).getKey(), sourceModels.size(), valByte.length);
			}
			return postBatchData(sourceModels, urlStr);
		}
		return true;
	}

	private boolean postBatchData(List<KeySourceModel> sourceModels, String urlStr){

		String srcValue = JSONArray.toJSONString(sourceModels);
		try {
			logger.info("send source url:{}, sourceSize:{}, sourceSet:{}", urlStr, sourceModels.size(),  srcValue);
			String response = postData(srcValue, urlStr.replaceAll(" ", "%20"));
			logger.info("response: {}", response);
			JSONObject resultJsonObj = JSONObject.parseObject(response);
			int responseCode = resultJsonObj.getJSONObject("response").getInteger("code");
			if (responseCode != 200) {
				logger.error("Failed to sync the source to remote url:{}, source:{}", urlStr,   srcValue);
				return false;
			}else {
				return true;
			}

		} catch (VIPHttpException e) {
			logger.error(e.getMessage(), e);
			return false;
		}

	}

}
