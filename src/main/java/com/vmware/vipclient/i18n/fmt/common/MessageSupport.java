/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.fmt.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.taglibs.standard.tag.common.core.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class MessageSupport extends BodyTagSupport {
    private Logger logger = LoggerFactory.getLogger(MessageSupport.class);

    public static final Locale defaultLocale = new Locale("en", "US");
    private PageContext        pageContext;
    protected String           keyAttrValue;
    protected boolean          keySpecified;
    private String             var;
    private int                scope;
    private List               params;
    private String             component     = "JSP", bundle = "webui";
    private TranslationMessage translation;

    public MessageSupport() {
        this.params = new ArrayList();
        init();
    }

    private void init() {
        this.var = null;
        this.scope = 1;
        this.keyAttrValue = null;
        this.keySpecified = false;
    }

    public int doStartTag() throws JspException {
        this.params.clear();
        return 2;
    }

    public int doEndTag() throws JspException {
        String key = null;
        if (this.keySpecified)
            key = this.keyAttrValue;
        else if ((this.bodyContent != null)
                && (this.bodyContent.getString() != null)) {
            key = this.bodyContent.getString().trim();
        }
        if ((key == null) || (key.equals(""))) {
            try {
                this.pageContext.getOut().print("Key is null");
            } catch (IOException ioe) {
                throw new JspTagException(ioe.toString(), ioe);
            }
            return 6;
        }
        Locale locale = LocaleUtility.getLocale();
        Object[] args = this.params.isEmpty() ? null : this.params.toArray();
        try{
            I18nFactory i18n = I18nFactory.getInstance();
            translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        } catch(NullPointerException e){
            throw new JspTagException("Haven't init I18nFactory, please init VIPCfg with your config first when your service starts" +
                    "(for example init VIPCfg in listener), then initialize I18nFactory with VIPCfg!", e);
        }
        String message = translation.getString2(component, bundle, locale, key, "TranslationCache", args);
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, message, this.scope);
        } else {
            try {
                this.pageContext.getOut().write(message);
            } catch (IOException ioe) {
                throw new JspTagException(ioe.toString(), ioe);
            }
        }
        return 0;
    }

    public Tag getParent() {
        return null;
    }

    public void release() {
        init();
    }

    public void setPageContext(PageContext arg0) {
        this.pageContext = arg0;
    }

    public void setParent(Tag arg0) {
    }

    public String getVar() {
        return this.var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public int getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = Util.getScope(scope);
    }

    public void addParam(Object arg) {
        this.params.add(arg);
    }
}
