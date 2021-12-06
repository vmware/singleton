/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.record.model.SyncRecordModel;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestSqlLiteDao {

	private static int grmFlag =1;
	private static int singletonFlag = 2;
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Test
	public void test001createSyncRecord() {
		SqlLiteDaoImpl dao = webApplicationContext.getBean(SqlLiteDaoImpl.class);
		ComponentSourceDTO csd = new ComponentSourceDTO();
		csd.setProductName("unitTest");
		csd.setVersion("1.0.0");
		csd.setComponent("default");
		csd.setLocale("en");
		dao.createSyncRecord(csd, grmFlag, System.currentTimeMillis());
		
		Assert.assertNotNull(dao);
	}
	@Test
	public void test002getSyncRecord() {
		SqlLiteDaoImpl dao = webApplicationContext.getBean(SqlLiteDaoImpl.class);
		dao.getSynRecords(grmFlag);
		dao.getSynRecords(singletonFlag);
		
		Assert.assertNotNull(dao);
	}
	
	@Test
	public void test003deleteSyncRecord() {
		SyncRecordModel csd = new SyncRecordModel();
		SqlLiteDao dao = webApplicationContext.getBean(SqlLiteDao.class);
		csd.setProduct("unitTest");
		csd.setVersion("1.0.0");
		csd.setComponent("default");
		csd.setLocale("en");
		csd.setType(grmFlag);
		dao.deleteSyncRecord(csd);
		
		Assert.assertNotNull(dao);
	}
	
}
