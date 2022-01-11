/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.domain.DbApiUrl;
import com.vmware.vip.api.domain.RequestLists;
import com.vmware.vip.messages.data.dao.api.IMultComponentDao;
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
public class MultComponentDaoControl {

	@Autowired
	private IMultComponentDao multComp;

	// produces = "application/json"
	@PostMapping(value = "/multComponent/{productName}/{version}/", produces = "application/json;charset=UTF-8")
	public List<ResultI18Message> get2JsonStrs(@PathVariable String productName, @PathVariable String version,
			@RequestBody RequestLists list) throws DataException {
		return multComp.get(productName, version, list.getComponents(), list.getLocales());

	}

}
