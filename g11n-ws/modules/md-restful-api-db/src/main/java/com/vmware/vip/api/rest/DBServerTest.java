/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.domain.DbApiUrl;

import io.swagger.annotations.ApiOperation;

/**
 * 
 *
 * @author shihu
 *
 */
@RestController
@RequestMapping(DbApiUrl.API_ROOT)
public class DBServerTest {
	@ApiOperation(value = "获取用户详细信息", notes = "根据url的id来获取用户详细信息")
	@GetMapping("/test")
	public String test() {
		return "this is a test!!!";
	}

}
