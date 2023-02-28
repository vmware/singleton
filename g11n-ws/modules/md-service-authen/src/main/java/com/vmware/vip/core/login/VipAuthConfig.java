/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.core.security.RSAUtils;


@Configuration
public class VipAuthConfig {
	private static Logger logger = LoggerFactory.getLogger(VipAuthConfig.class);
	@Value("${vipservice.authority.jwt.secret:#}")
	private String jwtSecretStr;
	
	private String jwtSecret = null;
	
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

	@Value("${csp.auth.issuer:###}")
	private String issuer;

	@Value("${csp.auth.refresh-interval-sec:30}")
	private int refreshIntervalSec;
	
	@Value("${vipservice.authority.ldap.searchbase:###}")
	private String searchbase; 
	
	/**
	 * RSA public key use to decrypt data
	 */
	@Value("${secret.rsa.publicKeyPath}")
	private String publicKeyPath;
	
	/**
	 * RSA private path use to encrypt data
	 */
	@Value("${secret.rsa.privateKeyPath}")
	private String privateKeyPath;
	
	private String publicKey = null;
	
	private String privateKey = null;

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

	public String getJwtSecret() {
		if(this.jwtSecret == null) {
			try {
				this.jwtSecret = RSAUtils.decryptData(this.jwtSecretStr, getPublicKey());
			} catch (Exception e) {
				 logger.error(e.getMessage(), e);
			}
		}
		return this.jwtSecret;
	}
	
	
	public String getPrivateKey() {
		if(this.privateKey == null) {
			File file = new File(this.privateKeyPath);
			logger.info("the RSA private key path: {}", this.privateKeyPath);
			if(file.exists()) {
				this.privateKey = RSAUtils.getKeyStrFromFile(file);
				logger.debug("private key: {}", this.privateKey);
			}else {
				 logger.error("not found private key file: {}", file.getAbsoluteFile());
			 return null;	
			} 
		}
		return this.privateKey;
	}
	
	public String getPublicKey() {
		if(this.publicKey == null) {
			logger.info("the RSA public key path: {}", this.publicKeyPath);
			File file = new File(this.publicKeyPath);
			if(file.exists()) {
				this.publicKey = RSAUtils.getKeyStrFromFile(file);
				logger.debug("public key: {}", this.publicKey);
			}else {
				 logger.error("not found public key file: {}", file.getAbsoluteFile());
			 return null;	
			} 
		}
		return this.publicKey;
	}


	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public int getRefreshIntervalSec() {
		return refreshIntervalSec;
	}

	public void setRefreshIntervalSec(int refreshIntervalSec) {
		this.refreshIntervalSec = refreshIntervalSec;
	}

}
