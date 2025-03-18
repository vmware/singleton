/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.sample;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.util.FormatUtils;
import org.junit.Assert;

public class TranslationDemo {
	static TranslationMessage t = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);
	static String key = "global_text_username";
	static Locale locale = Locale.ENGLISH;
	static String component = "default";
	
	public static void demo(Locale locale) {
		getMessage(locale);
	
		getMessage();
		getMessageLocaleNotSupported();
		getMessageNewKeyInSource();
		getMessageNewlyUpdatedSourceMsg();
		getMessageKeyNotFound();
		getMessageWithSimpleArg();
	}
	
	public static String getMessage(Locale locale) {
		System.out.println(">>>>>> TranslationDemo.getMessage(Locale \"" + locale.toLanguageTag() + "\") key: \"" + key + "\"");
		String msg = t.getMessage(locale, component, key);
		System.out.println(msg);
		return msg;
	}
	
	/**
	 * Demonstrates how to get a message in a few supported locales
	 */
	private static void getMessage() {
		System.out.println(">>>>>> TranslationDemo.getMessage start");
		// See offline mode supported languages inside the offlineResourcesBaseUrl path 
		// The offlineResourcesBaseUrl path is configured in sampleconfig.properties
		
		String enMessage = getMessage(Locale.ENGLISH);
		assertEquals("User name", enMessage);
		
		String enUSMessage = getMessage(Locale.forLanguageTag("en-US"));
		assertEquals("User name", enUSMessage);
		
		String filMessage = getMessage(Locale.forLanguageTag("fil"));
		assertEquals("Pangalan ng gumagamit", filMessage);
		
		String frMessage = getMessage(Locale.FRENCH);
		assertEquals("Nom d'utilisateur", frMessage);
		
		System.out.println(">>>>>> TranslationDemo.getMessage success");
	}
	
	/**
	 * Demonstrates how getting a message in the default locale 
	 * if the requested locale is not supported  
	 * Note: Service call must fail for the offline mode to kick in
	 */
	private static void getMessageLocaleNotSupported() {
		System.out.println(">>>>>> TranslationDemo.getMessageLocaleNotSupported start");
		// Chinese is supported neither in online or offline mode. 
		// See offline mode supported languages inside the offlineResourcesBaseUrl path 
		// The offlineResourcesBaseUrl path is configured in sampleconfig.properties
		String chMessage = t.getMessage(Locale.KOREAN, component, key);
		
		// Use default locale instead. The default locale is configured in sampleconfig.properties
		assertEquals("Nom d'utilisateur", chMessage); 
		
		System.out.println(">>>>>> TranslationDemo.getMessageLocaleNotSupported success");
	}
	
	/**
	 * Demonstrates that a new/untranslated source message will be displayed, regardless of the locale passed
	 */
	private static void getMessageNewKeyInSource() {
		System.out.println(">>>>>> TranslationDemo.getMessageNewKeyInSource start");
		//"new.key" is a new key that can be found only in messages_source.json
		String chMessage = t.getMessage(Locale.CHINESE, component, "new.key");
		String filMessage = t.getMessage(Locale.forLanguageTag("fil"), component, "new.key");
		
		// Use source message that is in messages_source.json
		assertEquals("New message", chMessage); 
		assertEquals("New message", filMessage); 
		
		System.out.println(">>>>>> TranslationDemo.getMessageNewKeyInSource success");
	}
	
	private static void getMessageNewlyUpdatedSourceMsg() {
		System.out.println(">>>>>> TranslationDemo.getMessageNewlyUpdatedSourceMsg start");
		// messages_source.json has "updated.message": "Updated message"
		// But messages_en.json has "updated.message": "Old message"
		// This means that the source message hasn't been collected and translated
		String frMessage = t.getMessage(Locale.FRENCH, component, "updated.message");
		
		// Use message that is in messages_source.json
		assertEquals("Updated message", frMessage); 
		
		System.out.println(">>>>>> TranslationDemo.getMessageNewlyUpdatedSourceMsg success");
	}
	
	private static void getMessageKeyNotFound() {
		System.out.println(">>>>>> TranslationDemo.getMessageKeyNotFound start");
		
		VIPJavaClientException e = null;
		try {
			t.getMessage(Locale.FRENCH, component, "key.not.found");
		} catch (VIPJavaClientException exc) {
			e = exc;
		}
		
		assertEquals(FormatUtils.format(ConstantsMsg.GET_MESSAGE_FAILED, "key.not.found", component, Locale.FRENCH), 
				e.getMessage());
		
		System.out.println(">>>>>> TranslationDemo.getMessageKeyNotFound success");
	}

	private static void getMessageWithSimpleArg(){
		System.out.println(">>>>>> TranslationDemo.getMessageWithSimpleArg start");
		final long timestamp = 1511156364801l;
		Object[] arguments = {
				7,
				new Date(timestamp),
				"a disturbance in the Force"
		};
		// get expected date string based on default timezone
		Date d = new Date(timestamp);
		final SimpleDateFormat df = new SimpleDateFormat("MMMMM d, yyyy");
		df.setTimeZone(TimeZone.getDefault());
		final SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
		tf.setTimeZone(TimeZone.getDefault());
		String dateString = tf.format(d) + " on " + df.format(d);

		String includeFormatMessage = t.getMessage(Locale.ENGLISH, component, "sample.includeFormat.message",
				arguments);
		System.out.println("message with number&date arguments: " + includeFormatMessage);
		Assert.assertEquals("At " + dateString + ", there was a disturbance in the Force on planet 7.", includeFormatMessage);
		System.out.println(">>>>>> TranslationDemo.getMessageWithSimpleArg success");
	}
}
