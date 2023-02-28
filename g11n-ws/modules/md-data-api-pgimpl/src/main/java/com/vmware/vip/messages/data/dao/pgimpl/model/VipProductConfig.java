/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * @author shihu
 *
 */
public class VipProductConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4382166291283627991L;

	private long id;
	private String product;
	private String datasource;
	private int status;
	private long created_userid;
	private Date crt_time;
	private int datasourceCount;

	public VipProductConfig() {

	}

	public VipProductConfig(String product, String datasource) {
		this.product = product;
		this.datasource = datasource;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreated_userid() {
		return created_userid;
	}

	public void setCreated_userid(long created_userid) {
		this.created_userid = created_userid;
	}

	public Date getCrt_time() {
		return crt_time;
	}

	public void setCrt_time(Date crt_time) {
		this.crt_time = crt_time;
	}

	public int getDatasourceCount() {
		return datasourceCount;
	}

	public void setDatasourceCount(int datasourceCount) {
		this.datasourceCount = datasourceCount;
	}
}
