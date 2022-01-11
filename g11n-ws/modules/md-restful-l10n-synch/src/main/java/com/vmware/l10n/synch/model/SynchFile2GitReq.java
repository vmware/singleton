/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.synch.model;

import java.io.Serializable;
import java.util.List;
/**
 * 
 *
 * @author shihu
 *
 */
public class SynchFile2GitReq implements Serializable{




	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2600208050207079087L;
	private String productName;
	private String version;
	private List<String> itemNames;
	private int type;
	
	public String getProductName() {
		return productName;
	}
	public String getVersion() {
		return version;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<String> getItemNames() {
		return itemNames;
	}
	public void setItemNames(List<String> itemNames) {
		this.itemNames = itemNames;
	}
	
	
}
