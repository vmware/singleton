/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.model;

import java.io.Serializable;

/**
 * 
 *
 * @author shihu
 *
 */
public class UpdateSyncInfoResp implements Serializable{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 510448802971498292L;
	
	
	private long updateCacheToken;
	private String updateCacheProductName="";
	private String updateCacheProductVersion="";
	private long  updateTimeStamp;
	public long getUpdateCacheToken() {
		return updateCacheToken;
	}
	public String getUpdateCacheProductName() {
		return updateCacheProductName;
	}
	public String getUpdateCacheProductVersion() {
		return updateCacheProductVersion;
	}
	public long getUpdateTimeStamp() {
		return updateTimeStamp;
	}
	public void setUpdateCacheToken(long updateCacheToken) {
		this.updateCacheToken = updateCacheToken;
	}
	public void setUpdateCacheProductName(String updateCacheProductName) {
		this.updateCacheProductName = updateCacheProductName;
	}
	public void setUpdateCacheProductVersion(String updateCacheProductVersion) {
		this.updateCacheProductVersion = updateCacheProductVersion;
	}
	public void setUpdateTimeStamp(long updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}
	
	
	


}
