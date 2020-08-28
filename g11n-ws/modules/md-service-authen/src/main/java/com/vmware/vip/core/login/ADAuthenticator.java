/*
 * Copyright 2019-2020 VMware, Inc.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADAuthenticator {
	private static Logger logger = LoggerFactory.getLogger(ADAuthenticator.class);
	private String domain;
	private String ldapHost;
	private String searchBase;

	public ADAuthenticator(String domain, String host, String dn)

	{

		this.domain = domain;

		this.ldapHost = host;

		this.searchBase = dn;

	}

	public Map<String, Object> authenticate(String user, String pass)

	{

		String returnedAtts[] = { "sn", "givenName", "mail" };

		// String searchFilter = "(&(objectClass=user)(sAMAccountName=" + user + "))";
		String searchFilter = "(&(objectCategory=person)(objectClass=user)(sAMAccountName=" + user + "))";

		// AUTH_LDAP_USER_SEARCH = LDAPSearch('DC=vmware,DC=com',ldap.SCOPE_SUBTREE,
		// '(&(objectCategory=person)(objectClass=user)(sAMAccountName=%(user)s))'

		// Create the search controls

		SearchControls searchCtls = new SearchControls();

		searchCtls.setReturningAttributes(returnedAtts);

		// Specify the search scope

		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		env.put(Context.PROVIDER_URL, ldapHost);

		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		env.put(Context.SECURITY_PRINCIPAL, user + "@" + domain);

		env.put(Context.SECURITY_CREDENTIALS, pass);

		LdapContext ctxGC = null;

		try

		{

			ctxGC = new InitialLdapContext(env, null);

			// Search objects in GC using filters
			NamingEnumeration<SearchResult> answer = ctxGC.search(searchBase, searchFilter, searchCtls);

			while (answer.hasMoreElements())

			{

				SearchResult sr = (SearchResult) answer.next();

				Attributes attrs = sr.getAttributes();

				Map<String, Object> amap = null;

				if (attrs != null)

				{

					amap = new HashMap<String, Object>();

					NamingEnumeration<? extends Attribute> ne = attrs.getAll();

					while (ne.hasMore())

					{
						Attribute attr = (Attribute) ne.next();

						amap.put(attr.getID(), attr.get());

					}

					ne.close();

				}

				return amap;

			}

		}

		catch (NamingException ex)

		{

			logger.error(ex.getMessage(), ex);

		}

		return null;

	}

	public static String doLogin(String username, String password, String serverUri, String tdomain, String tsearchbase) {

		String AUTH_LDAP_BIND_DN = username;
		String AUTH_LDAP_BIND_PASSWORD = password;
		String AUTH_LDAP_SERVER_URI = serverUri;
		String tidaphost = AUTH_LDAP_SERVER_URI;
		ADAuthenticator ada = new ADAuthenticator(tdomain, tidaphost, tsearchbase);
		Map<String, Object> umap = ada.authenticate(AUTH_LDAP_BIND_DN, AUTH_LDAP_BIND_PASSWORD);
		if (umap == null) {
			logger.info("login failed!!!!");
			return null;
		} else {

			for (Entry<String, Object> entry : umap.entrySet()) {

				logger.info(entry.getKey() + "-----" + entry.getValue().toString());

			}

			return username;

		}

	}

}
