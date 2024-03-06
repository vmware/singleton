---
title: "Java Client Introduction"
date: 2019-09-24T20:07:51+08:00
draft: false
weight: 10
---

# Singleton Java Client Library

To facilitate consumption of the Singleton Service API, internationalization(i18n) client libraries have been
developed in several programming languages. These libraries encapsulate API calls to the Singleton service, 
significantly improving development efficiency and reducing the cost of development work. 
This section gives a high-level overview of the Singleton Java Client Library.

## Features
- Translation - retrieving localized/translated text
- Formatting - retrieving formatted data using CLDR-compliant, localized patterns (e.g. date, time, number, currency) 
- Pluralization - retrieving singular/plural variations of data 
- Pseudo-translation - generating a fake(pseudo) translation from source text. This is often used for testing purposes.
- Client-side Localization(L10n) Cache - allows the client application to quickly serve L10n data 
without frequently communicating with the Singleton service.

## Globalization(G11n) Modes
A Java client application can be configured in one of the following modes.
- Online - The Singleton service serves localized resources to the client application over HTTP. 
- Offline - The client application uses localized resources that are stored in locally bundled files.   

Note: The client application can also be configured with both Online and Offline modes enabled. In this case, 
then Offline Mode will only be used as fallback in case of a Singleton service call failure.

## Installation
The Singleton Java Client Library must be added as a dependency in your client Java application. 
Builds can be found in [this](https://mvnrepository.com/artifact/com.vmware.singleton/singleton-client-java) Maven repository.
You may also see a sample application [here](https://github.com/vmware/singleton/tree/g11n-java-client/sample-client-app).

## Configuration
The following properties make up the Singleton configuration of a client application. 
Store them in a Properties file, and put the file in the application classpath.
See the [configuration file](https://github.com/vmware/singleton/blob/g11n-java-client/sample-client-app/src/main/resources/sampleconfig.properties) 
in the sample application for reference. 


- productName - (Required) The name of the product with resources that are localized in the Singleton service.
- version: (Required) Corresponds to the versioned set of localized resources of the specified product.  
- vipServer: (Optional) The HTTP url of the Singleton service's host (<http/s>://\<hostname>/\<ip>:\<port>>).
Note: If this parameter is not present, it means that Online Mode is disabled.
- offlineResourcesBaseUrl: (Optional if viServer is present) The location of localized resources (locally bundled JSON files) 
relative to the application classpath. See sample JSON files in 
[here](https://github.com/vmware/singleton/tree/g11n-java-client/sample-client-app/src/main/resources/offlineBundles).
Note: If this parameter is not present, then Offline Mode is disabled. 
- initializeCache - (Default: false) If true, it initializes the client-side G11n cache at application start up.
- pseudo - (Default: false) If true, pseudo-translation is enabled which means that the get translation API endpoint will 
return a fake(pseudo) translation generated from the source text instead of returning the text in its localized form.
- machineTranslation - (Default: false) If true, the get translation API endpoint will return a machine-generated 
translation(Machine Translation or MT). Note: This feature is off by default in Singleton service (server side). 

## Initialization
A few objects provided by the Singleton Java Client Library has to be instantiated and initialized before 
using the API. This includes loading the configuration parameters from the Properties file above. 
See how this is done in the [sample application](https://github.com/vmware/singleton/blob/g11n-java-client/sample-client-app/src/main/java/com/vmware/vipclient/sample/Main.java). 

## API
After initialization, the client application may already use the following API to access localized content:

|L10n Data Type|Javadoc|
|----------|----------|
|Translation|[TranslationMessage](https://vmware.github.io/singleton/docs/overview/singleton-sdk/resources/java-client/0.6.3/javadoc/com/vmware/vipclient/i18n/base/instances/TranslationMessage.html)|
|Date|[DateFormatting](https://vmware.github.io/singleton/docs/overview/singleton-sdk/resources/java-client/0.6.3/javadoc/com/vmware/vipclient/i18n/base/instances/DateFormatting.html)|
|Number|[NumberFormatting](https://vmware.github.io/singleton/docs/overview/singleton-sdk/resources/java-client/0.6.3/javadoc/com/vmware/vipclient/i18n/base/instances/NumberFormatting.html)|
|Supported Locales|[LocaleMessage](https://vmware.github.io/singleton/docs/overview/singleton-sdk/resources/java-client/0.6.3/javadoc/com/vmware/vipclient/i18n/base/instances/LocaleMessage.html)|