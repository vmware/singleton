package com.vmware.vip.test.javaclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.vmware.vip.test.common.Utils;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class BundleDataProvider {
	@DataProvider(name = "getACommonOfflineMessage")
	public static Object[][] getACommonOfflineMessage() {
		return getACommonMessage(OfflineBundle.getInstance());
	}

	@DataProvider(name = "getACommonOnlineMessage")
	public static Object[][] getACommonOnlineMessage() {
		return getACommonMessage(OnlineBundle.getInstance());
	}

	private static Object[][] getACommonMessage(Bundle bundle) {
		List<String> components = bundle.getTranslationReadyComponents();
		String component = Utils.getRandomItem(components);
		List<Locale> locales = bundle.getLocalizedLocalesByComponent(component);
		Locale locale = Utils.getRandomItem(locales);
		Map<String, Object> messages = bundle.getMessages(component, locale);
		String key = Utils.getRandomItem(new ArrayList<String>(messages.keySet()));
		String translation = messages.get(key).toString();
		Locale defaultLocale = LocaleUtility.getDefaultLocale();
		Map<String, Object> messagesInDefaultLocale = bundle.getMessages(component, defaultLocale);
		String messageInDefaultLocale = messagesInDefaultLocale.get(key).toString();
		return new Object[][]{
			{component, locale, key, translation, messageInDefaultLocale}
		};
	}

	@DataProvider(name = "getOnlineLocalizedMsgDiffWithOffline")
	public static Object[][] getOnlineLocalizedMsgDiffWithOffline() {
		String key = "online.localized.msg.different.with.offline";
		String component = "JAVA";
		Locale locale = new Locale("fr");
		String onlineMsg = "En ligne";
		String offlineMsg = "Hors ligne";
		return new Object[][]{
			{component, locale, key, onlineMsg, offlineMsg}
		};
	}
}
