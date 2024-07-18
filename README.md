Singleton Library for Java Clients
============

Several client libraries are provided to support sending API requests to Singleton service. Each library, when integrated on the client side, also provides additional features such as post-processing of localized resources and caching.

Below are details on how to use the library for Java clients.

Prerequisites
------------
 * Run the Singleton service by following the instructions in [here](https://github.com/vmware/singleton/blob/master/README.md).
 * Ensure the following are installed:     
    - [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (also support Java 9+)
    - [Git](https://git-scm.com/downloads)
    - [Gradle](https://gradle.org/install/)

How to build and use the client library
------------
 * Clone the repository using Git.
    ```
    git clone git@github.com:vmware/singleton.git g11n-java-client
    ```
    
 * Go to the project's root directory.
    ```
    cd g11n-java-client
    ```
    
 * Checkout the client library branch.
    ```
    git checkout g11n-java-client
    ```
    
 * Build the client library.
    ```
    gradle createWrapper
    gradlew clean build -x test
    ```

    Note: The library jar will be created under "build/libs" directory.
    
 * Import the library jar into your Java application and use its available APIs.

Note: The file src/main/resources/vipconfig.properties is a template configuration file that contains properties that can be configured as needed. It is preconfigured to work with default Singleton service settings (See Prerequisites). Use this as the Singleton configuration file, or use it as a template to create your own. 
To load Singleton configurations in your application:

```Java
// Initialize
VIPCfg cfg = VIPCfg.getInstance();
cfg.initialize("sampleconfig");
cfg.initializeVIPService();
cfg.createTranslationCache(MessageCache.class);
cfg.createFormattingCache(FormattingCache.class);
I18nFactory.getInstance(cfg);
```

Sample code
------------

```Java
// Import classes
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.base.instances.DateFormatting;

// Initialize global setting
VIPCfg cfg = VIPCfg.getInstance();
cfg.initialize("vipconfig");
cfg.initializeVIPService();
I18nFactory i18n = I18nFactory.getInstance(cfg);

// Create TranslationMessage Instance
cfg.createTranslationCache(MessageCache.class);
TranslationMessage tm = (TranslationMessage)i18n.getMessageInstance(TranslationMessage.class);
String translation = tm.getMessage(…);

// Create Formatting instance
cfg.createFormattingCache(FormattingCache.class);
DateFormatting dateformatting = (DateFormatting)i18n.getFormattingInstance(DateFormatting.class);
dateformatting.formatDate(…);
```

Existing features
------------
 * Provides default I18n (translation and formatting) instances as well as supporting customized I18n instance.
 * Provides cache management as well as supporting customized cache management.
 * Supports locale matching and fallback.
 * Supports the switch between local bundle (offline mode) and data from Singleton server (online mode).
 * Supports getting pseudo translations on development stage.
 * Provides configured filters extended from javax.servlet.Filter as a proxy for client(browser) to communicate with Singleton server.
 * Provides fmt tag to support JSP localization.

Upcoming features 
------------
 * Support for formatting (date, number, currency, etc.) in offline mode

Request for contributions from the community
------------
 * 
   
Sample application
------------
 A sample application is provided [here](https://github.com/vmware/singleton/tree/g11n-java-client/sample-client-app).

