/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.conf;

import java.io.File;

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

	/** the path of local resource file,can be configed in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String sourceFileBasepath;

	
	@Value("${remote.l10.base.url}")
	private String remoteBaseL10Url;
	
	@Value("${vip.i18n.base.url}")
	private String vipBasei18nUrl;
	
	
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


	

}
