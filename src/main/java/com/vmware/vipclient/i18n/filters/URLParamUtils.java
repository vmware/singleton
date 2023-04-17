/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class URLParamUtils {
    static Logger logger = LoggerFactory.getLogger(URLParamUtils.class);

    public static String getParamFromURI(ServletRequest request, String paramName) {
        HttpServletRequest res = (HttpServletRequest) request;
        String uri = res.getRequestURI();
        return getParamFromURI(uri, paramName);
    }

    public static String getParamFromURI(String uri, String paramName) {
        logger.debug("requestURI: " + uri);
        if (uri == null || uri.equalsIgnoreCase("")) {
            throw new RuntimeException("URI doesn't contain required parameter '" + paramName + "'!");
        }
        int paramNameIndex = uri.indexOf(paramName);
        if (paramNameIndex == -1) {
            throw new RuntimeException("URI doesn't contain required parameter '" + paramName + "'!");
        }
        if ((paramNameIndex + paramName.length()) == uri.length()) {
            throw new RuntimeException("URI doesn't provide value for required parameter '" + paramName + "'!");
        }
        String componentPath = uri
                .substring(paramNameIndex + paramName.length(),
                        uri.length());
        logger.debug("componentPath: " + componentPath);
        if (!componentPath.trim().startsWith("/")) {
            throw new RuntimeException("URI doesn't provide value for required parameter '" + paramName + "'!");
        }
        componentPath = componentPath.trim().substring(1);
        if (componentPath.indexOf("/") > 0) {
            return componentPath.substring(0, componentPath.indexOf("/")).trim();
        } else if (componentPath.indexOf("?") > 0) {
            return componentPath.substring(0, componentPath.indexOf("?")).trim();
        } else {
            return componentPath.substring(0, componentPath.length()).trim();
        }
    }

    public static String getParamFromQuery(ServletRequest request, String paramName) {
        HttpServletRequest res = (HttpServletRequest) request;
        String queryStr = res.getQueryString();
        return getParamFromQuery(queryStr, paramName);
    }

    public static String getParamFromQuery(String queryStr, String paramName) {
        logger.debug("queryStr: " + queryStr);
        if (queryStr == null || queryStr.equalsIgnoreCase("")) {
            throw new RuntimeException("Request parameter '" + paramName + "' is required!");
        }
        int paramNameIndex = queryStr.indexOf(paramName);
        if (paramNameIndex == -1) {
            throw new RuntimeException("Request parameter '" + paramName + "' is required!");
        }
        if ((paramNameIndex + paramName.length()) == queryStr.length()) {
            throw new RuntimeException("Value of request parameter '" + paramName + "' must not be empty!");
        }
        String localePath = queryStr.substring(paramNameIndex
                + paramName.length(), queryStr.length());
        logger.debug("localePath: " + localePath);
        if (!localePath.trim().startsWith("=")) {
            throw new RuntimeException("Request parameter '" + paramName + "' is required!");
        }
        return localePath.substring(localePath.indexOf("=") + 1,
                localePath.indexOf("&") > 0 ? localePath.indexOf("&")
                        : localePath.length()).trim();
    }

    public static String getSourceFromBody(ServletRequest request) {
        BufferedReader br;
        String line;
        StringBuilder source = new StringBuilder("");
        try {
            br = request.getReader();
            while ((line = br.readLine()) != null) {
                source.append(line);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return source.toString();
    }
}
