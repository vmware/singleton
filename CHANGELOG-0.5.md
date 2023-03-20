- [v0.5.1](#v051)
  - [Downloads for v0.5.1](#downloads-for-v051)
    - [Service Binaries](#service-binaries)
    - [Client Binaries](#client-binaries)
  - [Changelog since v0.5.0](#changelog-since-v050)
    - [Main Changes](#main-changes)
      - [SDK](#sdk)
    - [Improvement](#improvement)
- [v0.5.0](#v050)
  - [Downloads for v0.5.0](#downloads-for-v050)
    - [Service Binaries](#service-binaries-1)
    - [Client Binaries](#client-binaries-1)
  - [Changelog since v0.4.0](#changelog-since-v040)
    - [Main Changes](#main-changes-1)
      - [Service](#service)
      - [SDK](#sdk-2)
    - [Known Iusses](#known-issues)
    - [Improvement](#improvement-1)
    - [Action Required](#action-required)

v0.5.1
-------
[Documentation](https://vmware.github.io/singleton/)

## Downloads for v0.5.1

### Service Binaries
filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-manager-i18n-s3-0.5.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-i18n-s3/0.5.0/singleton-manager-i18n-s3-0.5.0.jar) | - | No update
[singleton-manager-i18n-0.5.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-i18n/0.5.0/singleton-manager-i18n-0.5.0.jar) | - | No update
[singleton-manager-l10n-0.5.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-l10n/0.5.0/singleton-manager-l10n-0.5.0.jar) | - | No update

### Client Binaries
filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-client-java-0.5.1.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-client-java/0.5.1/singleton-client-java-0.5.1.jar) | `ccfa8ca6b5de896861bda7f4a5ed89bece3ca9b0` | g11n-java-client/[v0.5.1-Singleton-Javaclient](https://github.com/vmware/singleton/releases/tag/v0.5.1-Singleton-Java-Client)
[@singleton-i18n/js-client](https://www.npmjs.com/package/@singleton-i18n/js-core-sdk/v/0.5.0--) | - | No update
[@singleton-i18n/nodejs-client](https://www.npmjs.com/package/@singleton-i18n/js-core-sdk-server/v/0.5.0--) | - | No update
[@singleton-i18n/angular-client](https://www.npmjs.com/package/@singleton-i18n/angular-client/v/0.2.0) | - | No update
[singleton-go-client](https://github.com/vmware/singleton/tree/g11n-go-client) | - | No update

## Changelog since v0.5.0

### Main Changes
#### SDK
#### JAVA Client
- Support for replacing placeholders/arguments using MAP (placeholder key, replacing value) in the message ([#618](https://github.com/vmware/singleton/issues/618))
- Fixed - ComponentService.getMessages is not retrieving "matchedLocale" properly ([#781](https://github.com/vmware/singleton/issues/781))
- Fixed - New workflow's Translation.getMessage must be able to use latest source messages from VIP service ([#812](https://github.com/vmware/singleton/issues/812))
- Fixed - The offlineResourceBaseURL inside a shared lib is not being read if main app is is run from IDE and not built as an executable jar ([#868](https://github.com/vmware/singleton/issues/868))
- Fixed - Non-blocking getSupportedLocales when fetching messages ([#877](https://github.com/vmware/singleton/issues/877))
- Fixed - Java client offline mode fails in nested-jar applications such as Spring Boot([#882](https://github.com/vmware/singleton/issues/882))

### Improvement
#### SDK
#### JAVA Client
- Upgrade to Java 11 in sonar cloud scan([#709](https://github.com/vmware/singleton/issues/709)) 
- Upgrade jackson to 2.11.2 ([#779](https://github.com/vmware/singleton/issues/779))

v0.5.0
-------
[Documentation](https://vmware.github.io/singleton/)

## Downloads for v0.5.0

### Service Binaries
filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-manager-i18n-s3-0.5.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-i18n-s3/0.5.0/singleton-manager-i18n-s3-0.5.0.jar) | `71118c84c5bec1b3c7856f78e8b1f1c9d46ded26` | master/[v0.5.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.5.0-Singleton-Service)
[singleton-manager-i18n-0.5.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-i18n/0.5.0/singleton-manager-i18n-0.5.0.jar) | `99ce6e694e070cb5c8f0774ce8fd636a9cb25f95` | master/[v0.5.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.5.0-Singleton-Service)
[singleton-manager-l10n-0.5.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-manager-l10n/0.5.0/singleton-manager-l10n-0.5.0.jar) | `fa2ab395d34934b5a6ed6ae87b90c53bb8f55585` | master/[v0.5.0-Singleton-Service](https://github.com/vmware/singleton/releases/tag/v0.5.0-Singleton-Service)

### Client Binaries
filename | sha1 hash | branch/tag
-------- | --- | ------
[singleton-client-java-0.5.0.jar](https://repo1.maven.org/maven2/com/vmware/singleton/singleton-client-java/0.5.0/singleton-client-java-0.5.0.jar) | `c8db72dd399af38be5682bd4fa4f59325b486170` | g11n-java-client/[v0.5.0-Singleton-Javaclient](https://github.com/vmware/singleton/releases/tag/v0.5.0-Singleton-Java-Client)
[@singleton-i18n/js-client](https://www.npmjs.com/package/@singleton-i18n/js-core-sdk/v/0.5.0--) | - | No update
[@singleton-i18n/nodejs-client](https://www.npmjs.com/package/@singleton-i18n/js-core-sdk-server/v/0.5.0--) | - | No update
[@singleton-i18n/angular-client](https://www.npmjs.com/package/@singleton-i18n/angular-client/v/0.2.0) | - | No update
[singleton-go-client](https://github.com/vmware/singleton/tree/g11n-go-client) | - | g11n-go-client/[v0.5.0-Singleton-Go-Client](https://github.com/vmware/singleton/releases/tag/v0.5.0-Singleton-Go-Client)

## Changelog since v0.4.0

### Main Changes
#### Service
- Support more fomatting options for langauge displayName in getSupportedLanguageList API ([#361](https://github.com/vmware/singleton/issues/361))
- Add etag/max-age in Translation APIs response ([#442](https://github.com/vmware/singleton/issues/442))
- Support to customize more items for cache-control ([#482](https://github.com/vmware/singleton/issues/482))
- Add scopeFilter to pattern API ([#515](https://github.com/vmware/singleton/issues/515))
- Add pattern data about compact number formats ([#555](https://github.com/vmware/singleton/issues/555))
- Add Microsoft Translator API Regional Source Support ([#566](https://github.com/vmware/singleton/pull/566))
- Support to customize API request header buffer size ([#586](https://github.com/vmware/singleton/issues/586))
- Add pattern data numberingSystems for Numbers ([#616](https://github.com/vmware/singleton/issues/616)) 
- Add key-based GET API ([#648](https://github.com/vmware/singleton/issues/648))
- Add API to GET localized city name list ([#675](https://github.com/vmware/singleton/issues/675)), currently only English city name returned
- Compress Singleton Service responses to improve UI performance ([#747](https://github.com/vmware/singleton/issues/747))
- Fixed - Don't support version fallback in getSupportedLanguageList API ([#338](https://github.com/vmware/singleton/issues/338))
- Fixed - Add copyright and license info in more code files ([#366](https://github.com/vmware/singleton/issues/366), [#397](https://github.com/vmware/singleton/issues/397))
- Fixed - Failed to get pattern data when request non-existing product in combination API ([#372](https://github.com/vmware/singleton/issues/372))
- Fixed - No parameter verification for some fileds in combination API ([#373](https://github.com/vmware/singleton/issues/373), [#495](https://github.com/vmware/singleton/issues/495))
- Fixed - Missing settings.gradle under tools\tool-cldr-extractor ([#651](https://github.com/vmware/singleton/issues/651))

#### SDK
#### JAVA Client
- Improve cache mechanism ([#439](https://github.com/vmware/singleton/issues/439))
- Add capability of loading local translation bundle ([#444](https://github.com/vmware/singleton/issues/444)); Please refere to [#here](https://vmware.github.io/singleton/docs/overview/singleton-sdk/java-client-introduction/) for more details.
- Updating the sample application to support offline mode ([#556](https://github.com/vmware/singleton/pull/556))
- Add sample code about using shared product ([#725](https://github.com/vmware/singleton/issues/725))
- Fixed - Error logging for Unsuccessful L10n Fetch ([#544](https://github.com/vmware/singleton/pull/544))
- Fixed - Wrong process when judging cache item expiration ([#604](https://github.com/vmware/singleton/issues/604))
- Fixed - Failed to initialize client when initializeCache=true in pure offline mode ([#661](https://github.com/vmware/singleton/issues/661))
- Fixed - When getMessage() returns to default locale's translation, cache can't get updated translation(default locale) after it is expired ([#662](https://github.com/vmware/singleton/issues/662))
- Fixed - When getMessages() returns to default locale's translation, cache can't get updated translation(default locale) after it is expired. ([#664](https://github.com/vmware/singleton/issues/664))
- Fixed - If cache item is expired, and online bundle fetch failed, offline fetch is triggered ([#686](https://github.com/vmware/singleton/issues/686))
- Fixed - TranslationMessage.getMessage for non-supported locales always triggers a request to VIP service in a separate thread ([#717](https://github.com/vmware/singleton/issues/717))
- Fixed - MsgOriginsQueue cannot be cleared once initializing VIPCfg ([#746](https://github.com/vmware/singleton/issues/746))

#### Go Client
- Improve cache mechanism ([#440](https://github.com/vmware/singleton/issues/440))
- Remove multiple component API in go client ([#419](https://github.com/vmware/singleton/issues/419))
- Fixed - Wrong process when judging cache item expiration ([#606](https://github.com/vmware/singleton/issues/606))
- Fixed - Offline fetch is triggered when cache item is expired and online bundle fetch failed ([#692](https://github.com/vmware/singleton/issues/692))
- Fixed - Can't get translation when version fallback occured in service ([#694](https://github.com/vmware/singleton/issues/694))

### Known Issues
#### Service

#### Go Client
- Unit test case 'TestGetStringAbnormal' failed ([#995](https://github.com/vmware/singleton/issues/995))

### Improvement
#### Service
- Upgrade tomcat to 9.0.37 ([#665](https://github.com/vmware/singleton/issues/665))
- Upgrade ant to 1.10.8 ([#613](https://github.com/vmware/singleton/issues/613))
- Upgrade log4j to 2.13.2 ([#563](https://github.com/vmware/singleton/issues/563))
- Upgrade guava to 20.0 ([#581](https://github.com/vmware/singleton/issues/581))
- Upgrade jackson-databind to 2.11.2, commons-collections to 3.2.2 ([#737](https://github.com/vmware/singleton/issues/737))

### Action Required
- The **POST** combination API will be **Deprecated**, please replace it by **GET** API
- Remove fallback to *English* content when the translation no exist for request locale ([#468](https://github.com/vmware/singleton/issues/468)), please pay attention on this if you use Singleton Service API directly, suggest to use Singleton Client to avoid this kind of issue
