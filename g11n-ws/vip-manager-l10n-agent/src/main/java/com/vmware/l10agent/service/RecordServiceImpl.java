/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vmware.l10agent.conf.PropertyConfigs;
import com.vmware.l10agent.model.ComponentSourceModel;
import com.vmware.l10agent.model.RecordModel;
import com.vmware.l10agent.utils.AccessTokenUtils;
import com.vmware.l10agent.utils.HttpRequester;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
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

	@Override
	public List<RecordModel> getRecordModelsByRemote() {
		// TODO Auto-generated method stub
		String getRecodeUrl = config.getRemoteBaseL10Url() + L10NAPIV1.API_L10N + "/records";
		Map<String, String> params = null;
		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			params = new HashMap<String, String>();
			params.put("AccessToken", tokenStr);
		}
        logger.info("getRecordUrl:>>>"+getRecodeUrl);
		String result = HttpRequester.sendGet(getRecodeUrl, params, null);
		if(result == null) {
			refreshToken();
			result = HttpRequester.sendGet(getRecodeUrl, params, null);
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

	@Override
	public ComponentSourceModel getComponentByRemote(RecordModel record) {
		// TODO Auto-generated method stub
		String getComponentUrl = config.getRemoteBaseL10Url() + L10NAPIV1.API_L10N + "/sourcecomponent/"
				+ record.getProduct() + "/" + record.getVersion() + "/" + record.getComponent() + "/"
				+ record.getLocale() + "/";
		Map<String, String> params = null;
		if (config.getAccessModel().equalsIgnoreCase("remote")) {
			params = new HashMap<String, String>();
			params.put("AccessToken", tokenStr);
		}

		 logger.info("getComponentUrl:>>>"+getComponentUrl);
		String result = HttpRequester.sendGet(getComponentUrl, params, null);
		if(result == null) {
			refreshToken();
			result = HttpRequester.sendGet(getComponentUrl, params, null);
		}

		JSONObject resultJsonObj = JSONObject.parseObject(result);
		int responseCode = resultJsonObj.getJSONObject("response").getInteger("code");

		if (responseCode == 204) {
			return null;
		} else {
			JSONObject recorJsonObj = resultJsonObj.getJSONObject("data");
			return recorJsonObj.toJavaObject(ComponentSourceModel.class);
		}

	}

	@Override
	public boolean synchRecordModelsByRemote(RecordModel record) {
		// TODO Auto-generated method stub
		String synchRecordUrl = config.getRemoteBaseL10Url() + L10NAPIV1.API_L10N + "/synchrecord";
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
		String result = HttpRequester.post(synchRecordUrl, param, null);
		if(result == null) {
			refreshToken();
			result = HttpRequester.post(synchRecordUrl, param, null);
		}
		
		Response response = JSONObject.parseObject(result, Response.class);
		if (response.getCode() == 204) {
			return false;
		} else {
			return true;
		}

	}

}
