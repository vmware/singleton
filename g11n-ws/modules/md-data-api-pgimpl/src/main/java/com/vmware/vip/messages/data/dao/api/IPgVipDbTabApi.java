/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.api;

import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
public interface IPgVipDbTabApi {
	/**
	 * 
	 * @param product
	 * @return the register result in db
	 */
	public boolean registerProduct(String product);

	public boolean refreshDataNodes();

	public boolean delProduct(String product) throws DataException;
	
	public boolean clearProductData(String product, String version);

	public boolean productIsRegistered(String product);
}
