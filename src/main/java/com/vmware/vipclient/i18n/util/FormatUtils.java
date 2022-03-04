/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.text.FieldPosition;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.l2.text.MessageFormat;

public class FormatUtils {
	static Logger logger = LoggerFactory.getLogger(FormatUtils.class);
    private FormatUtils() {

    }

    /**
     * format a string with placeholder by the input arguments
     *
     * @param pattern
     *            a string contains placeholder
     * @param arguments
     *            used to format the pattern string
     * @return a formatted string
     */
    public static String format(String pattern, Object... arguments) {
        String escaped = pattern.replaceAll("'", "''");
        return java.text.MessageFormat.format(escaped, arguments);
    }
    
    private static String formatWithArrayArgs(String pattern, Locale locale, Object[] arguments) {
		if (arguments.length > 0) {
	        MessageFormat messageFormat = new MessageFormat(pattern, locale);
	        return messageFormat.format(arguments, new StringBuilder(), new FieldPosition(0)).toString();
		}
		return pattern;
    }
   
    private static String formatWithNamedArgs(String pattern, Locale locale, Map<String, Object> arguments) {
		if (arguments.size() > 0) {
	    	MessageFormat messageFormat = new MessageFormat(pattern, locale);
	        return messageFormat.format(arguments, new StringBuilder(), new FieldPosition(0)).toString();
		}
		return pattern;
    }

    public static String formatMsg(String pattern, Locale locale, Object argument) {
    	if (pattern == null || pattern.isEmpty() || argument == null) {
    	   	return pattern;
        }
    	
		if (argument instanceof Map<?, ?>) {
			return formatWithNamedArgs(pattern, locale, (Map<String, Object>) argument);
        } else if (argument instanceof Object[]) {
        	return formatWithArrayArgs(pattern, locale, (Object[])argument);
        } 

		return pattern;
    }

	/**
	 * @deprecated Use {@link #formatMsg(String, Locale, Object)}.
	 */
    public static String format(String pattern, Locale locale, Object... arguments) {
    	try {
	        MessageFormat messageFormat = new MessageFormat(pattern, locale);
	        pattern = messageFormat.format(arguments, new StringBuilder(), new FieldPosition(0)).toString();
	        if (VIPCfg.getInstance().isPseudo() && pattern != null && !pattern.startsWith(ConstantsKeys.PSEUDOCHAR)) {
	    		pattern = ConstantsKeys.PSEUDOCHAR2 + pattern + ConstantsKeys.PSEUDOCHAR2;
			}
	        return pattern;
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    	}
    	return pattern;
    }
}
