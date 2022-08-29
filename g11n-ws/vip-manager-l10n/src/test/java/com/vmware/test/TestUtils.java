/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.source.dao.AllowListDao;
import com.vmware.l10n.source.dto.GRMAPIResponseStatus;
import com.vmware.l10n.utils.SourceUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestUtils {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	public void test001sourceUtils() {
		SourceUtils.createSourceDTO("TEST", "1.0.0", "test", "en", "test.key", "this is test", "", "");
		SourceUtils.handleSourceResponse(true);
		SourceUtils.handleSourceResponse(false);
	}

	@Test
	public void testGRMAPIResponseStatus() {
		System.out.println(GRMAPIResponseStatus.CREATED.getMessage());
		System.out.println(GRMAPIResponseStatus.INTERNAL_SERVER_ERROR.getMessage());
		System.out.println(GRMAPIResponseStatus.INVALID_REQUEST.getMessage());
		System.out.println(GRMAPIResponseStatus.UNAUTHORIZED.getMessage());
	}

	@Test
	public void testAllowList() {
		AllowListDao allowListUtils = webApplicationContext.getBean(AllowListDao.class);
		allowListUtils.getAllowList();
	}
	
	
}