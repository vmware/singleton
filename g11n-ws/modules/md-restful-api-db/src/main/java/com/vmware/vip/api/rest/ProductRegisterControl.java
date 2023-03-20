/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

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

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

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

	@ApiOperation(value = "get product", notes = "get product from vip DB")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "productName", value = "product Name", required = true, dataType = "String", paramType = "path") })

	@GetMapping(value = "/product/{productName}")
	public DbResponseStatus getProduct(@PathVariable String productName) {

		boolean result = dbtab.productIsRegistered(productName);
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "register product error!");
		}

	}

	@ApiOperation(value = "register product", notes = "customer need register product to vip DB")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "productName", value = "product Name", required = true, dataType = "String", paramType = "path") })
	@PutMapping(value = "/product/{productName}")
	public DbResponseStatus addProduct(@PathVariable String productName) {

		boolean result = dbtab.registerProduct(productName);
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "register product error!");
		}

	}

	@ApiOperation(value = "delete product", notes = "delete product to vip DB")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "productName", value = "product Name", required = true, dataType = "String", paramType = "path") })
	@DeleteMapping(value = "/product/{productName}")
	public DbResponseStatus deleteProduct(@PathVariable String productName) throws DataException {

		boolean result = dbtab.delProduct(productName);
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "delete product error!");
		}

	}
}
