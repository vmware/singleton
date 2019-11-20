---
title: "Download and Installation"
date: 2019-09-24T20:04:26+08:00
draft: false
Weight: 10
---

Singleton official 0.1.0 Release is available now! Choose the download that's right for you and click one of the links below.

Singleton Service 0.1.0 Binaries
================================

Downloading
-----------

filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-manager-i18n-0.1.0.jar](http://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-i18n/0.1.0/singleton-manager-i18n-0.1.0.jar) | `8e7599495292ce34cdb9769ec43695999071a1fe` | master/[v0.1.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.1.0-Singleton-Service)
[singleton-manager-l10n-0.1.0.jar](http://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-l10n/0.1.0/singleton-manager-l10n-0.1.0.jar) | `8d744bb83e310a1b451a68fb65999803488c8cfc` | master/[v0.1.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.1.0-Singleton-Service)

Installation
------------

Singleton Service include 2 types of builds, one is i18n manager, the other is l10n manager:

* I18n manager is the main build, it provides the [restful APIs](https://vmware.github.io/singleton/docs/overview/singleton-service/singleton-service-apis/) to talk with Singleton SDK.
* L10n manager will work together with I18n manager to achieve the feature [Source Collection](https://vmware.github.io/singleton/docs/overview/singleton-service/configurations/enable-source-collection/).

**Prerequisites**

* [JDK8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) is installed and works well on your environment, as Singleton Service is based on JAVA language development.
* The ports (8088, 8090, 8091) are allowed to access and not in use, as Singleton service run based on these ports.

**To start Singleton Service I18n manager**
```
java -jar singleton-manager-i18n-0.1.0.jar
```
**To Start Singleton Service L10n manager**
```
java -jar singleton-manager-l10n-0.1.0.jar
```

Singleton SDK 0.1.0 Binaries
============================

Downloading
-----------

filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-client-java-0.1.0.jar ](http://repo1.maven.org/maven2/com/vmware/singleton/singleton-client-java/0.1.0/singleton-client-java-0.1.0.jar) | `d27afa9e1659759805e320daab7d8d0e5f7b8da2` | g11n-java-client/[v0.1.0-Singleton-Java-Client](https://github.com/vmware/singleton/releases/tag/v0.1.0-Singleton-Java-Client)
[@singleton-i18n/js-core-sdk@0.1.0](https://www.npmjs.com/package/@singleton-i18n/js-core-sdk/v/0.1.0) | `1e776e866ded0751d39ea391cae29b412089fcc1` | g11n-js-client/[v0.1.0-Singleton-JS-Client](https://github.com/vmware/singleton/releases/tag/v0.1.0-Singleton-JS-Client)
[@singleton-i18n/angular-client@0.1.0](https://www.npmjs.com/package/@singleton-i18n/angular-client/v/0.1.0) | `8f8ae3ca2ef6e1bfccd828fc1b9e4c6e74f6be20` | g11n-angular-client/[v0.1.0-Singleton-Angular-Client](https://github.com/vmware/singleton/releases/tag/v0.1.0-Singleton-Angular-Client)
