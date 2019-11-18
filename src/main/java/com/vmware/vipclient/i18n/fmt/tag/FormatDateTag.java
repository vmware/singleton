/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.fmt.tag;

import java.util.Date;

import javax.servlet.jsp.JspTagException;

import org.apache.taglibs.standard.tag.common.fmt.FormatDateSupport;

public class FormatDateTag extends FormatDateSupport {
    public void setValue(Date value) throws JspTagException {
        this.value = value;
    }

    public void setType(String type) throws JspTagException {
        this.type = type;
    }

    public void setDateStyle(String dateStyle) throws JspTagException {
        this.dateStyle = dateStyle;
    }

    public void setTimeStyle(String timeStyle) throws JspTagException {
        this.timeStyle = timeStyle;
    }

    public void setPattern(String pattern) throws JspTagException {
        this.pattern = pattern;
    }

    public void setTimeZone(Object timeZone) throws JspTagException {
        this.timeZone = timeZone;
    }
}
