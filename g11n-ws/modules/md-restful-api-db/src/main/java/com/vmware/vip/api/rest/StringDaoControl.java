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
import com.vmware.vip.api.domain.RequestLists;
import com.vmware.vip.messages.data.dao.api.IStringDao;
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
public class StringDaoControl {

	@Autowired
	private IStringDao stringDao;

	@PostMapping(value = "/multString/{productName}/{version}/{component}/{locale}/", produces = "application/json;charset=UTF-8")
	public ResultI18Message getbykeys(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale, @RequestBody RequestLists list)
			throws DataException {
		return stringDao.getBykeys(productName, version, component, locale, list.getKeys());
	}

	@GetMapping(value = "/string/{productName}/{version}/{component}/{locale}/{key}/")
	public String get2JsonStr(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale, @PathVariable String key)
			throws DataException {
		return stringDao.get2JsonStr(productName, version, component, locale, key);
	}

	@DeleteMapping(value = "/string/{productName}/{version}/{component}/{locale}/{key}/")
	public DbResponseStatus delete(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale, @PathVariable String key) {
		boolean result;
		try {
			result = stringDao.delete(productName, version, component, locale, key);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DbResponseStatus.respFailure(1, e.getMessage());
		}
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "delete string error!");
		}

	}

	@PutMapping(value = "/string/{productName}/{version}/{component}/{locale}/", produces = "application/json;charset=UTF-8")
	public DbResponseStatus add(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale, @RequestBody Map<String, String> messages) {
		boolean result;
		try {
			result = stringDao.add(productName, version, component, locale, messages);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DbResponseStatus.respFailure(1, e.getMessage());
		}
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "add string error!");
		}

	}

	@PostMapping(value = "/string/{productName}/{version}/{component}/{locale}/", produces = "application/json;charset=UTF-8")
	public DbResponseStatus update(@PathVariable String productName, @PathVariable String version,
			@PathVariable String component, @PathVariable String locale, @RequestBody Map<String, String> messages) {
		boolean result;
		try {
			result = stringDao.update(productName, version, component, locale, messages);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return DbResponseStatus.respFailure(1, e.getMessage());
		}
		if (result) {
			return DbResponseStatus.respSuccess(0);
		} else {
			return DbResponseStatus.respFailure(1, "update string error!");
		}

	}

}
