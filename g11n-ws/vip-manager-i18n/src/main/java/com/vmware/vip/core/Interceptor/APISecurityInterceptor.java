/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.Interceptor;

import java.io.PrintWriter;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;

/**
 * Security interceptor, this is for sessionID and toksn validation and Authentication requests legitimacy 
 */
public class APISecurityInterceptor implements HandlerInterceptor {

    private Logger LOGGER = LoggerFactory.getLogger(APISecurityInterceptor.class);

    /**
     * The main method of the security interceptor, this method business logic will be executed
     * in WebConfiguration java class for security Authentication
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || !validateToken(request)
                || !verifyRightForResouceAccess(request)) {
            return responseError(response, APIResponseStatus.UNAUTHORIZED);
        }
        return true;
    }

    /**
     * Verify token exist in session
     *
     * @param request 
     * @return if true, Authentication is passed, otherwise not
     */
    private boolean validateToken(HttpServletRequest request) {
        String requestToken = request.getHeader(ConstantsKeys.TOKEN);
        String sessionToken = (String) request.getSession().getAttribute(ConstantsKeys.TOKEN);
        return sessionToken.equals(requestToken);
    }

    /**
     * Get data and verify token
     *
     * @param request HttpServletRequest object
     * @return If true, authentication is passed, otherwise not
     */
    @SuppressWarnings("unchecked")
    private boolean verifyRightForResouceAccess(HttpServletRequest request) {
        String token = request.getHeader(ConstantsKeys.TOKEN);
        if (StringUtils.isEmpty(token))
            return false;
        String[] tArray = token.split(ConstantsKeys.VIP.hashCode() + "");
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String productName = pathVariables.get(ConstantsKeys.PRODUCTNAME) == null
                ? request.getParameter(ConstantsKeys.PRODUCTNAME) : pathVariables.get(ConstantsKeys.PRODUCTNAME);
        String version = pathVariables.get(ConstantsKeys.VERSION) == null
                ? request.getParameter(ConstantsKeys.VERSION) : pathVariables.get(ConstantsKeys.VERSION);
        if (!StringUtils.isEmpty(productName) && !StringUtils.isEmpty(version)) {
            String productName_version = tArray[0];
            if (!productName_version.equals((productName + ConstantsChar.UNDERLINE + version).hashCode()+""))
                return false;
        }
        if (token.indexOf(String.valueOf(ConstantsKeys.VIP.hashCode())) <= 0)
            return false;
        return true;
    }

    /**
     * Encapsulates response error message
     *
     * @param response HttpServletResponse object
     * @param status The response status, encapsulates the result into http response for client
     * @return A boolean result
     * @throws Exception
     */
    private boolean responseError(HttpServletResponse response, Response status)
            throws Exception {
        LOGGER.error("Error code:" + status.getCode() + ",error message :" + status.getMessage());
        response.setStatus(status.getCode());
        response.setContentType(ConstantsKeys.CONTENT_TYPE_JSON);
        PrintWriter out = response.getWriter();
        out.write("{\"status\":{\"code\":" + status.getCode() + ",\"message\":\""
                + status.getMessage() + "\"}}");
        out.flush();
        out.close();
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {

    }

}
