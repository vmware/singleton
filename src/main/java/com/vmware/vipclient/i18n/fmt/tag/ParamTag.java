/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.fmt.tag;

import javax.servlet.jsp.JspTagException;

import com.vmware.vipclient.i18n.fmt.common.ParamSupport;

public class ParamTag extends ParamSupport {
    public void setValue(Object value) throws JspTagException {
        this.value = value;
        this.valueSpecified = true;
    }
}
