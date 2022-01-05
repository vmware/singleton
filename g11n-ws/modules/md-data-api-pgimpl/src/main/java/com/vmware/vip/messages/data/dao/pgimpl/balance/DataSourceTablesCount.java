/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.balance;

import java.io.Serializable;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
public class DataSourceTablesCount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8650750308848330551L;

	private String datasource;
	private int count;

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
