package com.vmware.vip.test.javaclient.level2;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.TestGroups;
import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;

public class CurrencyTest extends TestBase {
	public NumberFormatting numberFormatting = null;
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
		numberFormatting = (NumberFormatting)I18nFactory.getInstance(vipCfg).getFormattingInstance(NumberFormatting.class);
	}

	@Test(enabled=true, priority=1, dataProvider="defaultCurrencyCode")
	@TestCase(id = "001", name = "CurrencyTest_DefaultCurrencyCode", priority = Priority.P1,
	description = "USD will be used as default currency.")
	public void testDefaultCurrencyCode(Object amount, Locale locale, String expected) {
		String actual = numberFormatting.formatCurrency(amount, locale);
		log.verifyEqual(String.format("amount=%s, locale=%s",
				amount, locale), actual, expected);
	}

	@Test(enabled=true, priority=1, dataProvider="customCurrencyCode")
	@TestCase(id = "002", name = "CurrencyTest_CustomCurrencyCode", priority = Priority.P1,
	description = "Custom currency code will be used.")
	public void testCustomCurrencyCode(Object amount, String currencyCode, Locale locale, String expected) {
		String actual = numberFormatting.formatCurrency(amount, currencyCode, locale);
		log.verifyEqual(String.format("amount=%s, currencyCode=%s, locale=%s",
				amount, currencyCode, locale), actual, expected);
	}

	@Test(enabled=true, priority=1, dataProvider="rouding")
	@TestCase(id = "003", name = "CurrencyTest_Round",  priority = Priority.P1,
	description = "Test rounding correctly, using halfeven.")
	public void testRounding(Object amount, String currencyCode, Locale locale, String expected) {
		String actual = numberFormatting.formatCurrency(amount, currencyCode, locale);
		log.verifyEqual(String.format("amount=%s, currencyCode=%s, locale=%s",
				amount, currencyCode, locale), actual, expected);
	}

	@Test(enabled=true, priority=1, dataProvider="supplementalOverride")
	@TestCase(id = "004", name = "CurrencyTest_SupplementalOverride",
	description = "Rouding and digits will be overrided if supplemental data existed in 'currencies' categary,"
			+ "pattern data will not be used.")
	public void testSupplemental(Object amount, String currencyCode, Locale locale, String expected) {
		String actual = numberFormatting.formatCurrency(amount, currencyCode, locale);
		log.verifyEqual(String.format("amount=%s, currencyCode=%s, locale=%s",
				amount, currencyCode, locale), actual, expected);
	}

	@Test(enabled=true, priority=1, dataProvider="amountType")
	@TestCase(id = "005", name = "CurrencyTest_AmountDataType",
	description = "Should be able to handle all kinds of number data type")
	public void testAmountType(Object amount, String dataTypeStr, String currencyCode, Locale locale, String expected) {
		String actual = numberFormatting.formatCurrency(amount, currencyCode, locale);
		log.verifyEqual(String.format("amount=%s, dataType=%s, currencyCode=%s, locale=%s",
				amount, dataTypeStr, currencyCode, locale), actual, expected);
	}

	//	@Test(threadPoolSize = 3, invocationCount = 10,  timeOut = 10000,
	//			enabled=true, priority=1)
	//	@TestCase(id = "006", name = "CurrencyTest_MultipleThread", priority = Priority.P1,
	//	description = "Multiple thread testing, each thread using different test data should get correct result.")
	//	public void testMultipleThread() {
	//		Object[] dataList = getRandomTestData();
	//		Object amount = dataList[0];
	//		String amountDataType = (String)dataList[1];
	//		String currencyCode = (String)dataList[2];
	//		Locale locale = (Locale)dataList[3];
	//		String expected = (String)dataList[4];
	//		doSomethingInThread();
	//		String actual = numberFormatting.formatCurrency(amount, currencyCode, locale);
	//		log.verifyEqual(String.format("amount=%s, dataType=%s, currencyCode=%s, locale=%s",
	//				amount, amountDataType, currencyCode, locale), actual, expected);
	//	}

	@Test(enabled=true, priority=1, dataProvider="fallback")
	@TestCase(id = "007", name = "CurrencyTest_DisconnectFallback", priority = Priority.P1,
	description = "Disconnect VIP server, cannot get locale pattern, so get en pattern from local.")
	public void testFallback(Object amount, String currencyCode, Locale locale, String expected) {
		try {
			formatCache.clear();
			VIPCfg vipCfg = VIPCfg.getInstance();
			vipCfg.getVipService().getHttpRequester().setBaseURL("notexist:8090");
			NumberFormatting myFormatting = (NumberFormatting)I18nFactory.getInstance(vipCfg).getFormattingInstance(NumberFormatting.class);
			String actual = myFormatting.formatCurrency(amount, currencyCode, locale);
			log.verifyEqual(String.format("amount=%s, currencyCode=%s, locale=%s",
					amount, currencyCode, locale), actual, expected);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			formatCache.clear();
			VIPCfg vipCfg = VIPCfg.getInstance();
			vipCfg.getVipService().getHttpRequester().setBaseURL("https://"+vipCfg.getVipServer());
		}
	}

	@Test(enabled=true, priority=1, groups= {TestGroups.NEGATIVE, TestGroups.DYNAMIC_PROPERTY})
	@TestCase(id = "008", name = "CurrencyTest_InvalidPatternScope", priority = Priority.P1,
	description = "If there is no 'currencies' scope in VIP configuration properties file, exception will be thrown.")
	public void testInvalidPatternScope() throws MalformedURLException {
		formatCache.clear();
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.setI18nScope("noscope");
		try {
			numberFormatting.formatCurrency(123, "CNY", new Locale("zh", "CN"));
			log.verifyTrue("invalid pattern scope argument will raise 'IllegalArgumentException'", false);
		} catch (IllegalArgumentException e) {
			log.verifyTrue("invalid pattern scope argument will raise 'IllegalArgumentException'", true);
		} finally{
			initVIPServer();//set vip configuration back
		}
	}

	@Test(enabled=true, priority=1, groups= {TestGroups.NEGATIVE})
	@TestCase(id = "009", name = "CurrencyTest_InvalidCurrencyCode", priority = Priority.P1,
	description = "Throw exception when currency code in invalid.(Currency code should be a 3-letter alpha code)")
	public void testInvalidCurrencyCode() {
		try {
			numberFormatting.formatCurrency(123, "0", new Locale("zh", "CN"));
		} catch (IllegalArgumentException e) {
			log.verifyTrue("invalid currency code argument will raise 'IllegalArgumentException'", true);
		}
	}

	@Test(enabled=true, priority=1, dataProvider="LanguageRegion")
	@TestCase(id = "010", name = "Currency_GetFormatPatternByLanguageAndRegion", priority = Priority.P1,
	description = "get format pattern by language and region")
	public void testLanguageRegion(Object amount, String language, String region, String expected, String desc) {
		String actual = numberFormatting.formatCurrency(amount, language, region);
		log.verifyEqual(String.format("%s, amount=%s, language=%s, region=%s",
				desc, amount, language, region), actual, expected);
	}

	@Test(enabled=true, priority=1, dataProvider="LanguageRegionWithCurrencyCode")
	@TestCase(id = "011", name = "Currency_GetFormatPatternByLanguageAndRegion_WithCurrencyCode", priority = Priority.P1,
	description = "get format pattern by language and region")
	public void testLanguageRegion2(Object amount, String currencyCode,
			String language, String region, String expected, String desc) {
		String actual = numberFormatting.formatCurrency(amount, currencyCode, language, region);
		log.verifyEqual(String.format("%s, amount=%s, currencyCode=%s, language=%s, region=%s",
				desc, amount, currencyCode,language, region), actual, expected);
	}

	@DataProvider(name = "defaultCurrencyCode")
	public static Object[][] getDefaultCurrencyCodeTestData() {
		return new Object[][]{
			{20180903, new Locale("zh", "CN"), "US$20,180,903.00"},
			{20180903, new Locale("de"), "20.180.903,00 $"},
			{-20180903, new Locale("zh", "TW"), "-US$20,180,903.00"},
			{20180903, new Locale("en"), "$20,180,903.00"}
		};
	}

	@DataProvider(name = "customCurrencyCode")
	public static Object[][] getCustomCurrencyCode() {
		return new Object[][]{
			{20180903, "FRF", new Locale("fr"), "20 180 903,00 F"},
			{-20180903, "FRF", new Locale("fr"), "-20 180 903,00 F"}
		};
	}

	@DataProvider(name = "rouding")
	public static Object[][] getRoundingData() {
		return new Object[][]{
			{20180903.130, "CNY", new Locale("zh", "CN"), "￥20,180,903.13"},
			{-20180903.130, "CNY", new Locale("zh", "CN"), "-￥20,180,903.13"},
			{20180903.139, "CNY", new Locale("zh", "CN"), "￥20,180,903.14"},
			{-20180903.139, "CNY", new Locale("zh", "CN"), "-￥20,180,903.14"},
			{20180903.134, "CNY", new Locale("zh", "CN"), "￥20,180,903.13"},
			{-20180903.134, "CNY", new Locale("zh", "CN"), "-￥20,180,903.13"},
			{20180903.135, "CNY", new Locale("zh", "CN"), "￥20,180,903.14"},
			{-20180903.135, "CNY", new Locale("zh", "CN"), "-￥20,180,903.14"},
			{20180903.136, "CNY", new Locale("zh", "CN"), "￥20,180,903.14"},
			{-20180903.136, "CNY", new Locale("zh", "CN"), "-￥20,180,903.14"},
			{20180903.145, "CNY", new Locale("zh", "CN"), "￥20,180,903.14"},
			{-20180903.145, "CNY", new Locale("zh", "CN"), "-￥20,180,903.14"},
			{20180903.1345, "CNY", new Locale("zh", "CN"), "￥20,180,903.13"},
			{-20180903.1345, "CNY", new Locale("zh", "CN"), "-￥20,180,903.13"},
			{20180903.135, "JPY", new Locale("zh", "CN"), "JP¥20,180,903"},
			{-20180903.135, "JPY", new Locale("zh", "CN"), "-JP¥20,180,903"}
		};
	}

	@DataProvider(name = "supplementalOverride")
	public static Object[][] getSupplementalOverride() {
		return new Object[][]{
			{20180903, "JPY", new Locale("zh", "CN"), "JP¥20,180,903"},//"JPY":{"_rounding":"0","_digits":"0"}
			{20180903.001, "JPY", new Locale("zh", "CN"), "JP¥20,180,903"}
		};
	}

	@DataProvider(name = "amountType")
	public static Object[][] getAmountTypeData() {
		short numS = 12345;
		int numInt = 2018090309;
		long numL = 2018090300135135131L;
		float numF = 903.35f;
		double numD = 20180903.135;
		return new Object[][]{
			{numS, "int", "CNY", new Locale("zh", "CN"), "￥12,345.00"},
			{-numS, "int", "CNY", new Locale("zh", "CN"), "-￥12,345.00"},
			{numInt, "int", "CNY", new Locale("zh", "CN"), "￥2,018,090,309.00"},
			{-numInt, "int", "CNY", new Locale("zh", "CN"), "-￥2,018,090,309.00"},
			{numF, "float", "CNY", new Locale("zh", "CN"), "￥903.35"},
			{-numF, "float", "CNY", new Locale("zh", "CN"), "-￥903.35"},
			{numL, "long", "CNY", new Locale("zh", "CN"), "￥2,018,090,300,135,135,131.00"},
			{-numL, "long", "CNY", new Locale("zh", "CN"), "-￥2,018,090,300,135,135,131.00"},
			{numD, "double", "CNY", new Locale("zh", "CN"), "￥20,180,903.14"},
			{-numD, "double", "CNY", new Locale("zh", "CN"), "-￥20,180,903.14"}
		};
	}

	@DataProvider(name = "fallback")
	public static Object[][] getFallbackTestData() {
		return new Object[][]{
			{20180903, "JPY", new Locale("zh", "CN"), "¥20,180,903"},//JPY=JP¥ in zh_CN locale pattern
			{-20180903.135, "CNY", new Locale("de"), "-CN¥20,180,903.14"},
			{-903.135, "DEM", new Locale("de"), "-DEM903.14"},//de locale decimal is different with en
		};
	}

	@DataProvider(name = "LanguageRegion")
	public static Object[][] getLanguageRegion() {
		return new Object[][]{
			//amount, language, region, expected, description
			{1000.114, "zh-Hans", "CN", "US$1,000.11", "positive usage"},
			{1000.115, "zh-Hans", "fr", "1 000,12 $US", "Combined locale cannot be recognized by CLDR, "
					+ "use current region code to get the language tag with highest population rate"
					+ " in this region then generate a locale to get pattern data"},
			{1000.114, "null", "CN", "US$1,000.11", "invalide language, case result should be same with combined locale cannot be recognized by CLDR"}
		};
	}

	@DataProvider(name = "LanguageRegionWithCurrencyCode")
	public static Object[][] getLanguageRegionWithCurrencyCode() {
		String currencyCode = "JPY";
		return new Object[][]{
			//amount, language, region, expected, description
			{1000.114, currencyCode, "zh-Hans", "CN", "JP¥1,000", "positive usage"},
			{1001.5, currencyCode, "zh-Hans", "fr", "1 002 JPY", "Combined locale cannot be recognized by CLDR, "
					+ "use current region code to get the language tag with highest population rate"
					+ " in this region then generate a locale to get pattern data"},
			{1000.4, currencyCode, "null", "CN", "JP¥1,000", "invalide language, case result should be same with combined locale cannot be recognized by CLDR"}
		};
	}

	private void doSomethingInThread() {
		log.info("Do something here in thread");
		for (int i=0; i<getRandomIntInThread(50); i++) {
			int m = 1;
			int n = 2;
			int sum = m + n;
		}
	}

	private int getRandomIntInThread(int bound) {
		return ThreadLocalRandom.current().nextInt(bound);
	}

	public static Object[] getRandomTestData() {
		short numS = 12345;
		int numInt = 2018090309;
		long numL = 2018090300135135131L;
		float numF = 903.35f;
		double numD = 20180903.135;
		Object[][] data = new Object[][]{
			{numS, "int", "CNY", new Locale("zh", "CN"), "￥12,345.00"},
			{-numS, "int", "JPY", new Locale("zh", "CN"), "-JP¥12,345"},
			{numInt, "int", "CNY", new Locale("ja"), "元2,018,090,309.00"},
			{-numInt, "int", "JPY", new Locale("ja"), "-￥2,018,090,309"},
			{numF, "float", "KRO", new Locale("ko"), "KRO903.35"},
			{-numF, "float", "FRF", new Locale("fr"), "-903,35 F"},
			{numL, "long", "BAN", new Locale("es"), "2.018.090.300.135.135.131,00 BAN"},
			{-numL, "long", "THB", new Locale("de"), "-2.018.090.300.135.135.131,00 ฿"},
			{numD, "double", "ATS", new Locale("zh", "TW"), "ATS20,180,903.14"},
			{-numD, "double", "BOL", new Locale("en"), "-BOL20,180,903.14"}
		};
		int index = ThreadLocalRandom.current().nextInt(data.length-1);
		return data[index];
	}
}
