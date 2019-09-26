---
title: "Quick Start"
date: 2019-09-24T20:04:47+08:00
draft: false
weight: 20
---

The Singleton application suite includes:

* Singleton Service: It contains an Application Program Interface (API), which is a web service that is used for i18n features, such as providing translated text.
* Singleton Client: It is used to communicate with Singleton Service to get the localized resources.

Features and functionality
==========================

Singleton offers the following features and functionality:

* Singleton Core API - Provides RESTful web services for delivering localized resources.
* Source collection - Offers a source collection feature for Singleton string-based APIs. For more information about how to use the feature, see the **_Enabling source collection on Singleton service_** section of the Singleton's User Guide.
* Pseudotranslation – Helps test product compatibility with locales before beginning the localization process. It automatically generates text that is designed to test various aspects of localization readiness, including character set support, UI design, and hardcoding. For more information about how to use the feature, see the **_Enabling pseudo translation on Singleton service_** section of the Singleton's User Guide.

Using Singleton
===============

Downloading Singleton source code
---------------------------------

**Prerequisites**

+ Ensure the following applications are installed:
    - [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
    - [Git](https://git-scm.com/downloads)
+ Set the environment variables:
    - [Setting JAVA_HOME](https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/)
    - [Adding JAVA_HOME to the Path system variable](https://WWW.JAVA.COM/EN/DOWNLOAD/HELP/PATH.XML)

**Step**

Clone the repository to your environment using Git, by running the command ` git clone git@github.com:vmware/singleton.git `.

Compiling Singleton source code
-------------------------------

From the `singleton/g11n-ws` folder, generate the Singleton build file using the Gradle command ` ./gradlew build`.

The .jar files are generated in the `singleton/publish` location. For example:

`singleton/publish/vip-manager-i18n-0.1.0.jar`

Running Singleton application
-----------------------------

To run the Singleton application, from the singleton/g11n-ws folder, run the Spring Boot main application, using the command: `java - jar publish/vip-manager-i18n-0.1.0.jar`.

NOTE: To test all available API endpoints from the user interface, see https://localhost:8090/i18n/api/doc/swagger-ui.html or http://localhost:8091/i18n/api/doc/swagger-ui.html.

NOTE: Sample translation resources are available in the following location: `singleton/publish/l10n/bundles`

You can test the API using the following URI/request parameters:
```
productName: "SampleProject"

version: "1.0.0"

component: "component1" or "component2"

locale: "en", "ja" or "es"
```