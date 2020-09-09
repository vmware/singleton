package com.vmware.vip.test.javaclient;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;

import com.vmware.vip.test.common.Utils;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class Bundle {
	private JSONObject productObj;
	protected static final String BUNDLE_PREFIX = "messages_";
	protected static final String BUNDLE_SUFFIX = ".json";

	protected Bundle() {
	}
	protected static boolean isBundleFile(String fileName) {
		return fileName.startsWith(BUNDLE_PREFIX) && fileName.endsWith(BUNDLE_SUFFIX);
	}
	/*
	 * store all messages to json object
	 */
	public void initialize(String rootPath, String cfgFile) throws VIPClientInitException {
		VIPCfg cfg = VIPCfg.getInstance();
		cfg.initialize(Utils.removeFileExtension(cfgFile));
		this.productObj = new JSONObject();
		String[] versions = new File(rootPath).list();
		for (String version : versions) {
			String versionPath = Paths.get(rootPath, version).toString();
			String[] components = new File(versionPath).list();
			JSONObject versionObj = new JSONObject();
			for (String component : components) {
				String componentPath = Paths.get(versionPath, component).toString();
				String[] files = new File(componentPath).list();
				JSONObject componentObj = new JSONObject();
				for (String file : files) {
					if (isBundleFile(file)) {
						JSONObject jsonObj = Utils.readJSONObjFromFile(Paths.get(componentPath, file).toString());
						//source and latest will be saved as locale for messages_source.json and messages_latest.json
						String locale = file.replace(BUNDLE_PREFIX, "").replace(BUNDLE_SUFFIX, "");
						componentObj.put(locale, jsonObj.getJSONObject(Constants.RESOURCE_KEY_MSG));
					}
				}
				versionObj.put(component, componentObj);
			}
			this.productObj.put(version, versionObj);
		}
	}
	public List<String> getVersions() {
		return new ArrayList<String>(this.productObj.keySet());
	}
	public List<String> getTranslationReadyComponents() {
		List<String> components = getAllComponents();
		ArrayList<String> candidates = new ArrayList<String>();
		for (String component : components) {
			if (isTranslationReady(component)) {
				candidates.add(component);
			}
		}
		return candidates;
	}
	public List<String> getTranslationNotReadyComponents() {
		List<String> components = getAllComponents();
		ArrayList<String> candidates = new ArrayList<String>();
		for (String component : components) {
			if (!isTranslationReady(component)) {
				candidates.add(component);
			}
		}
		return candidates;
	}
	public List<String> getAllComponents() {
		VIPCfg cfg = VIPCfg.getInstance();
		JSONObject versionObj = this.productObj.getJSONObject(cfg.getVersion());
		return new ArrayList<String>(versionObj.keySet());
	}
	public List<Locale> getLocalesByComponent(String component) {
		VIPCfg cfg = VIPCfg.getInstance();
		JSONObject versionObj = this.productObj.getJSONObject(cfg.getVersion());
		JSONObject componentObj = versionObj.getJSONObject(component);
		ArrayList<Locale> locales = new ArrayList<Locale>();
		for (String locale : componentObj.keySet()) {
			//skip latest and source for locale collection
			if (locale.equals("latest") || locale.equals("source")) {
				continue;
			}
			locales.add(Locale.forLanguageTag(locale));
		}
		return locales;
	}
	public List<Locale> getLocalizedLocalesByComponent(String component) {
		List<Locale> locales = getLocalesByComponent(component);
		List<Locale> localizedLocales = new ArrayList<Locale>();
		Locale defaultLocale = LocaleUtility.getDefaultLocale();
		for (Locale locale : locales) {
			if (defaultLocale.equals(locale)) {
				continue;
			}
			localizedLocales.add(locale);
		}
		return localizedLocales;
	}
	public Map<String, Object> getMessages(String component, Locale locale) {
		VIPCfg cfg = VIPCfg.getInstance();
		String queryStr = String.format("/%s/%s/%s", cfg.getVersion(), component, locale);
		JSONObject messagesObj = (JSONObject)this.productObj.query(queryStr);
		return messagesObj.toMap();
	}
	private boolean isTranslationReady(String component) {
		return !component.equals(TestConstants.BUNDLE_COMPONENT_TRANSLATION_NOT_READY);
	}
}
