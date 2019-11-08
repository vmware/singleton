/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.fmt.common;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.taglibs.standard.resources.Resources;

public class ParamSupport extends BodyTagSupport {

    protected Object  value;
    protected boolean valueSpecified;

    public ParamSupport() {
        init();
    }

    private void init() {
        this.value = null;
        this.valueSpecified = false;
    }

    public int doEndTag() throws JspException {
        Tag t = findAncestorWithClass(this, MessageSupport.class);
        if (t == null) {
            throw new JspTagException(
                    Resources.getMessage("PARAM_OUTSIDE_MESSAGE"));
        }
        MessageSupport parent = (MessageSupport) t;

        Object input = null;

        if (this.valueSpecified) {
            input = this.value;
        } else {
            input = this.bodyContent.getString().trim();
        }
        parent.addParam(input);

        return 6;
    }

    public void release() {
        init();
    }
}
