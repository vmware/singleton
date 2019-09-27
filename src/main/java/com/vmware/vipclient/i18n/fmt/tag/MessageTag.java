/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.fmt.tag;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import com.vmware.vipclient.i18n.fmt.common.MessageSupport;

public class MessageTag extends MessageSupport {

	/**
	 * @throws IOException 
	 */
	public MessageTag() throws IOException {
		super();
	}

	public void setKey(String key) throws JspTagException {
		this.keyAttrValue = key;
		this.keySpecified = true;
	}
}
