/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.vmware.vipclient.i18n.util.StringUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.messages.service.PatternService;
import com.vmware.vipclient.i18n.util.LocaleUtility;

/**
 * This class is specified as a filter in web.xml
 *
 */
public class VIPPatternFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(VIPPatternFilter.class);

    @Override
    public void doFilter(final ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        String locale = null;
        try {
            locale = URLParamUtils.getParamFromQuery(request, "locale");
            logger.debug("locale: " + locale);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        String messages = "{}";
        if (!StringUtil.isEmpty(locale) && !LocaleUtility.isDefaultLocale(locale)) {
            Map<String, String> ctmap = new PatternService().getPatterns(locale);
            if (ctmap != null) {
                messages = JSONObject.toJSONString(ctmap);
            }
        }
        OutputStream os = response.getOutputStream();
        response.setContentType("text/javascript;charset=UTF-8");
        os.write(("var localeData =" + messages).getBytes("UTF-8"));
    }

    @Override
    public void destroy() {
        // Do Nothing
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do Nothing
    }
}
