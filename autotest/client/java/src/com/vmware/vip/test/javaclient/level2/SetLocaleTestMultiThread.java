package com.vmware.vip.test.javaclient.level2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.vmware.g11n.log.TestCaseConfig;
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

public class SetLocaleTestMultiThread extends TestBase {
	public DateFormatting dateFormatting = null;
	public Cache formatCache = null;
	@BeforeClass
	public void start() throws Exception {
		TestCaseConfig testCaseConfig = new TestCaseConfig(this.getClass().getSimpleName()+"_001",
				"L2_Locale_Setting_Global", "client", "API", "p0");
		testCaseConfig.setDescription("Make sure level 2 locale can be set by LocaleUtility in global, "
				+ "all code in same thread can get this locale from LocaleUtility.getL2Locale().");
		log.testCaseBegin(testCaseConfig);
		initVIPServer();
	}

	@AfterClass
	public void end() throws Exception {
		log.testCaseEnd();
	}

	public void initVIPServer() throws MalformedURLException {
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		formatCache = vipCfg.createFormattingCache(FormattingCache.class);
		dateFormatting = (DateFormatting)I18nFactory.getInstance(vipCfg).getFormattingInstance(DateFormatting.class);
	}

    public final Date testDate = new Date(1511156364801l);
    public final String testDatePattern = "fullDate";

	@Test(threadPoolSize = 3, invocationCount = 10,  timeOut = 10000, enabled=true, priority=1)
	@TestCase(id = "001", name = "PluralTest", description = "test desc")
	public void setL2LocaleGlobal() throws IOException {
		Object[] testData  = getRandomLocaleTestData();
		Locale l2Locale = (Locale)testData[0];
		String expectedFormatting = (String)testData[1];
		String formattedDate;
		synchronized("setL2LocaleGlobal") {
			LocaleUtility.setL2Locale(l2Locale);
			doSomethingInThread();
			formattedDate = dateFormatting.formatDate(testDate, testDatePattern, LocaleUtility.getL2Locale());
		}
		log.verifyEqual("Date formatting with global locale setting by LocallUtility.", formattedDate, expectedFormatting);
	}


	private void doSomethingInThread() {
		log.info("Do something here in thread");
		for (int i=0; i<ThreadLocalRandom.current().nextInt(50); i++) {
			int m = 1;
			int n = 2;
			int sum = m + n;
		}
	}

	static Object[][] localeTranslatedDateList = new Object[][]{
		{new Locale("zh", "cn"), "2017年11月20日星期一"},
		{new Locale("zh", "tw"), "2017年11月20日 星期一"},
		{new Locale("ja"), "2017年11月20日月曜日"},
		{new Locale("de"), "Montag, 20. November 2017"},
		{new Locale("fr"), "lundi 20 novembre 2017"},
		{new Locale("ko"), "2017년 11월 20일 월요일"},
		{new Locale("es"), "lunes, 20 de noviembre de 2017"},
		{new Locale("en"), "Monday, November 20, 2017"}
	};

	/**
	 * Get random test data from list, expected result is based on final member named 'testDate' and pattern 'fullDate'
	 * @return Object[0] is Locale object, Object[1] is date formatting expected result with this locale.
	 */
	public static Object[] getRandomLocaleTestData() {
		int index = ThreadLocalRandom.current().nextInt(localeTranslatedDateList.length-1);
		return localeTranslatedDateList[index];
	}
}
