/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt.azure;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FromModel {
	public String getFromLang() {
		return fromLang;
	}

	public void setFromLang(String fromLang) {
		this.fromLang = fromLang;
	}

	public String getToLang() {
		return toLang;
	}

	public void setToLang(String toLang) {
		this.toLang = toLang;
	}

	private String fromLang;
	private String toLang;
	private List<String> sourceList;

	public List<String> getSourceList() {
		return sourceList;
	}

	public List<ObjectNode> getSourceObjectList() {
		List<ObjectNode> objectNodeList = new ArrayList<ObjectNode>();
		ObjectMapper mapper = new ObjectMapper();
		if (sourceList != null) {
			for (String source : sourceList) {
				ObjectNode node = mapper.createObjectNode();
				node.put("Text", source);
				objectNodeList.add(node);
			}
		}
		return objectNodeList;
	}

	public void setSourceList(List<String> sourceList) {
		this.sourceList = sourceList;
	}
}
