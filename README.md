# I18NClient.NET

## Overview

I18N .NET client is tailored for the next generation of internationalization development. In a multi-cloud environment, the development of large-scale software project source strings and the way to obtain translations are very different from traditional internationalization software development. 

> Fast, convenient and flexible are the most basic requirements.

I18N .NET client is based on .NET Standard 2.0 with full pluggable architecture, aiming to provide i18n support (localization and I18n data formatting) for most .NET related applications.

Current Features Include:

- Full Pluggable Architecture
- Local Resource Bundle Loading
- Remote Resource Loading
- Configurable Resource Loading Strategy
  - Remote Only
  - Local Only
  - Mixed
- Configurable Cache Manager
- I18N Resource Management
- Runtime Culture Management

## Installation

Nuget:

```bash
Install-package I18NClient.NET
```

## Basic Usage

1.  Configure the `I18NClientOptions` according to the business requirement, in the actual application development process, these options are recommended to be defined  and configured in the application level configuration file, and then loaded to the I18NClientOptions at runtime.

   ```c#
   var options = new I18NClientOptions()
   {
       ProductName = "Sample",
       Version = "1.0.0",
       SupportedLanguages = new List<string>() {
          "en-US",
          "zh-CN",
          "de"
       },
       BackendServiceUrl = "http://localhost:8080",
       OfflineResourceRelativePath = "Resources.{0}.LabelValues",
       DefaultLanguage = "en-US",
       MaxDegreeOfParallelism  = 20
   };
   ```

2. Select and assemble plug-ins according to actual business needs. For example, the source string may come from a local file, or the strings may be stored in the DB due to historical reasons, or it may be loaded through a web service. There are also many different options for the loading method of translation, especially in some large-scale projects, the pre-design did not consider the needs of internationalization. 

   > Note: The configuration of the plug-in is not mandatory, we have the recommended process with default configuration.

   ##### Default

   ```c#
   var client = new I18NClientBuilder(options).Build();
   ```
   
   ##### Mixed loading strategy
   
   There is a usage scenario where the source string and basic translation are saved in a local resource file, and a remote translation service is also connected to update the local translation cache, which not only ensures the speed and stability of application UI loading, but also reaches the need to update the translation at runtime.
   
   ```c#
   var client = new I18NClientBuilder(options)
       .WithLoadingStrategy<MixedLoadingStrategy>()
       .Build();
   ```
   
   ##### Customize foundation services
   
   All foundation services can be replaced according to business needs, such as logger, cache and loader, etc.
   
   ```c#
   var client = new I18NClientBuilder(options)
       .WithComponentMessageCacheManager<CustomizedCacheManager>()
       .WithLocalLoader<ResxLocalLoader>()
       .WithRemoteLoader<SingletonRemoteLoader>()
       .WithLogger<TraceLogger>()
       .Build();
   ```
   
   > Note: It is best to build I18N client at application start, and then use it in different components and services in the form of dependency injection.

3. *Cache warming*, here are several cache warm-up schemes and cache refresh schemes.

   ##### By component and language

   This is suitable for caching translations according to functional modules, cache translation on demand.

   ```c#
   var success = await client.ResourceService.TryInitTranslationsByComponentAsync("Apps", "en-US");
   ```

   ##### By product and language

   In many cases, you may only need to warm up some commonly used languages or source languages by product scope.

   ```c#
   var success = await client.ResourceService.TryInitTranslationsByLanguageAsync("en-US");
   ```

   ##### By product

   If you need to warm up all strings without distinguishing between languages, this API is the right choice.

   ```c#
   var success = await client.ResourceService.TryInitTranslationsByProductAsync();
   ```

4.  Runtime message call, at the resource service level, some low-level APIs will be provided to obtain related i18n resources.

   ```c#
   var supportedComponentList = await client.ResourceService.GetSupportedComponentListAsync();
   var supportedLanguageList = await client.ResourceService.GetSupportedLanguageListAsync();
   var component = await client.ResourceService.GetTranslationsByComponentAsync("Account", "en-US");
   var translation = await client.ResourceService.GetTranslationByKeyAsync("Account", "ALLOW_HTTP_HTTPS", "zh-CN");
   ```

> Note: The public APIs in this lib are asynchronous , in most cases, you should access the value by using **Await** or **await** instead of accessing the property directly, but in some cases if you need synchronous APIs, please use the APIs from resource service extension which have been wrapped to avoid deadlock issues. 

## API



## Compatibility


| .NET Implementation | Version Support |
| ------ | ------ |
| .NET Core and .NET 5 | 2.0, 2.1, 2.2, 3.0, 3.1, 5.0 |
| .NET Framework | 4.6.1, 4.6.2, 4.7, 4.7.1, 4.7.2, 4.8 |
| Mono | 5.4, 6.4 |
| Xamarin.iOS | 10.14, 12.16 |
| Xamarin.Android | 8.0, 10.0 |
| Universal Windows Platform | 10.0.16299, TBD |
| Universal Windows Platform | 2018.1 |

## Todo

- [x] Message localizer
- [ ] Datetime formatter
- [ ] Number formatter
- [ ] Plural handler
