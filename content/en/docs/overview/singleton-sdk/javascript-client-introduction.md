---
title: "Javascript Client Introduction"
date: 2019-09-24T20:08:23+08:00
draft: false
weight: 20
---

The Singleton JavaScript Client is a Singleton Service-based JavaScript library used for SW i18n. It supports all JavaScript frameworks, you can run it in browsers, as well as NodeJS environments. With the Singleton JavaScript Client, it is much easier to use the Web front-end for SW i18n. Now, let's look at what the Client does, and how it is used.

## Features Overview

- Getting and managing locales.
- Getting the i18n resources from Singleton Service.
- Loading the local translation resource files (for environments without Singleton Service).
- Formatting the data: Formatting the time, number, and currency as defined in CLDR is supported at this time.
- Formatting the string, including the singular and plural variations.
- Providing support for pseudo translation, mainly for integration testing and local debugging.
- Collecting source language strings in bulk.

## Introducing Modules

### Getting and managing locales

A locale includes two parts: language and region.

Language determines the translations to be shown in the current user interface, as well as rules to deal with singular and plural variations.

The combination of language and region determines how strings like the current date and time, number, and currency are shown.

### Defining components

Singleton provides a parameter named "component" to organize strings to be translated. For products with a large data volume, it is recommended to manage resources by component.

Components can be categorized by function modules, or front-end/back-end attributes, provided that a component does not contain more than 5,000 strings.

### Loading i18n data

The i18n data (i.e. the information required for translation and data formatting) can be extracted according to the product information and the locale setting.

Please be sure to load all the i18n data before calling the relevant i18n API.

## APIs Available

### Getting locales

##### Getting locales in two ways

**Getting the language and region settings in the browser**

API: getBrowserCultureLang (): string

Return value

The language and region settings used by the browser

Example

```
import { getBrowserCultureLang  } from ‘@vip/vip-core-sdk’;
const locale = getBrowserCultureLang(); // eg: zh-CN
```

**Get the languages and regions supported by the product from Singleton Service**

- Get the list of languages supported by the product

API: getSupportedLanguages( displayLang?: string ): Promise

Parameters

displayLang: string (optional) Used to show the list of languages returned. If it is left blank, each language in the list shall be shown in its native way. The default value is blank.

Return value

The list of languages supported.

- Get the list of regions supported by the product

API: getSupportedRegions(language: string): Promise

Parameters

language: string (required) Used to show the list of regions returned.

Return value

The list of regions shown in the chosen language.

Example

```
…
i18nClient.i18nService. getSupportedLanguages().then(
(languageList) => { this. languageList = languageList; }
// eg: [ { displayName: ‘français’, languageTag: ‘fr’ } ]
);
…
const currentLanguage = localStorage.getItem(‘currentLanguage’);
i18nClient.i18nService. getSupportedRegions(currentLanguage).then(
(regionList) => { this. regionList = regionList; });
// eg: [ [ ‘FR’: ‘France’], [ ‘GE’: ‘Géorgie’ ] ]
…
```

### Loading i18n data

Loading the i18n resources from Singleton Service according to the product information.

##### Creating an instance of the Client for i18n

**Initializing the configuration items**

Methods

init(configs)

createInstance(configs)

Parameters

productId: string (required), to indicate the product name

component: string (required), to indicate the component name

version: string (required), to indicate the release number

host: string (required), to indicate the Singleton server address. Example: http://localhost:8091

isPseudo: boolean (optional) , to indicate whether the pseudo translation is used. Setting to true specifies that the pseudo translation shall be returned, regardless of whether the translated string is here. The default value is false.

language: string (optional), to indicate the language string (e.g. 'zh-Hans'). You can use the current language in the browser. The default value is 'en'.

region: string (optional), to indicate the region string (e.g. 'CN'). The language and the region must be defined together. The default value is 'US'.

i18nScope: PatternCategories [ ] (optional), to indicate the scope for i18n support. The default value is empty.

- PatternCategories.DATE, to indicate the date and time format.
- PatternCategories.NUMBER, to indicate the number and percentage format.
- PatternCategories.CURRENCIES, to indicate the currency format.

sourceBundle: {[key: string]: string} (optional), which is a collection of all source language strings. It is recommended to extract these strings into separate js/ts files.

- Value: An object with the key as the unique string, and its value as the source English string.
- All the resource files should be named as "xxx.l10n.js", which is helpful to collect the source language strings with scripts.
- To ensure that every key (key name) is unique in the project, the following naming convention is recommended for keys: 'namespace.moduleName'.
- The type of Array is supported, and the duplicate entries can be removed.

timeout: number (optional), to limit the time duration of the request, the default value is 3,000ms.

##### Load the data

Load the i18n data according to the configuration of the instance. If you divide the product into several components, you should call this method multiple times for different instances.

Methods: loadI18nData( callback?: () => void ): Promise

Parameters

The callback function to be executed when the request is complete, which is used to initialize the UI.

Return value

Promise of the data request.

Example

```
import { vipClient, getBrowserCultureLang, PatternCategories} from '@vip/vip-core-sdk';
…
const i18nClient = vipClient.init(
{
       productID: 'vipnodesample',
       version: '1.0.0',
       component: 'NodeJS',
       host: 'http://localhost:8091',
       language: currentLanguage,
       i18nScope: [PatternCategories.DATE],
       region: currentRegion,
       sourceBundle : {
    'app.title': 'Hello, world!',
    'plural.apples': '{0, one{ # apple}  other{ # apples} }'
},
       timeout: 5000
}
);
i18nClient.loadI18nData().then( () => {
…
});
```

### **Translation API**

##### Getting the Translation

Getting the Translation of a String

Methods: getMessage(key: string, args?:[ number|string ]): string

Parameters

key: string (required), to indicate the key value in sourceBundle

args: number|string[ ] (optional), to indicate the variable for index placeholders in the source string specified by key

Return value

The translation of the source string specified by key in SourceBundlekey.

Example

```
const translation = i18nClient.l10nService.getMessage('app.title');  // eg: 你好，世界！
// 单复数示例
const apple =  i18nClient.l10nService.getMessage('plural.apples', [2]);  // eg: 2 apples
```

### **Formatting API**

##### Formatting the Date and Time

Format the specified date and time according to the locale setting

Methods: formatDate(dateTime: number|Date|string, pattern: string = ‘short’, timezone?: string): string

Parameters

date: (required), to indicate the standard date object for i18n (ms or ISO-compliant date string). https://www.w3.org/TR/NOTE-datetime

pattern: (required), to indicate the date format shown after i18n

timezone: (optional), to indicate the time zone

Return value

The formatted date string

Example

```
i18nClient.i18nService.formatDate(new Date(),' short');  // eg: 8/19/19, 3:51 PM
```

##### Formatting the Number

Format the specified number according to the locale setting.

Methods: formatNumber(value: number): string

Parameters

The number to be formatted

Return value

The formatted numeric string

Example

```
i18nClient.i18nService.formatNumber(1123.7892);   // eg: 1,123.789
```

##### Formatting the Percentage

Format the specified number as a percentage according to the locale setting.

Methods: formatPercent(value: number): string

Parameters

The number to be formatted

Return value

The formatted number in percentage

Example

```
i18nClient.i18nService. formatPercent (0.123);   // eg: 12%
```

##### Formatting the Currency

Format the specified currency number as another style according to the locale setting. The default currency is USD.

Methods: formatCurrency(value: number|string, currencyCode: string = ‘USD’): string

Parameters

value: The number to be formatted

currencyCode: The currency code. See https://en.wikipedia.org/wiki/ISO_4217

Return value

The formatted string

Example

```
i18nClient. i18nService.formatCurrency(0.23, 'JPY');   // eg: 0¥
```

## **Scripting Tool**

##### Collecting the Source Language Strings in Bulk

After you extract the source language strings into a single type of resource files, you can use the command tool to send the strings to Singleton Service in bulk, for the translation team to work on.

Parameters in CLI

```
collect-source-bundle
--source-dir <资源文件所在文件夹路径>
--host <Singleton服务器地址>
--product <Singleton产品名称>
--component <Singleton 组件名称>
--version <Singleton 发布版本>

```

Defining Commands in package.json

```
{
...
scripts: {
"collect-source ": " collect-source-bundle --source-dir ./src/source --product vipnodesample --component NodeJS --host http://localhost:8090 --version 1.0.0"
}
...
}

```

Running Commands for Bulk Collection

```
npm run collect-source

```

## **Sample Project**

Link to the sample project: https://github.com/vmware/singleton/tree/g11n-js-client