/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.model;

import java.io.Serializable;
import java.nio.channels.ReadableByteChannel;

public class ResultMessageChannel implements Serializable{

	private static final long serialVersionUID = 3241337938771784142L;
	

	private String component;
	private String locale;
	private ReadableByteChannel readableByteChannel;
	
	public ResultMessageChannel(String component, String locale, ReadableByteChannel readableByteChannel) {
		this.component = component;
		this.locale = locale;
		this.readableByteChannel = readableByteChannel;
	}
	
	public String getComponent() {
		return component;
	}
	public String getLocale() {
		return locale;
	}
	public ReadableByteChannel getReadableByteChannel() {
		return readableByteChannel;
	}
	public boolean hasChannel() {
		return (readableByteChannel != null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((component == null) ? 0 : component.hashCode());
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultMessageChannel other = (ResultMessageChannel) obj;
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
			return false;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		return true;
	}
	
	public String generateNullMessage() {
		StringBuilder sb = new StringBuilder();
	 	sb.append("{\r\n    \"component\": \""+this.component+"\",\r\n" );
	 	sb.append("    \"messages\": null ,\r\n" );
    	sb.append("    \"locale\": \""+this.locale+"\",\r\n}" );
		return sb.toString();
	}
	
}
