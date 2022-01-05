/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.synch.model;

import java.io.Serializable;

public class UpdateSyncInfoResp implements Serializable{
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 8974771906969634110L;
	
	private long updateCacheToken = System.currentTimeMillis();
	private String updateCacheProductName="";
	private String updateCacheProductVersion="";
	private long  updateTimeStamp= this.updateCacheToken;
	
	public long getUpdateCacheToken() {
		return updateCacheToken;
	}

	public long getUpdateTimeStamp() {
		return updateTimeStamp;
	}
	
	public String getUpdateCacheProductVersion() {
		return updateCacheProductVersion;
	}
	
	public String getUpdateCacheProductName() {
		return updateCacheProductName;
	}
	public synchronized  void updateCacheToken(String productName, String version) {
		this.updateCacheToken = this.updateCacheToken+1;
		this.updateCacheProductName=productName;
		this.updateCacheProductVersion=version;
		this.updateTimeStamp = System.currentTimeMillis();
		
	}


}
