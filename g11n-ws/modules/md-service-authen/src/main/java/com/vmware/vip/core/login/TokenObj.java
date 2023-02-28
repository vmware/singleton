/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import java.io.Serializable;
/**
 * 
 *
 * @author shihu
 *
 */
public class TokenObj implements Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -9202342874650942404L;
	
	
	
	private String username;
	private long expTime;
	private long issTime;
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getExpTime() {
		return expTime;
	}
	public void setExpTime(long expTime) {
		this.expTime = expTime;
	}
	public long getIssTime() {
		return issTime;
	}
	public void setIssTime(long issTime) {
		this.issTime = issTime;
	}

}
