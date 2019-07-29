package com.vmware.vip.test.javaclient.level2;

import java.net.MalformedURLException;
import java.util.Locale;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;

public class NumberFormatTest extends TestBase {
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

	@Test(enabled=true, dataProvider="NumberFormat")
	@TestCase(id = "001", name = "NumberFormatTest", description = "test desc")
	public void testNumberFormat(Object amount, int fractionSize,
			Locale locale, String expected) {
		String actual = numberFormatting.formatNumber(amount, fractionSize, locale);
		log.verifyEqual(String.format("amount=%s, fraction=%s, locale=%s",
				amount, fractionSize, locale), actual, expected);
	}

	@Test(enabled=true, dataProvider="NumberFormatWithDefualtFractionSize")
	@TestCase(id = "002", name = "NumberFormatTest", description = "test desc")
	public void testNumberFormatWithDefaultFractionSize(Object amount,
			Locale locale, String expected) {
		String actual = numberFormatting.formatNumber(amount, locale);
		log.verifyEqual(String.format("amount=%s, fraction=default, locale=%s",
				amount, locale), actual, expected);
	}

	@Test(enabled=true, dataProvider="NumberFormatPercentWithDefaultFractionSize")
	@TestCase(id = "003", name = "NumberFormatTest_Percent_WithDefaultFractionSize", description = "test desc")
	public void testPercentWithDefaultFractionSize(Object amount, Locale locale, String expected) {
		String actual = numberFormatting.formatPercent(amount, locale);
		log.verifyEqual(String.format("amount=%s, fraction=default, locale=%s",
				amount, locale), actual, expected);
	}

	@Test(enabled=true, dataProvider="NumberFormatPercent")
	@TestCase(id = "004", name = "NumberFormatTest_Percent", description = "test desc")
	public void testPercent(Object amount, int fractionSize,
			Locale locale, String expected) {
		String actual = numberFormatting.formatPercent(amount, fractionSize, locale);
		log.verifyEqual(String.format("amount=%s, fraction=%s, locale=%s",
				amount, fractionSize, locale), actual, expected);
	}

	@Test(enabled=true, dataProvider="LanguageRegion")
	@TestCase(id = "005", name = "Number_GetFormatPatternByLanguageAndRegion",
	description = "get formatting patterns by language and region")
	public void testLanguageRegion(Object amount, String language, String region, String expected, String desc) {
		try {
			String actual = numberFormatting.formatNumber(amount, language, region);
			log.verifyEqual(String.format("%s, amount=%s, language=%s, region=%s",
					desc, amount, language, region), actual, expected);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(enabled=true, dataProvider="LanguageRegionPercent")
	@TestCase(id = "006", name = "Percent_GetFormatPatternByLanguageAndRegion",
	description = "get formatting patterns by language and region")
	public void testLanguageRegionPercentDefaultFractionSize(Object amount, String language, String region, String expected, String desc) {
		String actual = numberFormatting.formatPercent(amount, language, region);
		log.verifyEqual(String.format("%s, amount=%s, language=%s, region=%s",
				desc, amount, language, region), actual, expected);
	}

	@Test(enabled=true, dataProvider="LanguageRegionPercentWithFraction")
	@TestCase(id = "007", name = "Percent_GetFormatPatternByLanguageAndRegion",
	description = "get formatting patterns by language and region")
	public void testLanguageRegionPercent(Object amount, int fraction, String language, String region, String expected, String desc) {
		String actual = numberFormatting.formatPercent(amount, fraction, language, region);
		log.verifyEqual(String.format("%s, amount=%s, fraction=%s, language=%s, region=%s",
				desc, amount, fraction, language, region), actual, expected);
	}

	@DataProvider(name = "NumberFormatPercent")
	public static Object[][] getNumberFormatPercent() {
		return new Object[][]{
			{10.114, 1, new Locale("ko"), "1,011.4%"},
			{10.115, 4, new Locale("ko"), "1,011.5000%"}
		};
	}

	@DataProvider(name = "NumberFormatPercentWithDefaultFractionSize")
	public static Object[][] getNumberFormatPercentWithDefaultFractionSize() {
		return new Object[][]{
			{10.114, new Locale("ja"), "1,011%"},
			{10.115, new Locale("ja"), "1,012%"},
			{10.1246, new Locale("ja"), "1,012%"}
		};
	}

	@DataProvider(name = "NumberFormat")
	public static Object[][] getNumberFormaTestData() {
		return new Object[][]{
			{1.01f, -1, new Locale("fr"), "1"},
			{1.01f, 0, new Locale("fr"), "1"},
			{1.01f, 1, new Locale("fr"), "1,0"},
			{1.01f, 4, new Locale("fr"), "1,0100"},
			{1.015f, 2, new Locale("fr"), "1,02"},
			{1.015, 2, new Locale("fr"), "1,02"}
		};
	}

	@DataProvider(name = "NumberFormatWithDefualtFractionSize")
	public static Object[][] getNumberFormaTestDataWithDefaultFractionSize() {
		return new Object[][]{
			{1, new Locale("fr"), "1"},
			{1.06, new Locale("ja"), "1.06"},
			{1.0156f, new Locale("fr"), "1,016"}
		};
	}

	@DataProvider(name = "LanguageRegion")
	public static Object[][] getLanguageRegion() {
		return new Object[][]{
			//amount, language, region, expected, description
			{1000.114, "zh-Hans", "CN", "1,000.114", "positive usage"},
			{1000.114, "zh-Hans", "fr", "1 000,114", "Combined locale cannot be recognized by CLDR, "
					+ "use current region code to get the language tag with highest population rate"
					+ " in this region then generate a locale to get pattern data"},
//			{1000.114, "zh-Hans", "null", "1,000.114", "invalide region, bug 2331742"},
			{1000.114, "null", "CN", "1,000.114", "invalide language, case result should be same with combined locale cannot be recognized by CLDR"}
		};
	}

	@DataProvider(name = "LanguageRegionPercent")
	public static Object[][] getLanguageRegionPercent() {
		return new Object[][]{
			//amount, language, region, expected, description
			{0.666, "zh-Hans", "CN", "67%", "positive usage"},
			{1.666, "zh-Hans", "CN", "167%", "number bigger than 1"},
			{0.666, "zh-Hans", "fr", "67 %", "Combined locale cannot be recognized by CLDR, "
					+ "use current region code to get the language tag with highest population rate"
					+ " in this region then generate a locale to get pattern data"},
			{0.666, "null", "CN", "67%", "invalide language, case result should be same with combined locale cannot be recognized by CLDR"}
		};
	}

	@DataProvider(name = "LanguageRegionPercentWithFraction")
	public static Object[][] getLanguageRegionPercentWithFraction() {
		return new Object[][]{
			//amount, language, region, expected, description
			{0.6665, 1, "zh-Hans", "CN", "66.7%", "positive usage"},
			{0.6665, 3, "zh-Hans", "fr", "66,650 %", "Combined locale cannot be recognized by CLDR, "
					+ "use current region code to get the language tag with highest population rate"
					+ " in this region then generate a locale to get pattern data"},
			{0.6665, 0, "null", "CN", "67%", "invalide language, case result should be same with combined locale cannot be recognized by CLDR"}
		};
	}
}
