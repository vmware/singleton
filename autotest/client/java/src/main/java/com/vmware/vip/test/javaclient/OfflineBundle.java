package com.vmware.vip.test.javaclient;

//import java.io.File;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import org.json.JSONObject;
//
//import com.vmware.g11n.log.GLogger;
//import com.vmware.vip.test.common.Utils;
//import com.vmware.vipclient.i18n.util.LocaleUtility;

public class OfflineBundle extends Bundle{
//	private static GLogger log = GLogger.getInstance(OfflineBundle.class.getName()); 
	private static OfflineBundle bundle;
//	private List<String> components;
//	private Map<String, Map<Locale, JSONObject>> messagesMap;
//	protected static final String BUNDLE_PREFIX = "messages_";
//	protected static final String BUNDLE_SUFFIX = ".json";

	private OfflineBundle() {
//		messagesMap = new HashMap<String, Map<Locale, JSONObject>>();
//		components = new ArrayList<String>();
	}
//	private static boolean isBundleFile(String fileName) {
//		return fileName.startsWith(BUNDLE_PREFIX) && fileName.endsWith(BUNDLE_SUFFIX);
//	}
//	private Map<String, Map<Locale, JSONObject>> getMessagesMap() {
//		return this.messagesMap;
//	}
	public static synchronized OfflineBundle getInstance() {
        if (bundle == null) {
        	bundle = new OfflineBundle();
        }
        return bundle;
    }
//	public void initialize(String rootPath) {
//		components = Arrays.asList(new File(rootPath).list());
//		for (String component : components) {
//			Map<Locale, JSONObject> localeMsgsMap = new HashMap<Locale, JSONObject>();
//			for (String file : new File(Paths.get(rootPath, component).toString()).list()) {
//				if (isBundleFile(file)) {
//					JSONObject jsonObj = Utils.readJSONObjFromFile(file);
//					Locale locale = Locale.forLanguageTag((String)jsonObj.get(Constants.RESOURCE_KEY_LOCALE));
//					localeMsgsMap.put(locale, jsonObj);
//				}
//			}
//			messagesMap.put(component, localeMsgsMap);
//		}
//	}
//	public List<String> getComponents() {
//		return components;
//	}
//	public List<Locale> getLocalesByComponent(String component) {
//		Map<String, Map<Locale, JSONObject>> map = this.getMessagesMap();
//		List<Locale> locales = new ArrayList<Locale>();
//		if (map.isEmpty()) {
//			log.error("Messages is empty, please check bundle path and if OfflineBundle initialzed");
//		} else {
//			locales.addAll(map.get(component).keySet());
//		}
//		return locales;
//	}
//	public List<Locale> getLocalizedLocalesByComponent(String component) {
//		List<Locale> locales = getLocalesByComponent(component);
//		List<Locale> localizedLocales = new ArrayList<Locale>();
//		Locale defaultLocale = LocaleUtility.getDefaultLocale();
//		for (Locale locale : locales) {
//			if (defaultLocale.equals(locale)) {
//				continue;
//			}
//			localizedLocales.add(locale);
//		}
//		return localizedLocales;
//	}
//	public Map<String, Object> getMessages(String component, Locale locale) {
//		JSONObject rootObj = this.messagesMap.get(component).get(locale);
//		JSONObject msgObj = rootObj.getJSONObject(Constants.RESOURCE_KEY_MSG);
//		return msgObj.toMap();
//	}
}
