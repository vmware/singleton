/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.conf.RsaCryptUtil;
import com.vmware.l10n.source.dto.GRMAPIResponseStatus;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.l10n.utils.AllowListUtils;

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
		AllowListUtils allowListUtils = webApplicationContext.getBean(AllowListUtils.class);		allowListUtils.getAllowList();
	}
	
	@Test
	public void testRSAUtil() {
		File file = new File("testRSA.test");
		try {
			if(file.createNewFile()){
				RsaCryptUtil.getPublicKeyStrFromFile(file);
			}
			file.deleteOnExit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}