/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import java.io.Serializable;

public class AuthModel implements Serializable {


	private static final long serialVersionUID = 2384541895535715323L;
	/**
	 * 
	 */

	
	
	
	private long id;
	private String username;
	private String pubkey;
	private String refreshKey;
	private String verifyKey;
	
	
	
	public AuthModel() {}
	public AuthModel(String username) {
		this.username = username;
	
	}
	public AuthModel(String username, String pubkey) {
		this.username = username;
		this.pubkey = pubkey;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPubkey() {
		return pubkey;
	}
	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}
	public String getRefreshKey() {
		return refreshKey;
	}
	public void setRefreshKey(String refreshKey) {
		this.refreshKey = refreshKey;
	}
	public String getVerifyKey() {
		return verifyKey;
	}
	public void setVerifyKey(String verifyKey) {
		this.verifyKey = verifyKey;
	}
	
	
	

}
