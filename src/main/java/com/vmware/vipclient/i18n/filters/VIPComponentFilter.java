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

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.util.LocaleUtility;

/**
 * This class is specified as a filter in web.xml
 *
 */
public class VIPComponentFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(VIPComponentFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            String component = FilterUtils.getParamFromURI(request, "component");
            logger.debug("component: " + component);
            String locale = FilterUtils.getParamFromQuery(request, "locale");
            logger.debug("locale: " + locale);
            String messages = "{}";
            if (!StringUtil.isEmpty(component) && !StringUtil.isEmpty(locale)) {
                if (!LocaleUtility.isDefaultLocale(locale) && translation != null) {
                    Map<String, String> ctmap = translation.getMessages(LocaleUtility.fmtToMappedLocale(locale),
                            component);
                    if (ctmap != null) {
                        messages = JSONObject.toJSONString(ctmap);
                    }
                }
            }
            OutputStream os = response.getOutputStream();
            response.setContentType("text/javascript;charset=UTF-8");
            os.write(("var translation = {" + "\"messages\" : " + messages + ", "
                    + "\"productName\" : \"" + gc.getInstance().getProductName()
                    + "\", " + "\"version\" : \"" + gc.getInstance().getVersion()
                    + "\", " + "\"vipServer\" : \""
                    + gc.getInstance().getVipServer() + "\", " + "\"pseudo\" : \""
                    + gc.getInstance().isPseudo() + "\", "
                    + "\"collectSource\" : \"" + gc.getInstance().isCollectSource() + "\"};")
                    .getBytes("UTF-8"));
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            String errorMsg = "{\"code\":400, \"message\": \""+e.getMessage()+"\"}";
            FilterUtils.printErrorMsg(response, errorMsg);
        }
    }

    public void destroy() {
        // Do Nothing
    }

    private TranslationMessage translation;
    private VIPCfg             gc = VIPCfg.getInstance();

    public void init(FilterConfig filterConfig) throws ServletException {
        if (gc.getVipService() == null) {
            try {
                gc.initialize("vipconfig");
            } catch (VIPClientInitException e) {
                logger.error(e.getMessage());
            }
            gc.initializeVIPService();
        }
        gc.createTranslationCache(MessageCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
    }
}
