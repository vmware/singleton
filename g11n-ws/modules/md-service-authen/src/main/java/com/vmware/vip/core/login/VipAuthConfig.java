/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VipAuthConfig {
	
	@Value("${vipservice.authority.session.expiretime}")
	private int sessionExpire;
	
	@Value("${vipservice.authority.token.expiretime}")
	private int tokenExpire;
	
	@Value("${vipservice.authority.enable}")
	private String authSwitch;
	
	@Value("${vipservice.authority.ldap.server.url:###}")
	private String ldapServerUri;
	
	@Value("${vipservice.authority.ldap.tdomain:###}")
	private String tdomain;
	
	@Value("${csp.auth.url:###}")
	private String cspAuthUrl;
	
	@Value("${vipservice.authority.ldap.searchbase:DC=vmware,DC=com}")
	private String searchbase; 

	public String getLdapServerUri() {
		return ldapServerUri;
	}

	public String getTdomain() {
		return tdomain;
	}

	public void setLdapServerUri(String ldapServerUri) {
		this.ldapServerUri = ldapServerUri;
	}

	public void setTdomain(String tdomain) {
		this.tdomain = tdomain;
	}

	public int getSessionExpire() {
		return sessionExpire;
	}

	public void setSessionExpire(int sessionExpire) {
		this.sessionExpire = sessionExpire;
	}

	public int getTokenExpire() {
		return tokenExpire;
	}

	public void setTokenExpire(int tokenExpire) {
		this.tokenExpire = tokenExpire;
	}

	public String getAuthSwitch() {
		return authSwitch;
	}

	public void setAuthSwitch(String authSwitch) {
		this.authSwitch = authSwitch;
	}

	public String getCspAuthUrl() {
		return cspAuthUrl;
	}

	public void setCspAuthUrl(String cspAuthUrl) {
		this.cspAuthUrl = cspAuthUrl;
	}

	public String getSearchbase() {
		return searchbase;
	}

	public void setSearchbase(String searchbase) {
		this.searchbase = searchbase;
	}
}
