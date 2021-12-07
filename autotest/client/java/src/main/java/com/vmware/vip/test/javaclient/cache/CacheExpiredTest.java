package com.vmware.vip.test.javaclient.cache;

import java.util.Locale;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.TestGroups;
import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.BundleDataProvider;
import com.vmware.vip.test.javaclient.ClientConfigHelper;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;

public class CacheExpiredTest extends TestBase {
	@BeforeClass
	public void preparing() throws Exception {
		initVIPServer();
	}

	public void initVIPServer() throws Exception {
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		vipCfg.createTranslationCache(MessageCache.class);
	}
	@Test(enabled=false, priority=0, groups=TestGroups.BUG,
			dataProvider="getACommonOnlineMessage", dataProviderClass=BundleDataProvider.class)
	@TestCase(id = "001", name = "GetUpdatedTranslationByKeyTest", priority=Priority.P0,
	description = "When getMessage() returns to default locale's translation, "
			+ "cache can't get updated translation(default locale) after it is expired, "
			+ "bug from https://github.com/vmware/singleton/issues/662")
	public void getUpdatedTranslationByKeyTest(String component, Locale locale, String key, String translation) {
		TranslationMessage tm = null;
		try {
			tm = this.getTranslationMessage(vipCfg);

			Locale localeWithoutTranslation = new Locale("mn");
			Cache cache = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
			cache.clear();
			log.info("Request translation first time.");
			String originMsg = tm.getMessage(localeWithoutTranslation, component, key);

			log.info("Set cache expired.");
			Map<String, MessageCacheItem> cachedTranslationMap = ((MessageCache) cache).getCachedTranslationMap();
			String cacheKey =  String.format("%s_%s_%s_%s_#%s", vipCfg.getProductName(), vipCfg.getVersion(), component,
					vipCfg.isPseudo(), localeWithoutTranslation.toLanguageTag());
			MessageCacheItem cacheItem = cachedTranslationMap.get(cacheKey);
//			cacheItem.setMaxAgeMillis(0l);
			cacheItem.setCacheItem(cacheItem.getCachedData(), cacheItem.getEtag(), cacheItem.getTimestamp(), 0l);

			log.info("Update English string");
			//TODO
			MessageCacheItem map = (MessageCacheItem) TranslationCacheManager.getCache(VIPCfg.CACHE_L3).get(cacheKey);
			log.verifyTrue("check cache status, should be true:", map.isExpired());
			String updatedMsg = tm.getMessage(localeWithoutTranslation, component, key);
			//TODO log.verifyEqual("Get updated string.", updatedMsg, expected);
			log.verifyFalse("Verify new string is not same with original one.", originMsg.equals(updatedMsg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test(enabled=false, priority=0, groups=TestGroups.BUG,
			dataProvider="getACommonOnlineMessage", dataProviderClass=BundleDataProvider.class)
	@TestCase(id = "002", name = "GetUpdatedTranslationByComponentTest", priority=Priority.P0,
	description = "When getMessages() returns to default locale's translation, "
			+ "cache can't get updated translation(default locale) after it is expired, "
			+ "bug from https://github.com/vmware/singleton/issues/664")
	public void getUpdatedTranslationByComponentTest(String component, Locale locale, String key, String translation) {
		TranslationMessage tm = null;
		try {
			tm = this.getTranslationMessage(vipCfg);

			Locale localeWithoutTranslation = new Locale("mn");
			Cache cache = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
			cache.clear();
			log.info("Request translation first time.");
			Map<String, String> originMsgMap = tm.getMessages(localeWithoutTranslation, component);
			String originMsg = originMsgMap.get(key);

			log.info("Set cache expired.");
			Map<String, MessageCacheItem> cachedTranslationMap = ((MessageCache) cache).getCachedTranslationMap();
			String cacheKey =  String.format("%s_%s_%s_%s_#%s", vipCfg.getProductName(), vipCfg.getVersion(), component,
					vipCfg.isPseudo(), localeWithoutTranslation.toLanguageTag());
			MessageCacheItem cacheItem = cachedTranslationMap.get(cacheKey);
//			cacheItem.setMaxAgeMillis(0l);
			cacheItem.setCacheItem(cacheItem.getCachedData(), cacheItem.getEtag(), cacheItem.getTimestamp(), 0l);

			log.info("Update English string");
			//TODO
			MessageCacheItem map = (MessageCacheItem) TranslationCacheManager.getCache(VIPCfg.CACHE_L3).get(cacheKey);
			log.verifyTrue("check cache status, should be true:", map.isExpired());
			Map<String, String> updatedMsgMap = tm.getMessages(localeWithoutTranslation, component);
			String updatedMsg = updatedMsgMap.get(key);
			//TODO log.verifyEqual("Get updated string.", updatedMsg, expected);
			log.verifyFalse("Verify new string is not same with original one.", originMsg.equals(updatedMsg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test(enabled=true, priority=0, groups=TestGroups.BUG,
			dataProvider="getOnlineLocalizedMsgDiffWithOffline", dataProviderClass=BundleDataProvider.class)
	@TestCase(id = "003", name = "BundleFallbackTest", priority=Priority.P0,
	description = "Cache expired, fetch online failed, will get translation from expired cache not offline bundle."
			+ "bug from https://github.com/vmware/singleton/issues/686")
	public void bundleFallbackTest(String component, Locale locale, String key, String onlineMsg, String offlineMsg) {
		try {
			vipCfg.initialize(Utils.removeFileExtension(ClientConfigHelper.CONFIG_TEMPLATE_MIX));
			vipCfg.setInitializeCache(true);
			vipCfg.initializeVIPService();
		} catch (VIPClientInitException e) {
			log.error("Catche exception when initializing vip client\n"+e.toString());
		}
		Cache cache = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
		cache.clear();
		TranslationMessage tm = this.getTranslationMessage(vipCfg);
		String msgFromOnline = tm.getMessage(locale, component, key);
		log.verifyEqual("Get translation from VIP service successfully.", msgFromOnline, onlineMsg);
		log.info("Set cache expired.");
		Map<String, MessageCacheItem> cachedTranslationMap = ((MessageCache) cache).getCachedTranslationMap();
		String cacheKey =  String.format("%s_%s_%s_%s_#%s", vipCfg.getProductName(), vipCfg.getVersion(), component,
				vipCfg.isPseudo(), locale.toLanguageTag());
		MessageCacheItem cacheItem = cachedTranslationMap.get(cacheKey);
//		cacheItem.setMaxAgeMillis(0l);
		cacheItem.setCacheItem(cacheItem.getCachedData(), cacheItem.getEtag(), cacheItem.getTimestamp(), 0l);
		try {
			Thread.sleep(1000*5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String originURL = this.getVIPServiceURL();
		try {
			this.setVIPServiceURL("https://unreachable.com:8090");
			String msgAfterDisconnectVIP = tm.getMessage(locale, component, key);
			log.verifyEqual("Get translation from cache when VIP service disconnected.", msgAfterDisconnectVIP, onlineMsg);
		} catch (Exception e) {
			log.error(e.toString());
			log.verifyNull("No exception with unreachable service", e);
		} finally {
			this.setVIPServiceURL(originURL);
		}
	}
}
