package com.vmware.vip.test.javaclient.level2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Locale;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.DateFormatting;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class SetLocaleTest extends TestBase {
	public DateFormatting dateFormatting = null;
	public Cache formatCache = null;
	public final Date testDate = new Date(1511156364801l);
    public final String testDatePattern = "fullDate";
	@BeforeClass
	public void preparing() throws MalformedURLException {
		initVIPServer();
	}

	public void initVIPServer() throws MalformedURLException {
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		formatCache = vipCfg.createFormattingCache(FormattingCache.class);
		dateFormatting = (DateFormatting)I18nFactory.getInstance(vipCfg).getFormattingInstance(DateFormatting.class);
	}

	@Test(enabled=true)
	@TestCase(id = "001", name = "L2_Locale_Setting_By_Parameter",
	description = "Set level 2 locale")
	public void setL2LocaleByParameter() throws IOException {
		Locale l2Locale = new Locale("zh", "cn");
		String s = dateFormatting.formatDate(testDate, testDatePattern, l2Locale);
		log.verifyEqual("Date formatting with global locale setting by LocallUtility.", s, "2017年11月20日星期一");
	}

	@Test(enabled=true)
	@TestCase(id = "002", name = "L2_Locale_Setting_Override",
	description = "Set level 2 locale")
	public void SetLocaleOverGlobalSetting() throws IOException {
		Locale l2Locale = new Locale("ja");
		LocaleUtility.setL2Locale(l2Locale);
		Locale overrideLocale = new Locale("ko");
		String s = dateFormatting.formatDate(testDate, testDatePattern, overrideLocale);
		log.verifyEqual("Date formatting with global locale setting by LocallUtility.", s, "2017년 11월 20일 월요일");
	}
}
