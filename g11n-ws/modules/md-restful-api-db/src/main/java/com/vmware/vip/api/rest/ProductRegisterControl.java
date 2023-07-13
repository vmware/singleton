/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.domain.DbApiUrl;
import com.vmware.vip.api.domain.DbResponseStatus;
import com.vmware.vip.messages.data.dao.api.IPgVipDbTabApi;
import com.vmware.vip.messages.data.dao.exception.DataException;


/**
 * 
 *
 * @author shihu
 *
 */
@RestController
@RequestMapping(DbApiUrl.API_ROOT)
public class ProductRegisterControl {

	@Autowired
	private IPgVipDbTabApi dbtab;

	@Operation(summary = "get product", description = "get product from vip DB")

	@GetMapping(value = "/product/{productName}")
	public DbResponseStatus getProduct(@Parameter(name = "productName", description = "product Name", required = true) @PathVariable String productName) {

		boolean result = dbtab.productIsRegistered(productName);
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "register product error!");
		}

	}

	@Operation(summary = "register product", description = "customer need register product to vip DB")
	@PutMapping(value = "/product/{productName}")
	public DbResponseStatus addProduct(@Parameter(name = "productName", description = "product Name", required = true) @PathVariable String productName) {

		boolean result = dbtab.registerProduct(productName);
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "register product error!");
		}

	}

	@Operation(summary = "delete product", description = "delete product to vip DB")
	@DeleteMapping(value = "/product/{productName}")
	public DbResponseStatus deleteProduct(@Parameter(name = "productName", description = "product Name", required = true) @PathVariable String productName) throws DataException {

		boolean result = dbtab.delProduct(productName);
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "delete product error!");
		}

	}
}
