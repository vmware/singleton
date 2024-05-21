/*
 * Copyright 2019-2024 VMware, Inc.
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

import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.util.StringUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
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
            if (!StringUtil.isEmpty(component) && !StringUtil.isEmpty(locale) && !LocaleUtility.isDefaultLocale(locale)) {
                Map<String, String> ctmap = translation.getStrings(LocaleUtility.fmtToMappedLocale(locale),
                        component);
                if (ctmap != null) {
                    messages = JSONObject.toJSONString(ctmap);
                }
            }
            OutputStream os = response.getOutputStream();
            response.setContentType("text/javascript;charset=UTF-8");
            os.write(("var translation = {" + "\"messages\" : " + messages + ", "
                    + "\"productName\" : \"" + gc.getInstance().getProductName()
                    + "\", " + "\"vipServer\" : \""
                    + gc.getInstance().getVipServer() + "\", " + "\"pseudo\" : \""
                    + gc.getInstance().isPseudo() + "\", "
                    + "\"collectSource\" : \"" + gc.getInstance().isCollectSource() + "\"};")
                    .getBytes("UTF-8"));
        } catch (VIPJavaClientException e) {
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

    /**
     * Here will create TranslationMessage instance, but create it requires I18nFactory instance created first, and creation of I18nFactory
     * requires VIPCfg instance. Hence you must create VIPCfg instance and I18nFactory instance before this filter initialize, so recommend
     * you create them at your service starts, that is in listener class that implements ServletContextListener, or will throw ServletException.
     *
     * Furthermore when initialize VIPCfg you had better rename your config file to avoid config loading error. You can write it in web.xml
     * as <context-param> to avoid hardcoding the config file in code and make the whole application share the same config.
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        I18nFactory i18n = I18nFactory.getInstance();
        try {
            translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        } catch(NullPointerException e){
            throw new ServletException("Haven't init I18nFactory, please init VIPCfg with your config first when your service starts" +
                    "(for example init VIPCfg in listener), then initialize I18nFactory with VIPCfg!", e);
        }
    }
}
