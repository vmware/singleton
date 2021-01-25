---
title: "C# Client Introduction"
date: 2020-03-15T20:07:51+08:00
draft: false
weight: 40
---

# Introduction
C# Client is a class libary running on Windows .NET Framework. It enables applications that can use .NET Framework libraries to handle its globalization easily.

# Features in C# Client

- Getting the translation.
- Getting messages by cache interfaces.
- Getting configuration content.
- Enrich implementations by extending different extension interfaces.

# APIs Available
* [Factory Class](#Factory-Class)
   * [I18N](#I18N)
* [Interface For Configuration](#Interface-For-Configuration)
   * [IConfig](#IConfig)
   * [IConfigItem](#IConfigItem)
* [Interface For Release](#Interface-For-Release)
   * [IRelease](#IRelease)
* [Interface For Translation](#Interface-For-Translation)
   * [ITranslation](#ITranslation)
   * [ISource](#ISource)
* [Interface For Cache Messages](#Interface-For-Cache-Messages)
   * [IReleaseMessages](#IReleaseMessages)
   * [ILocaleMessages](#ILocaleMessages)
   * [IComponentMessages](#IComponentMessages)
* [Interface For Extension](#Interface-For-Extension)
   * [IExtension](#IExtension)
   * [ICacheManager](#ICacheManager)
   * [ICacheMessages](#ICacheMessages)
   * [ILog](#ILog)
   * [IResourceParser](#IResourceParser)
   * [IAccessService](#IAccessService)

## Factory Class
### I18N

* It's the factory class that creates and initializes the configuration object and the release object.

```csharp
public sealed class I18N
{
    public static IConfig LoadConfig(string resourceBaseName, Assembly assembly, string configResourceName);
    public static IConfig GetConfig(string product, string version);
    public static IRelease GetRelease(IConfig config);
    public static IExtension GetExtension();
}
```

### I18N / LoadConfig

* Load a configuration json or yaml text from the resource defined by a resx file and initialize a correspondent release object.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| resourceBaseName | string | Resource base name |
| assembly | [Assembly](https://docs.microsoft.com/en-us/dotnet/api/system.reflection.assembly?view=netcore-3.1) | It owns the resource |
| configResourceName | string | Configuration resource name after base name |

| Return | Description |
| ------ | ------ |
| [IConfig](#IConfig) | Configuration Object |

### I18N / GetConfig

* Get a loaded configuration object by product and version.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| product | string | Product name |
| version | string | L10n version |

| Return | Description |
| ------ | ------ |
| [IConfig](#IConfig) | Configuration Object |

### I18N / GetRelease

* Get the release object initialized by a configuration object.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| config | [IConfig](#IConfig) | Configuration object |

| Return | Description |
| ------ | ------ |
| [IRelease](#IRelease) | The release object |

### I18N / GetExtension

* Get the extension interface to change ways of implementation.

| Return | Description |
| ------ | ------ |
| [IExtension](#IExtension) | Extension interface |


## Interface For Configuration
### IConfig
```csharp
public interface IConfig
{
    IConfigItem GetItem(string key);
    List<string> GetComponentList();
    IConfigItem GetComponentAttribute(string component, string key);
    List<string> GetLocaleList(string component);
    IConfigItem GetLocaleAttribute(string component, string locale, string key);
    string ReadResourceText(string resourceBaseName, string resourceName);
    Hashtable ReadResourceMap(string resourceName, string format, string locale);
}
```

### IConfig / GetItem

* Get the definition item by key.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| key | string | Key text |

| Return | Description |
| ------ | ------ |
| [IConfigItem](#IConfigItem) | The definition item object |

### IConfig / GetComponentList

* Get the component list.

| Return | Description |
| ------ | ------ |
| [List](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.list-1)\<string\> | The component list |

### IConfig / GetComponentAttribute

* Get the definition item of an attribute of the component.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component name |
| key | string | Key text |

| Return | Description |
| ------ | ------ |
| [IConfigItem](#IConfigItem) | The definition item object |

### IConfig / GetLocaleList

* Get the defined locale list of a component.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component name |

| Return | Description |
| ------ | ------ |
| [List](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.list-1)\<string\> | The locale list |

### IConfig / GetLocaleAttribute

* Get the definition item of an attribute of the locale information for a component.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component name |
| locale | string | Locale name |
| key | string | Key text |

| Return | Description |
| ------ | ------ |
| [IConfigItem](#IConfigItem) | The definition item object |

### IConfig / ReadResourceText

* Read a text from the Assembly resource.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| resourceBaseName | string | Resource base name |
| resourceName | string | Resource name |

| Return | Description |
| ------ | ------ |
| string | Text |

### IConfig / ReadResourceMap

* Read a key-value map from a resx resource.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| resourceName | string | Resource name |
| format | string | Format to select parser |
| locale | string | Locale name |

| Return | Description |
| ------ | ------ |
| [Hashtable](https://docs.microsoft.com/en-us/dotnet/api/system.collections.hashtable) | Key-value table |


### IConfigItem
```csharp
public interface IConfigItem
{
    string GetString();
    List<string> GetStringList();
    bool GetBool();
    int GetInt();
    IConfigItem GetMapItem(string key);
    List<string> GetArrayItemList(string key);
    IConfigItem GetArrayItem(string key, string value);
}
```

### IConfigItem / GetString

* Get string value of the configuration item.

| Return | Description |
| ------ | ------ |
| string | Text |

### IConfigItem / GetStringList

* Get string list of the configuration item.

| Return | Description |
| ------ | ------ |
| [List](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.list-1)\<string\> | Defined string list |

### IConfigItem / GetBool

* Get boolean value of the configuration item.

| Return | Description |
| ------ | ------ |
| bool | Boolean value |

### IConfigItem / GetInt

* Get integer value of the configuration item.

| Return | Description |
| ------ | ------ |
| int | Integer value |

### IConfigItem / GetMapItem

* Read the child configuration item by a key.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| key | string | Key |

| Return | Description |
| ------ | ------ |
| [IConfigItem](#IConfigItem) | The child configuration item |

### IConfigItem / GetArrayItemList

* Get a string list from the configuration child item array by a key.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| key | string | Key |

| Return | Description |
| ------ | ------ |
| [List](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.list-1)\<string\> | An extracted string list from the configuration child item array|

### IConfigItem / GetArrayItem

* Read the child configuration item by a key and its correspondent value from a configuration item array.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| key | string | Key |
| value | string | Requested value |

| Return | Description |
| ------ | ------ |
| [IConfigItem](#IConfigItem) | The child configuration item |


## Interface For Release
### IRelease
```csharp
public interface IRelease
{
    IConfig GetConfig();
    IReleaseMessages GetMessages();
    ITranslation GetTranslation();
}
```

### IRelease / GetConfig

* Get the configuration object owned the release object.

| Return | Description |
| ------ | ------ |
| [IConfig](#IConfig) | The configuration object |

### IRelease / GetMessages

* Get the configuration object owned by the release object.

| Return | Description |
| ------ | ------ |
| [IReleaseMessages](#IReleaseMessages) | The interface to access messages |

### IRelease / GetTranslation

* Get the interface to access translation.

| Return | Description |
| ------ | ------ |
| [ITranslation](#ITranslation) | The interface to access translation |

## Interface For Translation
### ITranslation
```csharp
public interface ITranslation
{
    ISource CreateSource(string component, string key, string source = null, string comment = null);
    string GetString(string locale, ISource source);
    string GetString(string component, string key, string source = null, string comment = null);
    string Format(string locale, ISource source, params object[] objects);
    bool SetCurrentLocale(string locale);
    string GetCurrentLocale();
    string GetLocaleSupported(string locale);
}
```

### ITranslation / CreateSource

* Create a source object for other methods.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component |
| key | string | Key |
| source | string | Source |
| comment | string | Comment |

| Return | Description |
| ------ | ------ |
| [ISource](#ISource) | The source object |

### ITranslation / GetString

* Get translation message.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |
| [ISource](#ISource) | The source object |

| Return | Description |
| ------ | ------ |
| string | The translation message |

### ITranslation / GetString

* Get translation message according to current locale.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component |
| key | string | Key |
| source | string | Source |
| comment | string | Comment |

| Return | Description |
| ------ | ------ |
| string | The translation message |

### ITranslation / Format

* Get translation message according to the format.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |
| [ISource](#ISource) | The source object |
| object[] | objects to fill format |

| Return | Description |
| ------ | ------ |
| string | The translation message |

### ITranslation / SetCurrentLocale

* Set current locale.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |

| Return | Description |
| ------ | ------ |
| bool | Return true if successful |

### ITranslation / GetCurrentLocale

* Get current locale.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |

| Return | Description |
| ------ | ------ |
| string | Locale |

### ITranslation / GetLocaleSupported

* Get supported or nearest locale of the locale.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |

| Return | Description |
| ------ | ------ |
| string | Supported or nearest locale |

### ISource
```csharp
public interface ISource
{
    string GetComponent();
    string GetKey();
    string GetSource();
    string GetComment();
}
```

## Interface For Cache Messages
### IReleaseMessages
```csharp
public interface IReleaseMessages
{
    List<string> GetLocaleList();
    List<string> GetComponentList();
    ILocaleMessages GetLocaleMessages(string locale, bool asSource = false);
    Dictionary<string, ILocaleMessages> GetAllLocaleMessages();
}
```

### IReleaseMessages / GetLocaleList

* Get the locale list of release messages.

| Return | Description |
| ------ | ------ |
| [List](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.list-1)\<string\> | The locale list |

### IReleaseMessages / GetComponentList

* Get the component list of release messages.

| Return | Description |
| ------ | ------ |
| [List](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.list-1)\<string\> | The component list |

### IReleaseMessages / GetLocaleMessages

* Get the locale messagess from release messages by locale.

| Parameter | Type | Description | Default |
| ------ | ------ | ------ | ------ |
| locale | string | Locale |  |
| asSource | bool | If messages are of source | false |

| Return | Description |
| ------ | ------ |
| [ILocaleMessages](#ILocaleMessages) | The interface to access locale messages |

### IReleaseMessages / GetAllLocaleMessages

* Get all locale messagess of the release messages that don't include source.

| Return | Description |
| ------ | ------ |
| [Dictionary](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.dictionary-2)\<string, [ILocaleMessages](#ILocaleMessages)\> | Map from locale to its locale messages |

### ILocaleMessages
```csharp
public interface ILocaleMessages
{
    string GetLocale();
    List<string> GetComponentList();
    IComponentMessages GetComponentMessages(string component);
    string GetString(string component, string key);
}
```

### ILocaleMessages / GetLocale

* Get locale of the messages.

| Return | Description |
| ------ | ------ |
| string | Locale name |

### ILocaleMessages / GetComponentList

* Get the component list of locale messages.

| Return | Description |
| ------ | ------ |
| [List](https://docs.microsoft.com/en-us/dotnet/api/system.collections.generic.list-1)\<string\> | The component list |

### ILocaleMessages / GetComponentMessages

* Get the component messagess from locale messages by component.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component name |

| Return | Description |
| ------ | ------ |
| [IComponentMessages](#IComponentMessages) | The interface to access component messages |

### ILocaleMessages / GetString

* Get the message from locale messages by component and key.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component name |
| key | string | Key |

| Return | Description |
| ------ | ------ |
| string | Translation message |

### IComponentMessages
```csharp
public interface IComponentMessages
{
    void SetString(string key, string message);
    string GetString(string key);
    ICollection GetKeys();
    int GetCount();
    string GetLocale();
    string GetComponent();
    void SetResourcePath(string resourcePath);
    string GetResourcePath();
    void SetResourceType(string resourceType);
    string GetResourceType();
}
```

### IComponentMessages / SetString

* Set message of the key.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| key | string | Key |
| message | string | Message text |

### IComponentMessages / GetString

* Get the message from component messages by key.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| key | string | Key |

| Return | Description |
| ------ | ------ |
| string | Translation message |

### IComponentMessages / GetKeys

* Get all keys of component messages.

| Return | Description |
| ------ | ------ |
| [ICollection](https://docs.microsoft.com/en-us/dotnet/api/system.collections.icollection) | Collection of keys |

### IComponentMessages / GetCount

* Get count of messages.

| Return | Description |
| ------ | ------ |
| int | Count of messages |

### IComponentMessages / GetLocale

* Get locale of component messages.

| Return | Description |
| ------ | ------ |
| string | Locale |

### IComponentMessages / GetComponent

* Get component name of component messages.

| Return | Description |
| ------ | ------ |
| string | Component name |

### IComponentMessages / SetResourcePath

* Set resource path where stores the component messages.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| resourcePath | string | The resource path or resource id |

### IComponentMessages / GetResourcePath

* Get resource path where stores the component messages.

| Return | Description |
| ------ | ------ |
| string | The resource path or resource id |

### IComponentMessages / SetResourceType

* Set resource type which is used to know how the resource is stored and what parser to select.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| resourceType | string | The resource type |

### IComponentMessages / GetResourceType

* Get resource type.

| Return | Description |
| ------ | ------ |
| string | The resource type |

## Interface For Extension
### IExtension
```csharp
public interface IExtension
{
    void RegisterCacheManager(ICacheManager cacheManager, string cacheManagerName);
    void RegisterCacheComponentManager(ICacheComponentManager cacheComponentManager,
        string cacheComponentManagerName);
    void RegistertLogger(ILog logger, string loggerName);
    void RegisterResourceParser(IResourceParser parser, string parserName);
    void RegisterAccessService(IAccessService accessService, string accessName);
}
```

### IExtension / RegisterCacheManager

* Register cache manager object with its name.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| cacheManager | [ICacheManager](#ICacheManager) | Cache manager object |
| cacheManagerName | string | Cache manager name |

### IExtension / RegisterCacheComponentManager

* Register component cache manager object with its name.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| cacheComponentManager | [ICacheComponentManager](#ICacheComponentManager) | Component cache manager object |
| cacheComponentManagerName | string | Component cache manager name |

### IExtension / RegistertLogger

* Register a logger with its name.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| logger | [ILog](#ILog) | Logger object |
| loggerName | string | Logger name |

### IExtension / RegisterResourceParser

* Register a resource parser with its name.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| parser | [IResourceParser](#IResourceParser) | Parser object |
| parserName | string | Parser name |

### IExtension / RegisterAccessService

* Register an accessing service object with its name.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| accessService | [IAccessService](#IAccessService) | Accessing service object |
| accessName | string | Access name |

### ICacheManager
```csharp
public interface ICacheManager
{
    ICacheMessages GetReleaseCache(string product, string version);
}
```

### ICacheManager / GetReleaseCache

* Get the release cache by product and version.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| product | string | Product |
| version | string | L10n version |

| Return | Description |
| ------ | ------ |
| [ICacheMessages](#ICacheMessages) | The cache object for a release |

### ICacheMessages
```csharp
public interface ICacheMessages
{
    ILocaleMessages GetLocaleMessages(string locale, bool asSource = false);
}
```

### ICacheMessages / GetLocaleMessages

* Get the locale messages from the release cache.

| Parameter | Type | Description | Default |
| ------ | ------ | ------ | ------ |
| locale | string | Locale |  |
| asSource | bool | If messages are of source | false |

| Return | Description |
| ------ | ------ |
| [ILocaleMessages](#ILocaleMessages) | The locale messages |

### ICacheComponentManager
```csharp
public interface ICacheComponentManager
{
    IComponentMessages NewComponentCache(string locale, string component);
}
```

### ICacheComponentManager / NewComponentCache

* Create a new component messages object.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |
| component | string | Component |

| Return | Description |
| ------ | ------ |
| [IComponentMessages](#IComponentMessages) | The component messages for a locale |

### ILog
```csharp
public interface ILog
{
    void Log(LogType logType, string text);
}
```

### ILog / Log

* Log a string with a type.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| logType | [LogType](#LogType) | Log type |
| text | string | Log text |

### LogType
```csharp
public enum LogType
{
    Debug,
    Info,
    Warning,
    Error,
    None
}
```

### IResourceParser
```csharp
public interface IResourceParser
{
    Hashtable Parse(string text);
}
```

### IResourceParser / Parse

* Parse a text into a key-value map.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |

| Return | Description |
| ------ | ------ |
| [Hashtable](https://docs.microsoft.com/en-us/dotnet/api/system.collections.hashtable) | The key-value map |

### IAccessService
```csharp
public interface IAccessService
{
    string HttpGet(string url, Hashtable headers);
    string HttpPost(string url, string text, Hashtable headers);
}
```

### IAccessService / HttpGet

* Get data from http service.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| url | string | Remote location |
| headers | [Hashtable](https://docs.microsoft.com/en-us/dotnet/api/system.collections.hashtable) | Headers map |

| Return | Description |
| ------ | ------ |
| string | Text data from remote |

### IAccessService / HttpPost

* Send data to http service.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| url | string | Remote location |
| text | string | Text data to be sent |
| headers | [Hashtable](https://docs.microsoft.com/en-us/dotnet/api/system.collections.hashtable) | Headers map |

| Return | Description |
| ------ | ------ |
| string | Response text data |

