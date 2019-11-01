---
title: "Overview"
date: 2019-09-24T22:58:41+08:00
draft: false
weight: 20
---

Singleton helps streamline the globalization process for software products. It provides a unified solution for product internationalization (i18n) and localization (L10n) across most of the popular programming languages, frameworks, and a wide range of developer tools such as Java, JavaScript, Angular applications.

The Singleton application suite includes:

* Singleton Service: It contains an Application Program Interface (API), which is a web service that is used for i18n features, such as providing translated text.
* Singleton Client: It is used to communicate with Singleton Service to get the localized resources.

### How Singleton works

Singleton separates the localized resources including: embedded strings, locale-sensitive methods, functions, or classes (for example: date/ time formatting, number formatting), custom programming patterns, and static files from the product software. Using the Singleton Service (web service that provides a unified API), it sends the source artifacts or resources for translation. The localized resources are then processed externally and embedded into the Singleton Service.

Singleton eliminates the need for developers to learn different APIs for internationalization across technologies and programming languages. It acts as an abstraction layer that provides consistent formatting of i18n to various applications that may be written in different programming languages. The web service API exposes REST endpoints for i18n that provides abstraction across multiple clients.

### Benefits of using Singleton

* Consistency: Singleton offers unified i18n and L10n implementation across programming languages and frameworks (from Java, Python, to various JavaScript frameworks). The unified implementation enables consistent product behaviors (such as date/time format, number format, searching and sorting results) and increases product quality and CSAT.
* Flexibility: Decoupling i18n and L10n from the product makes possible for asynchronous, continuous, and flexible localization delivery without impacting the the product.
* Productivity: The unified solution brings in higher productivity and reduces cost for both developers and G11n to deliver localized products.
* Agility: Singleton brings in simpler and automated product localization It shortens the translation delivery time and increases the agility of product localization to meet SaaS requirements.

Features and functionality
--------------------------

Singleton offers the following features and functionality:

* Singleton Core API - Provides RESTful web services for delivering localized resources.
* Source collection - Offers a source collection feature for Singleton string-based For more information about how to use the feature, see section - **_[Enabling source collection on Singleton service](#enabling-source-collection-on-singleton-service)_**.
* Pseudotranslation – Helps test product compatibility with locales before beginning the localization process. It automatically generates text that is designed to test various aspects of localization readiness, including character set support, UI design, and hardcoding. For more information about how to use the feature, see section - **_[Enabling pseudo translation on Singleton service](#enabling-pseudotranslation-on-singleton-service)_**.

Using Singleton
---------------

### Downloading Singleton source code

**Prerequisites**

* Ensure the following applications are installed:
    * [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
    * [Git](https://git-scm.com/downloads)
* Set the environment variables:
    * [Setting JAVA_HOME](https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/)
    * [Adding JAVA_HOME to the Path system variable](https://WWW.JAVA.COM/EN/DOWNLOAD/HELP/PATH.XML)

**Step**

Clone the repository to your environment using Git, by running the command `git clone git@github.com:vmware/singleton.git`.

### Compiling Singleton source code

From the `singleton/g11n-ws` folder, generate the Singleton build file using the Gradle command - `./gradlew build`.

The .jar files are generated in the `singleton/publish` location. For example:

`singleton/publish/vip-manager-i18n-0.1.0.jar` 

### Running Singleton application

To run the Singleton application, from the `singleton/g11n-ws` folder, run the Spring Boot main application, using the command: `java - jar publish/vip-manager-i18n-0.1.0.jar`

NOTE: To test all available API endpoints from the user interface, see https://localhost:8090/i18n/api/doc/swagger-ui.html or http://localhost:8091/i18n/api/doc/swagger-ui.html.

NOTE: Sample translation resources are available in the following location: `singleton/publish/l10n/bundles`


You can test the API using the following URI/request parameters:
```
productName: "SampleProject"

version: "1.0.0"

component: "component1" or "component2"

locale: "en", "ja" or "es"
```

Creating the resource files
---------------------------

To deploy Singleton source code and translation data files, you must first create or convert all the product resource files to JSON file.

NOTE: If your product already has localized resource files, you must convert these files to JSON files manually to retain the existing translations. Please refer to the JSON file structure and folder structure from `singleton/publish/l10n/bundles`.

Enabling source collection on Singleton service
-----------------------------------------------

About this task

To enable the source collection feature on Singleton, both **vip-manager-l10n-0.1.0.jar** and **vip-manager-i18n-0.1.0.jar** under `singleton/publish` forlder are required.

NOTE: This feature works for Singleton string-based and component-based APIs.

Steps

* Open the `vip-manager-i18n-0.1.0.jar/BOOT-INF/classes/application-bundle.properties` file and make the following changes:
```
// enables source collection 
#collection source server
source.cache.flag = true
source.cache.server.url = https://VIPl10n-ip-address:8088
```

* Run `java -jar vip-manager-i18n-0.1.0.jar` command to start

* Open the `vip-manager-l10n-0.1.0.jar/BOOT-INF/classes/application.properties` file and make the following changes:

```
// save new or updated source strings to local bundle
#sync source to bundle and GRM
sync.source.enable=true

// schedule for writing source strings from cache to local bundles 
sync.source.schedule.cron=0 0/1 * * * ?
polling.grm.schedule.cron=*/5 * * * * ?

// path of local bundle files for source strings
source.bundle.file.basepath = /data/l10n/

// path of local bundle files for translated strings
// (can be the same as source.bundle.file.basepath)
translation.bundle.file.basepath = /data/l10n/

// The source can be sent to the Translation Server to do translation directly by this setting, if set to local, it means the source don’t be forward to Translation Server
grm.server.url = local

// This item can be set as local, it means the source don’t be forward to VIP service
#sync source to VIP service
vip.server.url = https://VIPi18n-ip-address:8090
```
* Run `java -jar vip-manager-l10n-0.1.0.jar` command to start the source collector If required, you can choose to run the source collector (l10n) on the same server as Singleton (i18n), but please put these 2 jar files in different folders. 
You can perform source collection by calling the Singleton service by setting collectSource to true. For example:
```
https://VIPi18n-ip-address:8090/i18n/api/v1/translation/product/Testing/component/default/key/test.key.1?source=test%20key%201&version=1.0.0&locale=zh_CN&collectSource=true
```
For source text exceeding 10,000 characters, use the POST string-based API. The API is located in the following location: 
```
https://VIPi18n-ip-address:8090/i18n/api/doc/swagger-ui#!/translation-product-component-key-api/getTranslationByPostUsingPOST_1.
```
You can verify the results of the source creation by setting the pseudo to true and locale to en. For example:
```
https://VIPi18n-ip-address:8090/i18n/api/v1/translation/product/Testing/component/default?version=1.0.0&locale=en&pseudo=true
```

Enabling pseudotranslation on Singleton service
-----------------------------------------------

To obtain pseudotranslation from a Singleton product, component, or string-based API, you must set pseudo to true. For example:
```
https://VIPi18n-ip-address:8090/i18n/api/v1/translation/product/Testing/component/default/key/testing?source=my%20source&version=1.0.0&locale=zh_CN&commentForSource=test&pseudo=true
```
There are 4 types for the returned content:

* Localized string enclosed in @ signs (for example, @5341ç 张 133f@)

This tag indicates that the key and source string exist in the backend on the current Singleton build and is returned by the API call. And you can uniquely locate the string in resource file according to "5341".

* @

This tag indicates that the key and source string exist in the backend on the current Singleton build. The tag is dynamically generated by the Singleton server.

* @@

This tag indicates that the key and source string could not be found on Singleton. It should be new strings, please do source collection then.

* source without any tags

This indicates that the string is hard code, doesn't integrated with Singleton.

Singleton Service APIs
----------------------

After installing the Singleton build, you can access the Singleton APIs. A user interface for testing all available API endpoints is available in the following URL:
```
https://localhost:8090/i18n/api/doc/swagger-ui.html 
or
http://localhost:8091/i18n/api/doc/swagger-ui.html                                             
```
The sample translation resources are available in the following location:

`singleton/publish/l10n/bundles`                                                                           


To test the API, you can use the following parameters as URI/request parameters:
```
productName: "SampleProject"

version: "1.0.0"

component: "component1" or "component2"

locale: "en", "ja" or "es"
```

Sample translation resources
```
{
"component" : "component1",
"messages": {
"sample.apple" : "apple",
"sample.banana" : "banana",
"sample.cat" : "cat",
"sample.dog" : "dog",
"sample.egg" : "egg",
"sample.fly" : "fly",
"sample.giant" : "giant"
},
"locale" : "en"
}
```
```
{
"component" : "component1",
"messages" : {
"sample.apple" : "manzana",
"sample.banana" : "plátano",
"sample.cat" : "gato",
"sample.dog" : "perro",
"sample.egg" : "huevo",
"sample.fly" : "volar",
"sample.giant" : "gigante"
},
"locale" : "es"
}
```
```
{
"component" : "component1",
"messages" : {
"sample.apple" : "林檎",
"sample.banana" : "バナナ",
"sample.cat" : "ネコ",
"sample.dog" : "犬",
"sample.egg" : "卵",
"sample.fly" : "飛ぶ",
"sample.giant" : "巨人"
},
"locale" : "ja"
}
```
```
{
"component" : "component1",
"messages": {
"sample.apple" : "apple",
"sample.banana" : "banana",
"sample.cat" : "cat",
"sample.dog" : "dog",
"sample.egg" : "egg",
"sample.fly" : "fly",
"sample.giant" : "giant"
},
"locale" : "latest"
}
```

Singleton client libraries
--------------------------

Several client libraries are provided to support sending API requests to Singleton service. Each library, when integrated on the client side, also provides additional features such as post-processing of localized resources and caching.

### Supported programming languages and web frameworks

* Java
* JS
* Angular

### Integrating and using Singleton client library

Singleton Client libraries are located in the following sub-branch of the Singleton repository: [https://github.com/vmware/singleton](https://github.com/vmware/singleton).

Instructions on how to integrate each library with a Singleton client can be found on the README.md document of each library:

* Java - [https://github.com/vmware/singleton/tree/g11n-java-client](https://github.com/vmware/singleton/tree/g11n-java-client)
* js - [https://github.com/vmware/singleton/tree/g11n-js-client](https://github.com/vmware/singleton/tree/g11n-js-client)
* Angular - [https://github.com/vmware/singleton/tree/g11n-angular-client](https://github.com/vmware/singleton/tree/g11n-angular-client)