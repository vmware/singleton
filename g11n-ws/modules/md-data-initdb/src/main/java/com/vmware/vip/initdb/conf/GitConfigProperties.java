/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.conf;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
/**
 * 
 *
 * @author shihu
 *
 */
@Configuration
public class GitConfigProperties implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 7128342805229948384L;

	
	
	/*  remoteUri:
		  localFolder:
		  branch:
		  gituser:
		  gitpassword:*/
	@Value("${remoteGitFile.remoteUri}")
	private String remoteUri;
	@Value("${remoteGitFile.localFolder}")
	private String localFolder;
	@Value("${remoteGitFile.branch}")
	private String branch;
	@Value("${remoteGitFile.gituser}")
	private String username;
	@Value("${remoteGitFile.gitpassword}")
	private String password;
	
	@Value("${remoteGitFile.pubKeyPath}")
	private String pubKeyPath;
	@Value("${remoteGitFile.priKeyPath}")
	private String priKeyPath;
	
	@Value("${remoteGitFile.checkInterval}")
	private String checkInterval;

	public String getRemoteUri() {
		return remoteUri;
	}

	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}

	public String getLocalFolder() {
		return localFolder;
	}

	public void setLocalFolder(String localFolder) {
		this.localFolder = localFolder;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPubKeyPath() {
		return pubKeyPath;
	}

	public void setPubKeyPath(String pubKeyPath) {
		this.pubKeyPath = pubKeyPath;
	}

	public String getPriKeyPath() {
		return priKeyPath;
	}

	public void setPriKeyPath(String priKeyPath) {
		this.priKeyPath = priKeyPath;
	}

	public String getCheckInterval() {
		return checkInterval;
	}

	public void setCheckInterval(String checkInterval) {
		this.checkInterval = checkInterval;
	}

	public long getCheckIntervalTime() {
		return Long.parseLong(this.checkInterval.trim());
	}

}
