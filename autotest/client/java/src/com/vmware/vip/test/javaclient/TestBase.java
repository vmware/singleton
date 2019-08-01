package com.vmware.vip.test.javaclient;

//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
import java.util.ResourceBundle;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
//import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.vmware.g11n.log.GLogger;
import com.vmware.g11n.log.TestSetConfig;
//import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.test.common.Config;
//import com.vmware.vip.test.common.RequestType;
//import com.vmware.vip.test.javaclient.mock.MockAgent;
//import com.vmware.vipclient.i18n.base.PatternCacheManager;
//import com.vmware.vipclient.i18n.base.TranslationCacheManager;

public class TestBase {
	public final static ResourceBundle prop = ResourceBundle.getBundle("vipconfig");
	public final static String PRODUCT = prop.getString("productName");
	public final static String VERSION = prop.getString("version");
	public final static boolean IS_PSEUDO = new Boolean(prop.getString("pseudo"));
	public final static boolean IS_COLLECT_SOURCE = new Boolean(prop.getString("collectSource"));

	protected static GLogger log = GLogger.getInstance(TestBase.class.getName());
//	protected static MockAgent mockAgent = MockAgent.getInstance();
	protected static HashMap<String, String> defaultResponseHeaders = new HashMap<String, String>();
	private static Config cfg = Config.getInstance();

//	protected final String MOCK_COMPONENT = "mock_component";
//	protected final String MOCK_COMPONENT2 = "mock_component2";
//	protected final String MOCK_COMPONENT3 = "mock_component3";
//	protected final String MOCK_SOURCE = "mock_source";
//	protected final String MOCK_COMMENT = "mock_comment";
//	protected final Locale MOCK_LOCALE = new Locale("zh", "CN");
//	protected final Locale DEFAULT_LOCALE = new Locale("en", "US");

//	protected final String MOCK_KEY = "mock_key";
//	protected final String MOCK_TRANSLATION = "\u6d4b\u8bd5\u6d88\u606f";
//	protected final String DEFAULT_LOCALE_STRING = MOCK_SOURCE;
//	protected final String MOCK_PSEUDO = String.format("#@%s#@", MOCK_TRANSLATION);
	@BeforeSuite(alwaysRun=true)
	public void suiteSetUp() throws Exception {
//		setDefaultResponseHeaders();

		String bu = cfg.get(Constants.CONF_KEY_BU);
		String buildid = cfg.get(Constants.CONF_KEY_BUILD_ID);
		String product = cfg.get(Constants.CONF_KEY_PRODUCT);
		String branch = cfg.get(Constants.CONF_KEY_BRANCH);
		String buildType = cfg.get(Constants.CONF_KEY_BUILD_TYPE);
		String user = cfg.get(Constants.CONF_KEY_RACETRACK_USER);
		String customTestResultFolder = cfg.get(Constants.CONF_KEY_TEST_RESULT_FOLDER);
		String logOnRacetrack = cfg.get(Constants.CONF_KEY_RACETRACK_ENABLE);
		log.setConfig(bu, product, branch, buildid, buildType,
				branch, "Regression", "en_US", "win2k8r2", "x64", "none", "none",
				"none", "none", user, logOnRacetrack, null, customTestResultFolder);
	}

	@BeforeTest(alwaysRun=true)
	public void testSetup() throws Exception {
		TestSetConfig testSetConfig = new TestSetConfig("test for vip java client");
		log.testSetBegin(testSetConfig);
	}

//	@BeforeMethod
//	public void setBasicResponse() {
////		setComponentListResponse();
////		setLocaleListResponse();
//	}

	@AfterMethod(alwaysRun=true)
	public void cleanCache() {
//		log.debug("release cache after method invocation");
//		TranslationCacheManager.createTranslationCacheManager().releaseCache();
//		PatternCacheManager.getInstance().releaseCache();
	}

//	@AfterMethod(alwaysRun=true)
//	public void clearMockExpectation() {
////		if (mockAgent.isMockServerRuning()) {
////			log.debug("reset mock");
////			mockAgent.resetMock();
////		}
//	}

	@AfterSuite(alwaysRun=true)
	public void suiteCleanUp() throws Exception {
		log.testSetEnd();
	}

//	private void setDefaultResponseHeaders() {
//		//TODO:
////		defaultResponseHeaders.put("date", "Sat, 02 Jun 2018 14:13:13 GMT");
////		defaultResponseHeaders.put("cache-control", "private");
////		defaultResponseHeaders.put("expires", "Wed, 31 Dec 1969 16:00:00 PST");
////		defaultResponseHeaders.put("etag", "\"0dd394d9fc93b6bf755b5941ac0b7a7ab\"");
////		defaultResponseHeaders.put("content-length", "1429");
////		defaultResponseHeaders.put("content-type", "application/json;charset=UTF-8");
//	}
//
//	private void setComponentListResponse() {
//		String request = URLGetter.productComponentList(PRODUCT, VERSION);
//		List<String> componentList = new ArrayList<String>();
//		componentList.add(MOCK_COMPONENT);
//		componentList.add(MOCK_COMPONENT2);
//		componentList.add(MOCK_COMPONENT3);
//		String response = ResponseGetter.componentList(APIResponseStatus.OK.getCode(),
//				APIResponseStatus.OK.getMessage(), "", PRODUCT, VERSION, componentList );
//		mockAgent.addExpectation(RequestType.GET, request, null, null, APIResponseStatus.OK.getCode(),
//				defaultResponseHeaders, response);
//	}
//
//	private void setLocaleListResponse() {
//		String request = URLGetter.productLocaleList(PRODUCT, VERSION);
//		List<String> localList = new ArrayList<String>();
//		localList.add(MOCK_LOCALE.getDisplayName());
//		localList.add(DEFAULT_LOCALE.getDisplayName());
//		String response = ResponseGetter.productLocales(APIResponseStatus.OK.getCode(),
//				APIResponseStatus.OK.getMessage(), "", PRODUCT, VERSION, localList);
//		mockAgent.addExpectation(RequestType.GET, request, null, null, APIResponseStatus.OK.getCode(),
//				defaultResponseHeaders, response);
//	}
}
