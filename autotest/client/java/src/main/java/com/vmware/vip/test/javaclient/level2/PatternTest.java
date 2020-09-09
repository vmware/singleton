/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.javaclient.level2;
import java.util.Locale;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.TestGroups;
import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;

public class PatternTest extends TestBase {
	public NumberFormatting numberFormatting = null;
	public Cache formatCache = null;
	@BeforeClass
	public void preparing() throws Exception {
		initVIPServer();
	}

	public void initVIPServer() throws Exception {
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		formatCache = vipCfg.createFormattingCache(FormattingCache.class);
		numberFormatting = (NumberFormatting)I18nFactory.getInstance(vipCfg).getFormattingInstance(NumberFormatting.class);
	}

	@Test(enabled=false, priority=0, groups=TestGroups.BUG)
	@TestCase(id = "001", name = "NoExceptionWithInvalidLocale", priority = Priority.P0,
	description = "https://github.com/vmware/singleton/issues/655")
	public void getPatternWithInvalidLocale() {
		try {
			String actual = numberFormatting.formatCurrency(123, new Locale("invalid"));
			log.verifyEqual("Fallback to default locale", actual, "$123.00");//TODO if default locale is not en
		} catch (Exception e) {
			log.verifyNull("No exception with invalid locale", e, e.toString());
		}
		try {
			String actual = numberFormatting.formatCurrency(123, "invalid", "invalid");
			log.verifyEqual("Fallback to default locale", actual, "$123.00");//TODO if default locale is not en
		} catch (Exception e) {
			log.verifyNull("No exception with invalid language and region", e, e.toString());
		}
	}

	@Test(enabled=true, priority=0, groups=TestGroups.BUG)
	@TestCase(id = "002", name = "NoExceptionWithUnreachableService", priority = Priority.P0,
	description = "https://github.com/vmware/singleton/issues/656")
	public void getPatternWithUnreachableService() {
		String originURL = vipCfg.getVipService().getHttpRequester().getBaseURL();
		try {
			vipCfg.getVipService().getHttpRequester().setBaseURL("https://unreachable.com:8090");
			String actual = numberFormatting.formatCurrency(123, new Locale("fr"));
			log.verifyEqual("Get pattern from CLDR", actual, "123,00 $US");
		} catch (Exception e) {
			log.error(e.toString());
			log.verifyNull("No exception with unreachable service", e);
		} finally {
			vipCfg.getVipService().getHttpRequester().setBaseURL(originURL);
		}
	}
}
