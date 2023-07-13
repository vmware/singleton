/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.auth;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VIPAuthentication {
	private static Logger logger = LoggerFactory.getLogger(VIPAuthentication.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static IAuthen getInstance(Class c, HttpServletRequest request) {
		IAuthen v = null;
		AnnotatedType[] types = c.getAnnotatedInterfaces();
		if (types.length > 0) {
			String name = types[0].getType().getTypeName();
			if (name.equalsIgnoreCase(IAuthen.class.getName())) {
				try {
					v = (IAuthen) c.getDeclaredConstructor(
							HttpServletRequest.class).newInstance(request);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
			}
		}
		return v;
	}
}
