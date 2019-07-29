package com.vmware.vip.test.javaclient.level2;

import java.net.MalformedURLException;
import java.util.Date;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.DateFormatting;

public class DateFormatTest extends TestBase {
	public DateFormatting dateFormatting = null;
	public Cache formatCache = null;
	@BeforeClass
	public void preparing() throws MalformedURLException {
		initVIPServer();
	}

	public void initVIPServer() throws MalformedURLException {
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		formatCache = vipCfg.createFormattingCache(FormattingCache.class);
		I18nFactory i18nFactory = I18nFactory.getInstance(vipCfg);
		dateFormatting = (DateFormatting)i18nFactory.getFormattingInstance(DateFormatting.class);
	}

	@Test(enabled=true, priority=1, dataProvider="LanguageRegion")
	@TestCase(id = "001", name = "Date_GetFormatPatternByLanguageAndRegion", priority = Priority.P1,
	description = "get format pattern by language and region")
	public void testLanguageRegion(Object amount, String dateFormatType, String timeZone,
			String language, String region, String expected, String desc) {
		try {
		String actual = dateFormatting.formatDate(amount, dateFormatType, timeZone, language, region);
		log.verifyEqual(String.format("%s, amount=%s, language=%s, region=%s",
				desc, amount, language, region), actual, expected);}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@DataProvider(name = "LanguageRegion")
	public static Object[][] getLanguageRegion() {
		final long timestamp = 1511156364801l;
		Date date = new Date(timestamp);
		final String timeZone = "GMT+8";
		return new Object[][]{
			//amount, date format type, time zone, language, region, expected, description
			{date, "fullDate", timeZone, "fr", "fr", "lundi 20 novembre 2017", "full date"},
			{date, "longDate", timeZone, "fr", "fr", "20 novembre 2017", "long date"},
			{date, "mediumDate", timeZone, "fr", "fr", "20 nov. 2017", "medium date"},
			{date, "shortDate", timeZone, "fr", "fr", "20/11/2017", "short date"},
			{date, "fullTime", timeZone, "fr", "fr", "13:39:24 GMT+08:00", "full time"},
			{date, "longTime", timeZone, "fr", "fr", "13:39:24 GMT+8", "long time"},
			{date, "mediumTime", timeZone, "fr", "fr", "13:39:24", "medium time"},
			{date, "shortTime", timeZone, "fr", "fr", "13:39", "short time"},
			{date, "full", timeZone, "fr", "fr", "lundi 20 novembre 2017 à 13:39:24 GMT+08:00", "full"},
			{date, "long", timeZone, "fr", "fr", "20 novembre 2017 à 13:39:24 GMT+8", "long"},
			{date, "medium", timeZone, "fr", "fr", "20 nov. 2017 à 13:39:24", "medium"},
			{date, "short", timeZone, "fr", "fr", "20/11/2017 13:39", "short"},
		};
	}
}
