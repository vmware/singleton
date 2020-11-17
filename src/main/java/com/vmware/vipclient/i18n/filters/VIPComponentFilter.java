/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.vmware.vipclient.i18n.VIPCfgFactory;
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
        String locale = this.getParamFromQuery(request, "locale");
        String component = this.getParamFromURI(request, "component");
        Map<String, String> ctmap;
        String messages = "{}";
        if (!LocaleUtility.isDefaultLocale(locale) && translation != null) {
            ctmap = translation.getMessages(LocaleUtility.fmtToMappedLocale(locale),
                    component);
            if (ctmap != null) {
                messages = JSONObject.toJSONString(ctmap);
            }
        }
        OutputStream os = response.getOutputStream();
        response.setContentType("text/javascript;charset=UTF-8");
        VIPCfg globalCfg = VIPCfgFactory.getGlobalCfg().getVipCfg();

        os.write(("var translation = {" + "\"messages\" : " + messages + ", "
                + "\"productName\" : \"" + globalCfg.getProductName()
                + "\", " + "\"version\" : \"" + globalCfg.getVersion()
                + "\", " + "\"vipServer\" : \""
                + globalCfg.getVipServer() + "\", " + "\"pseudo\" : \""
                + globalCfg.isPseudo() + "\", "
                + "\"collectSource\" : \"" + globalCfg.isCollectSource() + "\"};")
                        .getBytes("UTF-8"));
    }

    private String getParamFromURI(ServletRequest request, String paramName) {
        HttpServletRequest res = (HttpServletRequest) request;
        String path = res.getRequestURI();
        String localepath = path
                .substring(path.indexOf(paramName) + paramName.length() + 1,
                        path.length());
        return localepath.substring(0,
                localepath.indexOf('/') >= 0 ? localepath.indexOf('/')
                        : localepath.length());
    }

    private String getParamFromQuery(ServletRequest request, String paramName) {
        HttpServletRequest res = (HttpServletRequest) request;
        String queryStr = res.getQueryString();
        String localepath = queryStr.substring(queryStr.indexOf(paramName)
                + paramName.length() + 1, queryStr.length());
        return localepath.substring(0,
                localepath.indexOf('/') >= 0 ? localepath.indexOf('/')
                        : localepath.length());
    }

    private String getSourceFromBody(ServletRequest request) {
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

    public void destroy() {
        // Do Nothing
    }

    private TranslationMessage translation;

    public void init(FilterConfig filterConfig) throws ServletException {
        VIPCfg gc = null;
        if (gc.getVipService() == null) {
            try {
                gc = VIPCfgFactory.initialize("vipconfig", true).getVipCfg();
            } catch (VIPClientInitException e) {
                logger.error(e.getMessage());
            }
        }
        gc.createTranslationCache(MessageCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
    }
}
