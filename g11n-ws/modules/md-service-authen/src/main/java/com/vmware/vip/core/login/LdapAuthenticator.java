/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.owasp.esapi.Encoder;
import org.owasp.esapi.reference.DefaultEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthenticator {
	private static Logger LOGGER = LoggerFactory.getLogger(LdapAuthenticator.class);

	@Autowired
	private VipAuthConfig authConfig;
	

	private Map<String, Object> authenticate(String user, String pass){

		String returnedAtts[] = { "sn", "givenName", "mail" };
		String searchFilter = "(&(objectCategory=person)(objectClass=user)(sAMAccountName=%s))";

		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningAttributes(returnedAtts);
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put("java.naming.ldap.factory.socket", "com.vmware.vip.core.login.LTSSocketFactory");
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		env.put(Context.PROVIDER_URL, authConfig.getLdapServerUri());
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, user + "@" + authConfig.getTdomain());
		env.put(Context.SECURITY_CREDENTIALS, pass);
		
		
		LdapContext ctxGC = null;
		try{
			ctxGC = new InitialLdapContext(env, null);
			  Encoder encoder = DefaultEncoder.getInstance();
			  String safeNme = encoder.encodeForLDAP(user);
			  String safeFilter = String.format(searchFilter, safeNme);
			  NamingEnumeration<SearchResult> answer = ctxGC.search(authConfig.getSearchbase(), safeFilter,  searchCtls);
			  
			while (answer.hasMoreElements()){
				SearchResult sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				
				Map<String, Object> amap = null;
				if (attrs != null){
					amap = new HashMap<String, Object>();
					NamingEnumeration<? extends Attribute> ne = attrs.getAll();
					while (ne.hasMore()){
						Attribute attr = (Attribute) ne.next();
						amap.put(attr.getID(), attr.get());
					}
					ne.close();
					return amap;
				}
				

			}

		}catch (NamingException ex){
			LOGGER.error(ex.getMessage(), ex);
		}

		return null;

	}

	public String doLogin(String username, String password) {

		Map<String, Object> umap = authenticate(username, password);
		if (umap == null) {
			LOGGER.info("login failed!!!!");
			return null;
		} else {

			for (Entry<String, Object> entry : umap.entrySet()) {
				LOGGER.info(entry.getKey() + "-----" + entry.getValue().toString());
			}

			return username;

		}

	}
	
	
	
	
	
	
	

}
