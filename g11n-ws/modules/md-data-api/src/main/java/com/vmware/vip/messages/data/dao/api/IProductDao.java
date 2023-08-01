/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.api;

import java.util.List;

import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * 
 *
 * @author shihu
 *
 */
public interface IProductDao {

	public List<String> getComponentList(String productName, String version) throws DataException;

	public List<String> getLocaleList(String productName, String version) throws DataException;
	
	public String getVersionInfo(String productName, String version) throws DataException;

	public List<String> getVersionList(String productName) throws DataException;
	
	public String getAllowProductListContent(String path) throws DataException;
}
