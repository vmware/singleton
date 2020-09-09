package com.vmware.vip.test.javaclient;

public class OnlineBundle extends Bundle{
	private static OnlineBundle bundle;
//	private JSONObject productObj;
//	protected static final String BUNDLE_PREFIX = "messages_";
//	protected static final String BUNDLE_SUFFIX = ".json";

	private OnlineBundle() {
//		productObj = new JSONObject();
	}
//	private static boolean isBundleFile(String fileName) {
//		return fileName.startsWith(BUNDLE_PREFIX) && fileName.endsWith(BUNDLE_SUFFIX);
//	}
	public static synchronized OnlineBundle getInstance() {
        if (bundle == null) {
        	bundle = new OnlineBundle();
        }
        return bundle;
    }
//	public void initialize(String rootPath) {
//		String[] versions = new File(rootPath).list();
//		for (String version : versions) {
//			String versionPath = Paths.get(rootPath, version).toString();
//			String[] components = new File(versionPath).list();
//			JSONObject versionObj = new JSONObject(version);
//			for (String component : components) {
//				String componentPath = Paths.get(versionPath, component).toString();
//				String[] files = new File(componentPath).list();
//				JSONObject componentObj = new JSONObject(component);
//				for (String file : files) {
//					if (isBundleFile(file)) {
//						JSONObject jsonObj = Utils.readJSONObjFromFile(file);
//						String locale = (String)jsonObj.get(Constants.RESOURCE_KEY_LOCALE);
//						componentObj.put(locale, jsonObj.getJSONObject(Constants.RESOURCE_KEY_MSG));
//					}
//				}
//				versionObj.put(component, componentObj);
//			}
//			this.productObj.put(version, versionObj);
//		}
//	}
//	public List<String> getVersions() {
//		return new ArrayList<String>(this.productObj.keySet());
//	}
//	public List<String> getComponents() {
//		VIPCfg cfg = VIPCfg.getInstance();
//		JSONObject versionObj = this.productObj.getJSONObject(cfg.getVersion());
//		return new ArrayList<String>(versionObj.keySet());
//	}
//	public List<Locale> getLocalesByComponent(String component) {
//		VIPCfg cfg = VIPCfg.getInstance();
//		JSONObject versionObj = this.productObj.getJSONObject(cfg.getVersion());
//		JSONObject componentObj = versionObj.getJSONObject(component);
//		ArrayList<Locale> locales = new ArrayList<Locale>();
//		for (String locale : componentObj.keySet()) {
//			locales.add(Locale.forLanguageTag(locale));
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
//		VIPCfg cfg = VIPCfg.getInstance();
//		String queryStr = String.format("/%s/%s/%s", cfg.getVersion(), component, locale);
//		JSONObject messagesObj = (JSONObject)this.productObj.query(queryStr);
//		return messagesObj.toMap();
//	}
}
