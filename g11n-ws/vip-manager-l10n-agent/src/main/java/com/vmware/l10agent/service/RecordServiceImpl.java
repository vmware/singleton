/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vmware.l10agent.base.PropertyContantKeys;
import com.vmware.l10agent.base.TaskSysnQueues;
import com.vmware.l10agent.conf.PropertyConfigs;
import com.vmware.l10agent.model.ComponentSourceModel;
import com.vmware.l10agent.model.RecordModel;
import com.vmware.l10agent.utils.AccessTokenUtils;
import com.vmware.l10agent.utils.HttpRequester;
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;
import com.vmware.vip.common.i18n.status.Response;
/**
 * 
 *
 * @author shihu
 *
 */
@Service
public class RecordServiceImpl implements RecordService {
	private static Logger logger = LoggerFactory.getLogger(RecordServiceImpl.class);

	@Autowired
	private PropertyConfigs config;

	private static String tokenStr = null;

	@PostConstruct
	public void initToken() {
		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			logger.info("init the remote token");
			tokenStr = AccessTokenUtils.getToken(config.getAccessTokenUrl(), config.getAccessAppid(),
					config.getAccessAppsecret(), config.getAccessGrant_type());
		}
	}
	
	
    private void refreshToken() {
    	if (config.getAccessModel().equalsIgnoreCase("remote")) {
    		logger.info("refresh the remote token");
			tokenStr = AccessTokenUtils.getToken(config.getAccessTokenUrl(), config.getAccessAppid(),
					config.getAccessAppsecret(), config.getAccessGrant_type());
		}
    }

    /**
     * get update record from l10n sqlite database when l10n deploy single instance
     */
	@Override
	public List<RecordModel> getRecordModelsByRemote() {
		// TODO Auto-generated method stub
		String getRecodeUrl = config.getRemoteBaseL10Url() + L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV1;
		Map<String, String> params = null;
		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			params = new HashMap<String, String>();
			params.put("AccessToken", tokenStr);
		}
        logger.info("getRecordUrl:>>>"+getRecodeUrl);
		Map<String, String> header=null;
		if (!config.getUserAgent().isEmpty()){
			header = new HashMap<>();
			header.put("User-Agent", config.getUserAgent());
		}
		String result = HttpRequester.sendGet(getRecodeUrl, params, header);
		if(result == null) {
			refreshToken();
			result = HttpRequester.sendGet(getRecodeUrl, params, header);
		}
		logger.info(result);
		if(result == null || result.trim().equals("")) {
			return null;
		}
		JSONObject resultJsonObj = JSONObject.parseObject(result);
		int responseCode = resultJsonObj.getJSONObject("response").getInteger("code");

		if (responseCode == 204) {
			return null;
		} else {
			JSONArray recorJsonArr = resultJsonObj.getJSONArray("data");
			return recorJsonArr.toJavaList(RecordModel.class);
		}

	}

	/**
	 * get the update source content from remote l10n
	 */
	@Override
	public ComponentSourceModel getComponentByRemote(RecordModel record) {
		// TODO Auto-generated method stub
		String getComponentUrl = config.getRemoteBaseL10Url() + L10nI18nAPI.BASE_COLLECT_SOURCE_PATH + "/api/v1/source/sourcecomponent/"
				+ record.getProduct() + "/" + record.getVersion() + "/" + record.getComponent() + "/"
				+ record.getLocale() + "/";
		Map<String, String> params  = new HashMap<String, String>();
		params.put("rand", String.valueOf(System.currentTimeMillis()));
		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			params.put("AccessToken", tokenStr);
		}
		getComponentUrl = getComponentUrl.replaceAll(" ", "%20");   
		 logger.info("getComponentUrl:>>>"+getComponentUrl);
		Map<String, String> header=null;
		if (!config.getUserAgent().isEmpty()){
			header = new HashMap<>();
			header.put("User-Agent", config.getUserAgent());
		}
		String result = HttpRequester.sendGet(getComponentUrl, params, header);
		if(result == null) {
			refreshToken();
			result = HttpRequester.sendGet(getComponentUrl, params, header);
		}
		
		if(result != null) {
        logger.info(result);
		JSONObject resultJsonObj = JSONObject.parseObject(result);
		int responseCode = resultJsonObj.getJSONObject("response").getInteger("code");
		if (responseCode == 204 ) {
			return null;
		} else {
			JSONObject recorJsonObj = resultJsonObj.getJSONObject("data");
			return recorJsonObj.toJavaObject(ComponentSourceModel.class);
		}
		}
		return null;
	}
    /**
     * sync update record status to remote l10n that deploy single instance 
     */
	@Override
	public boolean synchRecordModelsByRemote(RecordModel record) {
		// TODO Auto-generated method stub
		String synchRecordUrl = config.getRemoteBaseL10Url() + L10nI18nAPI.SOURCE_SYNC_RECORD_STATUS_APIV1;
		Map<String, String> param = new HashMap<String, String>();

		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			param.put("AccessToken", tokenStr);
		}

		param.put("product", record.getProduct());
		param.put("version", record.getVersion());
		param.put("component", record.getComponent());
		param.put("locale", record.getLocale());
		param.put("status", String.valueOf(record.getStatus()));
		 StringBuilder sb = new StringBuilder();
		 for(Entry<String,String> entry: param.entrySet()) {
			 sb.append(entry.getKey()+":"+entry.getValue());
			 
		 }
		 logger.info("synchRecordUrl:>>>"+synchRecordUrl+">>>params:"+sb.toString());
		Map<String, String> header=null;
		if (!config.getUserAgent().isEmpty()){
			header = new HashMap<>();
			header.put("User-Agent", config.getUserAgent());
		}
		String result = HttpRequester.post(synchRecordUrl, param, header);
		if(result == null) {
			refreshToken();
			result = HttpRequester.post(synchRecordUrl, param, header);
		}
		
		Response response = JSONObject.parseObject(result, Response.class);
		if (response.getCode() == 204) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * get update record from l10n sqlite database when l10n deploy multi-instance
	 */
	@Override
	public void getRecordModelsByRemoteV1() {
		String getRecodeUrl = config.getRemoteBaseL10Url() + L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV1;
		Map<String, String> params = new HashMap<String, String>();
		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			params.put("AccessToken", tokenStr);
		}
		params.put(PropertyContantKeys.RECORD_UPDATE, "true");
        logger.info("getRecordUrl:>>>"+getRecodeUrl);
		Map<String, String> header=null;
		if (!config.getUserAgent().isEmpty()){
			header = new HashMap<>();
			header.put("User-Agent", config.getUserAgent());
		}
		String result = HttpRequester.sendGet(getRecodeUrl, params, header);
		if(result == null) {
			refreshToken();
			result = HttpRequester.sendGet(getRecodeUrl, params, header);
		}
		logger.info(result);
		if(result == null || result.trim().equals("")) {
			return;
		}
		JSONObject resultJsonObj = JSONObject.parseObject(result);
		int responseCode = resultJsonObj.getJSONObject("response").getInteger("code");

		if (responseCode == 204) {
			logger.warn("response code is {}", responseCode);
			return;
		} else {
			JSONArray recorJsonArr = resultJsonObj.getJSONArray("data");
		     List<RecordModel>  list = recorJsonArr.toJavaList(RecordModel.class);
		     for(RecordModel rm :list) {
		    	 try {
		    		rm.setStatus(0);
					TaskSysnQueues.SendComponentTasks.put(rm);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				}
		     }
		}
	}

    /**
     * get update record  when l10n use s3 data storage
     */
	@Override
	public List<RecordModel> getRecordModelsByRemoteS3(String product, String version, long lastModifyTime) {
		String getRecodeUrl = config.getRemoteBaseL10Url() + L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV2;
		Map<String, String> params = new HashMap<String, String>();;
		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			params.put("AccessToken", tokenStr);
		}
		if(!StringUtils.isEmpty(product)) {
			params.put(PropertyContantKeys.PRODUCT_NAME, String.valueOf(product));
		}
        if(!StringUtils.isEmpty(version)) {
        	params.put(PropertyContantKeys.VERSION, version);
		}
		params.put(PropertyContantKeys.LONG_DATE, String.valueOf(lastModifyTime));
        logger.info("getRecordUrl:>>>"+getRecodeUrl);
        for(Entry<String, String> entry : params.entrySet()) {
        	logger.info("paramters: {}---{}", entry.getKey(), entry.getValue());
        }
		params.put("randNum", String.valueOf(System.currentTimeMillis()));
		Map<String, String> header=null;
		if (!config.getUserAgent().isEmpty()){
			header = new HashMap<>();
			header.put("User-Agent", config.getUserAgent());

		}
		String result = HttpRequester.sendGet(getRecodeUrl, params, header);

		if(result == null) {
			refreshToken();
			result = HttpRequester.sendGet(getRecodeUrl, params, header);
		}
		logger.info("requestResult:{}",result);
		if(result == null || result.trim().equals("")) {
			return null;
		}
		JSONObject resultJsonObj = JSONObject.parseObject(result);
		int responseCode = resultJsonObj.getJSONObject("response").getInteger("code");

		if (responseCode == 204) {
			logger.warn("response code is {}", responseCode);
			return null;
		} else {
			JSONArray recorJsonArr = resultJsonObj.getJSONArray("data");
		    return recorJsonArr.toJavaList(RecordModel.class);
		}
	}

}
