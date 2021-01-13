---
title: "Java Client Introduction"
date: 2019-09-24T20:07:51+08:00
draft: false
weight: 10
---

#Singleton Java Client Library
To facilitate consumption of the Singleton Service API, internationalization(i18n) client libraries have been
developed in several programming languages. These libraries encapsulate API calls to the Singleton service, 
significantly improving development efficiency and reducing the cost of development work. 
This section gives a high-level overview of the Singleton Java Client Library.

## Installation
The Singleton Java Client Library can be added as a dependency in your client Java application. 
Builds can be found in https://mvnrepository.com/artifact/com.vmware.singleton/singleton-client-java

## Features
- Translation - retrieving localized/translated text
- Formatting - retrieving formatted data using CLDR-compliant, localized patterns (e.g. date, time, number, currency) 
- Pluralization - retrieving singular/plural variations of data 
- Pseudo-translation - generating a fake(pseudo) translation from source text. This is often used for testing purposes.for testing purposes
- Client-side Localization(L10n) Cache - allows the client application to quickly serve L10n data 
without frequently communicating with the Singleton service.

## Globalization Modes
- Online   
- Offline  

## Configuration
The following parameters make up the Singleton configuration of a client application:

- productName - (Required) The name of the product with resources that are localized in the Singleton service.
- version: (Required) Corresponds to the versioned set of localized resources of the specified product.  
- vipServer: (Optional) The HTTP url of the Singleton service's host (<http/s>://\<hostname>/\<ip>:\<port>>).
If this parameter is not present, it means that Online Mode is not supported.
- offlineResourcesBaseUrl: (Optional if viServer is present) The classpath of locally bundled localized resources.
If this parameter is present, then Offline Mode is supported. If the vipServer parameter is also present, 
then Offline Mode will only be used as fallback mechanism in case of a Singleton service call failure.
- initializeCache - (Default: false) If true, it initializes the client-side G11n cache at application start up.
- pseudo - (Default: false) If true, pseudo-translation is enabled which means that the get translation API endpoint will 
return a fake(pseudo) translation generated from the source text instead of returning the text in its localized form.
- machineTranslation - (Default: false) Returns the MT (Machine Translation) output or not. The default value is false. Turned off at the server side by default.
- *true: Returns the MT (Machine Translation) output, regardless of whether the existing translation is here.*
- *false: Returns the translation in a normal way.*

## API
### Translation
See Javadoc at https://vmware.github.io/singleton/docs/overview/singleton-sdk/java-client-introduction/javadoc/com/vmware/vipclient/i18n/base/instances/TranslationMessage.html.
### Formatting
See Javadoc at https://vmware.github.io/singleton/docs/overview/singleton-sdk/java-client-introduction/javadoc/com/vmware/vipclient/i18n/base/instances/DateFormatting.html.

