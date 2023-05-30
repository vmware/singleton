/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.PatternMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.util.StringUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.util.LocaleUtility;

/**
 * This class is specified as a filter in web.xml
 *
 */
public class VIPPatternFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(VIPPatternFilter.class);

    private PatternMessage patternMessage;
    private VIPCfg gc = VIPCfg.getInstance();

    @Override
    public void doFilter(final ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            String locale = FilterUtils.getParamFromQuery(request, "locale");
            logger.debug("locale: " + locale);

            String patterns = "{}";
            if (!StringUtil.isEmpty(locale) && !LocaleUtility.isDefaultLocale(locale)) {
                Map<String, String> ctmap = patternMessage.getPatternMessage(Locale.forLanguageTag(locale));
                if (ctmap != null) {
                    patterns = JSONObject.toJSONString(ctmap);
                }
            }
            OutputStream os = response.getOutputStream();
            response.setContentType("text/javascript;charset=UTF-8");
            os.write(("var localeData =" + patterns).getBytes("UTF-8"));
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            String errorMsg = "{\"code\":400, \"message\": \""+e.getMessage()+"\"}";
            FilterUtils.printErrorMsg(response, errorMsg);
        }
    }

    @Override
    public void destroy() {
        // Do Nothing
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (gc.getVipService() == null) {
            try {
                gc.initialize("vipconfig");
            } catch (VIPClientInitException e) {
                logger.error(e.getMessage());
            }
            gc.initializeVIPService();
        }
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        patternMessage = (PatternMessage) i18n.getMessageInstance(PatternMessage.class);
    }
}
