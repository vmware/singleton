/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.domain.DbApiUrl;
import com.vmware.vip.messages.data.dao.api.IProductDao;
import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * 
 *
 * @author shihu
 *
 */
@RestController
@RequestMapping(DbApiUrl.API_ROOT)
public class ProductDaoControl {
	@Autowired

	private IProductDao product;

	@GetMapping(value = "/componentList/{productName}/{version}/")
	public List<String> getComponentList(@PathVariable String productName, @PathVariable String version)
			throws DataException {
		return product.getComponentList(productName, version);

	}

	@GetMapping(value = "/localeList/{productName}/{version}/")
	public List<String> getLocaleList(@PathVariable String productName, @PathVariable String version)
			throws DataException {
		return product.getLocaleList(productName, version);

	}

}
