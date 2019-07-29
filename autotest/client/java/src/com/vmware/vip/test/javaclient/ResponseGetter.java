package com.vmware.vip.test.javaclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResponseGetter {
	public static String componentTranslationGet(int exitCode, String message, String signature,
			String product, String version, String pseudo, String component,
			HashMap<String, String> messages, String locale,String status, int id) {
		if (new Boolean(pseudo)) {
			locale = "latest";
		}
		List<String> keyValue = new ArrayList<String>();
		for (String key : messages.keySet()) {
			keyValue.add(String.format("\"%s\": \"%s\"", key, messages.get(key)));
		}
		String messagesStr = String.join(", ", keyValue);

		return String.format("{\"response\":{\"code\":%s,\"message\":\"%s\"},\"signature\":\"%s\","
				+ "\"data\":{\"productName\":\"%s\",\"version\":\"%s\",\"pseudo\":%s,\"component\":\"%s\","
				+ "\"messages\":{%s},\"locale\":\"%s\",\"status\":\"%s\",\"id\":%s}}",
				exitCode, message, signature, product, version, pseudo, component, messagesStr, locale, status, id);
	}

	public static String keyTranslationPost(int exitCode, String message, String signature,
			String product, String version, String pseudo, String source, String translation, String locale,
			String key, String component, String status) {
		return String.format("{\"response\":{\"code\":%s,\"message\":\"%s\"},\"signature\":\"%s\","
				+ "\"data\":{\"productName\":\"%s\",\"version\":\"%s\",\"pseudo\":%s,\"source\":\"%s\","
				+ "\"translation\":\"%s\",\"locale\":\"%s\",\"key\":\"%s\",\"component\":\"%s\",\"status\":\"%s\"}}",
				exitCode, message, signature, product, version, pseudo,
				source, translation, locale, key, component, status);
	}

	public static String productLocales(int exitCode, String message, String signature,
			String product, String version, List<String> localeList) {
		String locales = "\""+String.join("\",\"", localeList)+"\"";
		return String.format("{\"response\":{\"code\":%s,\"message\":\"%s\"},\"signature\":\"%s\","
				+ "\"data\":{\"locales\":[%s],\"version\":\"%s\",\"productName\":\"%s\"}}",
				exitCode, message, signature, locales, version, product);
	}

	public static String componentList(int exitCode, String message, String signature,
			String product, String version, List<String> componentList) {
		String components = "\""+String.join("\",\"", componentList)+"\"";
		return String.format("{\"response\":{\"code\":%s,\"message\":\"%s\"},\"signature\":\"%s\","
				+ "\"data\":{\"components\":[%s],\"version\":\"%s\",\"productName\":\"%s\"}}",
				exitCode, message, signature, components, version, product);
	}
}
