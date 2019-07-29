package com.vmware.vip.test.javaclient.level2;

import java.net.MalformedURLException;
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
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;

public class PluralTest extends TestBase {
	public TranslationMessage translation = null;
	public Cache translationCache = null;
	@BeforeClass
	public void preparing() throws MalformedURLException {
		initVIPServer();
	}

	public void initVIPServer() throws MalformedURLException {
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		translationCache = vipCfg.createTranslationCache(MessageCache.class);
		translation = (TranslationMessage)I18nFactory.getInstance(vipCfg).getMessageInstance(TranslationMessage.class);
	}

	@Test(enabled=true, priority=0)
	@TestCase(id = "001", name = "PluralTest", description = "test desc")
	public void pluralTest() {
		Locale myLocale = new Locale("zh", "CN");
		String component = "demo", bundle = "messages";
		String pluralKey = "plural.files";
		Object[] testArg1 = {1, "MyDisk"};
		String pluralMessage1 = translation.getString2(component, bundle, myLocale, pluralKey, testArg1);
		log.verifyEqual("", pluralMessage1, "\"MyDisk\"上有 1 个文件。");

		Object[] testArg0 = {0, "MyDisk"};
		String pluralMessage0 = translation.getString2(component, bundle, myLocale, pluralKey, testArg0);
		log.verifyEqual("", pluralMessage0, "\"MyDisk\"上有 0 个文件。");
	}

	@Test(enabled=true, priority=0)
	@TestCase(id = "002", name = "PluralTest_Fallback", description = "test desc")
	public void fallbackDiffSource() {
		Locale myLocale = new Locale("zh", "CN");
		String component = "demo", bundle = "diff_source";
		String pluralKey = "plural.files";

		Object[] testArg0 = {0, "MyDisk"};
		String pluralMessage0 = translation.getString2(component, bundle, myLocale, pluralKey, testArg0);
		log.verifyEqual("", pluralMessage0, "0 files on \"MyDisk\".");

		Object[] testArg1 = {1, "MyDisk"};
		String pluralMessage1 = translation.getString2(component, bundle, myLocale, pluralKey, testArg1);
		log.verifyEqual("", pluralMessage1, "A file on \"MyDisk\".");
	}

	@Test(enabled=true, priority=0)
	@TestCase(id = "003", name = "PluralTest_Fallback_Disconnect",
	description = "Plural rule should fallback to en when VIP server disconnected.")
	public void fallbackDisconnect() throws Exception {
		try {
			VIPCfg vipCfg = VIPCfg.getInstance();
			vipCfg.getVipService().getHttpRequester().setBaseURL("https://unreachable.com:8090");
			vipCfg.initializeVIPService();
			translationCache.clear();
			Locale myLocale = new Locale("zh", "CN");
			String component = "demo", bundle = "messages";
			String pluralKey = "plural.files";

			Object[] testArg0 = {0, "MyDisk"};
			String pluralMessage0 = translation.getString2(component, bundle, myLocale, pluralKey, testArg0);
			log.verifyEqual("", pluralMessage0, "There are 0 files on \"MyDisk\".");

			Object[] testArg1 = {1, "MyDisk"};
			String pluralMessage1 = translation.getString2(component, bundle, myLocale, pluralKey, testArg1);
			log.verifyEqual("", pluralMessage1, "There is a file on \"MyDisk\".");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			translationCache.clear();
			VIPCfg vipCfg = VIPCfg.getInstance();
			vipCfg.getVipService().getHttpRequester().setBaseURL("https://"+vipCfg.getVipServer());
		}
	}

	//	@Test(enabled=true, priority=0)
	//	@TestCase(id = "004", name = "PluralTest_Fallback_Disconnect_Init_Params",
	//	description = "bug 2240802, initialize product")
	//	public void fallbackDisconnect3() throws Exception {
	//		VIPCfg vipCfg = VIPCfg.getInstance();
	//		vipCfg.initialize("noserver", "noproduct", "noversion");
	//		vipCfg.initializeVIPService();
	//		vipCfg.setI18nScope("numbers,dates,currencies,plurals,measurements");
	//		vipCfg.createTranslationCache(MessageCache.class);
	//		vipCfg.createFormattingCache(FormattingCache.class);
	//		I18nFactory i18n = I18nFactory.getInstance(vipCfg);
	//		TranslationMessage translation = (TranslationMessage)i18n.getMessageInstance(TranslationMessage.class);
	//
	//		Locale myLocale = new Locale("zh", "CN");
	//		String component = "demo", bundle = "messages";
	//		String pluralKey = "plural.files";
	//
	//		Object[] testArg0 = {0, "MyDisk"};
	//		String pluralMessage0 = translation.getString2(component, bundle, myLocale, pluralKey, testArg0);
	//		log.verifyEqual("", pluralMessage0, "There are 0 files on \"MyDisk\".");
	//
	//		Object[] testArg1 = {1, "MyDisk"};
	//		String pluralMessage1 = translation.getString2(component, bundle, myLocale, pluralKey, testArg1);
	//		log.verifyEqual("", pluralMessage1, "There is a file on \"MyDisk\".");
	//	}

	@Test(enabled=true, priority=1)
	@TestCase(id = "004", name = "PluralTest_ReservedCharacter", description = "bug 2130798")
	public void reservedCharacter() {
		Locale myLocale = new Locale("zh", "CN");
		String component = "demo", bundle = "messages";
		String pluralKey = "plural.reserved.character";
		Object[] testArg1 = {1};
		String pluralMessage1 = translation.getString2(component, bundle, myLocale, pluralKey, testArg1);
		log.verifyEqual("", pluralMessage1, "有 1 个井号#");

		Object[] testArg2 = {2};
		String pluralMessage2 = translation.getString2(component, bundle, myLocale, pluralKey, testArg2);
		log.verifyEqual("", pluralMessage2, "有 2 个井号#");
	}
}
