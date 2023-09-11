/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.filters;

import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class FilterUtils {
    static Logger logger = LoggerFactory.getLogger(FilterUtils.class);

    public static String getParamFromURI(ServletRequest request, String paramName) {
        HttpServletRequest res = (HttpServletRequest) request;
        String uri = res.getRequestURI();
        return getParamFromURI(uri, paramName);
    }

    public static String getParamFromURI(String uri, String paramName) {
        logger.debug("requestURI: " + uri);
        if (StringUtil.isEmpty(uri)) {
            throw new VIPJavaClientException("URI doesn't contain required parameter '" + paramName + "'!");
        }
        int paramNameIndex = uri.indexOf(paramName);
        if (paramNameIndex == -1) {
            throw new VIPJavaClientException("URI doesn't contain required parameter '" + paramName + "'!");
        }
        if ((paramNameIndex + paramName.length()) == uri.length()) {
            throw new VIPJavaClientException("URI doesn't provide value for required parameter '" + paramName + "'!");
        }
        String componentPath = uri
                .substring(paramNameIndex + paramName.length(),
                        uri.length());
        logger.debug("componentPath: " + componentPath);
        if (!componentPath.trim().startsWith("/")) {
            throw new VIPJavaClientException("URI doesn't provide value for required parameter '" + paramName + "'!");
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
        if (StringUtil.isEmpty(queryStr)) {
            throw new VIPJavaClientException("Request parameter '" + paramName + "' is required!");
        }
        int paramNameIndex = queryStr.indexOf(paramName);
        if (paramNameIndex == -1) {
            throw new VIPJavaClientException("Request parameter '" + paramName + "' is required!");
        }
        if ((paramNameIndex + paramName.length()) == queryStr.length()) {
            throw new VIPJavaClientException("Value of request parameter '" + paramName + "' must not be empty!");
        }
        String localePath = queryStr.substring(paramNameIndex
                + paramName.length(), queryStr.length());
        logger.debug("localePath: " + localePath);
        if (!localePath.trim().startsWith("=")) {
            throw new VIPJavaClientException("Request parameter '" + paramName + "' is required!");
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

    public static void printErrorMsg(ServletResponse response, String errorMsg) throws IOException {
        OutputStream os = response.getOutputStream();
        response.setContentType("application/json;charset=UTF-8");
        os.write(errorMsg.getBytes("UTF-8"));
    }
}
