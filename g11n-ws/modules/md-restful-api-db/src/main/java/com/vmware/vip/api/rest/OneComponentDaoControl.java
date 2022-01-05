/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.domain.DbApiUrl;
import com.vmware.vip.api.domain.DbResponseStatus;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;

/**
 * 
 *
 * @author shihu
 *
 */
@RestController
@RequestMapping(DbApiUrl.API_ROOT)
public class OneComponentDaoControl {

	@Autowired
	private IOneComponentDao compDao;

	@GetMapping(value = "/component/{productName}/{version}/{component}/{locale}/")
	public ResultI18Message get(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale) throws DataException {

		return compDao.get(productName, version, component, locale);

	}

	@PutMapping(value = "/component/{productName}/{version}/{component}/{locale}/", produces = "application/json;charset=UTF-8")
	public DbResponseStatus add(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale, @RequestBody Map<String, String> messages) {

		boolean result = false;
		try {
			result = compDao.add(productName, version, component, locale, messages);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DbResponseStatus.respFailure(1, e.getMessage());
		}

		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "add component error!");
		}

	}

	@PostMapping(value = "/component/{productName}/{version}/{component}/{locale}/", produces = "application/json;charset=UTF-8")
	public DbResponseStatus update(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale, @RequestBody Map<String, String> messages) {

		boolean result;
		try {
			result = compDao.update(productName, version, component, locale, messages);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DbResponseStatus.respFailure(1, e.getMessage());
		}

		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "update component error!");
		}

	}

	@DeleteMapping(value = "/component/{productName}/{version}/{component}/{locale}/")
	public DbResponseStatus delete(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale) {
		boolean result;
		try {
			result = compDao.delete(productName, version, component, locale);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DbResponseStatus.respFailure(1, e.getMessage());
		}

		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "delete component error!");
		}

	}
}
