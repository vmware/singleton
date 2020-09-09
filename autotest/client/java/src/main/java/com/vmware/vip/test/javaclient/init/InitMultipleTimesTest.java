/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.javaclient.init;

import java.util.List;

import org.testng.annotations.Test;

import com.vmware.vip.test.common.TestGroups;
import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.ClientConfigHelper;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.base.DataSourceEnum;

public class InitMultipleTimesTest extends TestBase{
	@Test(enabled=true, priority=0, groups=TestGroups.BUG)
	@TestCase(id = "001", name = "MsgOriginsQueue_Clear_Test", priority=Priority.P0,
	description = "MsgOriginsQueue should be cleared when initializing VIPCfg. bug from https://github.com/vmware/singleton/issues/746")
	public void initExternalProperties() throws Exception {
		int times = 4;
		log.info(String.format("Initializing VIPCfg for %s times", times));
		for (int i=0; i<times; i++) {
			vipCfg.initialize(Utils.removeFileExtension(ClientConfigHelper.CONFIG_TEMPLATE_MIX));
		}
		List<DataSourceEnum> queue = vipCfg.getMsgOriginsQueue();
		log.verifyEqual("MsgOriginsQueue size should be 2", queue.size(), 2);
		log.verifyTrue("One of MsgOriginsQueue is VIP", queue.contains(DataSourceEnum.VIP));
		log.verifyTrue("One of MsgOriginsQueue is Bundle", queue.contains(DataSourceEnum.Bundle));
	}
}
