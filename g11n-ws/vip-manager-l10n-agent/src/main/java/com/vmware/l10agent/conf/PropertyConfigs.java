/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.conf;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.vmware.l10agent.base.PropertyContantKeys;
/**
 * 
 *
 * @author shihu
 *
 */
@Configuration
public class PropertyConfigs {
	private  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/** the path of local resource file,can be configed in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String sourceFileBasepath;

	@Value("${remote.l10n.base.url}")
	private String remoteBaseL10Url;

	@Value("${remote.l10n.user-agent:}")
	private String userAgent;
	
	@Value("${vip.i18n.base.url}")
	private String vipBasei18nUrl;
	
	@Value("${vip.l10n.base.url}")
	private String vipBaseL10nUrl;
	
	@Value("${access.mode}")
	private String accessModel;
	
	@Value("${access.token.url}")
	private String accessTokenUrl;
	
	@Value("${access.token.regenerateurl}")
	private String accessTokenRegenerateUrl;
	
	@Value("${access.appid}")
	private String accessAppid;
	
	
	@Value("${access.appsecret}")
	private String accessAppsecret;
	
	@Value("${access.grant_type}")
	private String accessGrant_type;
	
	@Value("${source.sync.api.version:s3}")
	private String recordApiVersion;

	@Value("${source.sync.req.thread:1}")
	private String recordReqThread;

	@Value("${source.sync.s3.syncListPath:bundle.json}")
	private String syncListPath;

	@Value("${source.sync.s3.startTime:2020-01-01 01:00:00}")
	private String syncStartDatetime;

	@Value("${vip.sync.batch.enable:false}")
	private boolean syncBatchEnable;
	@Value("${vip.sync.batch.size:50}")
	private int syncBatchSize;


	@Value("${vip.sync.source.base64.enable:false}")
	private boolean base64Enable;
  
	@Value("${vip.sync.batch.requestBody.size:8M}")
	private String reqBodySizeStr;



	public long getSyncStartDatetime() {
		
		try {
			Date date = sdf1.parse(this.syncStartDatetime);
			return date.getTime();
		} catch (ParseException e) {
			return 0;
		}
		
	}
	
	public String getRecordApiVersion() {
		return recordApiVersion;
	}

	public void setRecordApiVersion(String recordApiVersion) {
		this.recordApiVersion = recordApiVersion;
	}

	public int getRecordReqThread() {
		int result;
		try {
		result = Integer.valueOf(this.recordReqThread);
		if(result <1) {
			result =1 ;
		}
		}catch(Exception e) {
			result =1;
		}
		return result;
	}

	public void setRecordReqThread(String recordReqThread) {
		this.recordReqThread = recordReqThread;
	}

	
	
	public String getSourceFileBasepath() {
		if(StringUtils.isEmpty(sourceFileBasepath)) {
			this.sourceFileBasepath = "."+File.pathSeparator+PropertyContantKeys.DEFAULT_SOURCE_ROOT;
		}
		
		return sourceFileBasepath;
	}

	public void setSourceFileBasepath(String srcBasepath) {
		this.sourceFileBasepath = srcBasepath;
	}

	public String getRemoteBaseL10Url() {
		return remoteBaseL10Url;
	}

	public void setRemoteBaseL10Url(String remoteBaseL10Url) {
		this.remoteBaseL10Url = remoteBaseL10Url;
	}



	public String getAccessModel() {
		return accessModel;
	}

	public void setAccessModel(String accessModel) {
		this.accessModel = accessModel;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	public String getVipBasei18nUrl() {
		return vipBasei18nUrl;
	}

	public void setVipBasei18nUrl(String vipBasei18nUrl) {
		this.vipBasei18nUrl = vipBasei18nUrl;
	}

	public String getAccessAppid() {
		return accessAppid;
	}

	public void setAccessAppid(String accessAppid) {
		this.accessAppid = accessAppid;
	}

	public String getAccessAppsecret() {
		return accessAppsecret;
	}

	public void setAccessAppsecret(String accessAppsecret) {
		this.accessAppsecret = accessAppsecret;
	}

	public String getAccessGrant_type() {
		return accessGrant_type;
	}

	public void setAccessGrant_type(String accessGrant_type) {
		this.accessGrant_type = accessGrant_type;
	}

	public String getVipBaseL10nUrl() {
		return vipBaseL10nUrl;
	}
		
	public String getSyncListPath() {
		return syncListPath;
	}

	public String getUserAgent() { return userAgent; }

	public int getSyncBatchSize() { return (syncBatchSize - 1); }

	public boolean isSyncBatchEnable() { return syncBatchEnable; }

	public boolean isBase64Enable() {
		return base64Enable;
  }
	public int getSyncReqBodySize(){
		return (1024*1024*Integer.valueOf(this.reqBodySizeStr.toUpperCase().replaceAll("M", "").trim())) - (512*1024);
	}
}
