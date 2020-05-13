---
title: "Integrate Singleton in Java App"
date: 2019-09-24T20:09:42+08:00
draft: false
weight: 10
---

This guideline demonstrates how to integrate Singleton client(Java) in your application.

Singleton Client(Java) is a common lib providing I18n support for Java application. The client will communicate with Singleton service to fetch translation and I18n pattern data for providing globalization support in messages and formatting.

For I18n formatting, Singleton Client(Java) also provides I18n support for datetime format, number format, currency and plural. Singleton Client(Java) formatting is based on CLDR data repository and keeps the same scope in supported locale set.


## Requirement
 - Java 8+
 - Dependency libraries:  
     json-simple-1.1.1.jar
     slf4j-api-1.7.26

## Build
All of the builds could be found at this path: https://repo1.maven.org/maven2/com/vmware/singleton/singleton-client-java

## Sample Codes of API Usage
This is the example of how to use the APIs:
```Java
// Import classes
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
// Initialize global setting
VIPCfg cfg = VIPCfg.getInstance();
cfg.initialize("vipconfig");
cfg.initializeVIPService();
cfg.createTranslationCache(MessageCache.class);
// Create TranslationMessage Instance
TranslationMessage t = (TranslationMessage) I18nFactory.getInstance(cfg).getMessageInstance(TranslationMessage.class);
String translation = t.getString(…);
// Create Formatting instance
cfg.createFormattingCache(FormattingCache.class);
DateFormatting dateformatting = (DateFormatting)i18n.getFormattingInstance(DateFormatting.class);
dateformatting.formatDate(…);
```
More JAVA APIs details: https://vmware.github.io/singleton/docs/overview/singleton-sdk/java-client-introduction/

## Sample App
https://github.com/vmware/singleton/tree/g11n-java-client/sample-client-app
