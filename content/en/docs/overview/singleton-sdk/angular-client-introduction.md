---
title: "Angular Client Introduction"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

The Singleton Angular Client is a Singleton Service-based library used for SW i18n. Unlike the Singleton JavaScript Client mentioned earlier, the Angular Client is an end-to-end solution that includes specific data loading methods and the Angular-specific template API support. The Singleton Angular Client now supports the Angular 7.x.



## **Features Overview**

Getting and managing locales.

Loading i18n resources, including lazy loading mode.

Providing support for the l10n of individual components.

Loading the local translation resource files (for environments without Singleton Service).

Formatting the data: Formatting the date and time, number, and currency as defined in [CLDR](https://www.unicode.org/reports/tr35/tr35-dates.html) is supported at this time.

Formatting the string, including the singular and plural variations.

Providing support for pseudo translation, mainly for integration testing and local debugging.

Collecting source language strings in bulk.



# **Introducing Modules**

### Getting and managing locales

A locale includes two parts: language and region.

Language determines the translations to be shown in the current user interface, as well as rules to deal with singular and plural variations.

The combination of language and region determines how strings like the current date and time, number, and currency are shown.



### Defining components

Singleton provides a parameter named "component" to organize strings to be translated. For products with a large data volume, it is recommended to manage resources by component. Components can be categorized by function modules, or front-end/back-end attributes, provided that a component does not contain more than 5,000 strings.



### Loading i18n data

The i18n data (i.e. the information required for translation and data formatting) can be extracted according to the product information and the locale setting. Data can be loaded in a customized way according to the project size and the module attributes. If the project is small, you can load all the resources at once before the application starts. Otherwise, you can load the translations in the lazy loading mode of the module.



### Translation tool

The Singleton Angular Client provides pipes and directives to localize the UI by converting key-value pairs to strings according to the current language setting.



### Data formatting tool

The Singleton Angular Client provides pipes to format such data as the date and time, number and currency, among others, according to the current region settings.



## **APIs Available**



### **Import Modules**

##### Extract the source language strings into resource files

Extract all source language strings into resource files. The resource file contains an object named ENGLISH, where all source strings are stored as key-value pairs. The resource file should be named with the suffix ".l10n.ts", to help collect source language strings in bulk with a script.

Example:  source.l10n.ts

```
export const ENGLISH = {

    'app.title': 'Hello, world! ',

    'plural.apples': '{0, one{ # apple}  other{ # apples} }'

};
```



##### Importing the Singleton Angular Client module

Import VIPModule and other dependencies.

```javascript
import { HttpClientModule } from '@angular/common/http';

import { VIPModule, VIPService, LocaleService, PatternCategories} from '@vmw/ngx-vip';

@NgModule({

       imports: [

              HttpClientModule

              VIPModule.forRoot()

              …

       ],

       …

});
```



###  

### **Getting and managing locales**

##### Getting locales in two ways

Getting the language and region settings in the browser

API: getBrowserCultureLang (): string

Return value

The language and region settings used by the browser

Example

```javascript
import { getBrowserCultureLang  } from '@vmw/ngx-vip';

const locale = getBrowserCultureLang(); // eg: zh-CN
```

Get the languages and regions supported by the product from Singleton Service

Get the list of languages supported by the product

API: getSupportedLanguages(): Promise<languagesList>

Return value

The list of languages supported.

Get the list of regions supported by the product

API: getSupportedRegions(language: string): Promise<regionsList>

Parameters

language: (required), to indicate the language for the current use, which is used to show the list of regions returned.

Return value

The list of regions shown in the language specified in "language".

Example

```javascript
…

this.i18nService. getSupportedLanguages().then(

    (languageList) => { this. languageList = languageList; }

    // eg: [ { 'displayName': 'français', languageTag: 'fr' } ]

);

…

const currentLanguage = localStorage.getItem('currentLanguage');

this.i18nService. getSupportedRegions(currentLanguage).then(

(regionList) => { this. regionList = regionList;

    // eg: [ [ 'FR': 'France'], [ 'GE': 'Géorgie' ] ]

});

…
```



##### Set up the locale

Three methods are available to set up the locale, language, and region. When setLocale is used alone in the product, and there is only one parameter, the language is used to request translations, and the returned value for data formatting is the data required for formatting in the default region of the current language.

Set up the locale

Method: setCurrentLocale (languageTag: string, regionCode?: string, silentMode?: boolean = false)

Parameters

language: string (required), to indicate the language

region: string (optional) to indicate the region

silentMode: : Boolean (optional) the default value is false. When the locale of the page is changed, an event is triggered to update the translation on this page. However, when the application is initialized, this method is used to set the initial language and region only, without any event triggered. and the silentMode value is set to true.

Set up the language

Methods: setCurrentLanguage(languageTag: string)

Parameters:

languageTag: string (required), to indicate the language

Set up the region

Methods: setCurrentRegion(regionCode: string)

Parameters

regionCode: string (required), to indicate the region

Example

```javascript
import { LocaleService } from '@vmw/ngx-vip';

…

this.localeService.setCurrentLocale('en', 'US');

this.localeService.setCurrentLanguage ('en');

this.localeService.setCurrentRegion ('US');

…
```



### **Loading I18N Data**

Load the i18n resources from Singleton Service according to the product information and the initial language and region.

##### Initializing the Configuration Items

Parameters

productId: string (required), to indicate the product name

component: string (required), to indicate the component name

version: string (required), to indicate the release number

host: string (required), to indicate the Singleton server address. Example: http://localhost:8090

isPseudo: boolean (optional) , to indicate whether the pseudo translation is used. Setting to true specifies that the pseudo translation shall be returned, regardless of whether the translated string is here. The default value is false.

i18nScope: PatternCategories [ ] (optional), to indicate the scope for i18n support. The default value is empty.

- PatternCategories.DATE, to indicate the date and time format
- PatternCategories.NUMBER, to indicate the number and percentage format
- PatternCategories.CURRENCIES, to indicate the currency format

sourceBundle: {[key: string]: string} (optional), which is a collection of all source language strings. It is recommended to extract these strings into separate js/ts files

To ensure that every key (key name) is unique in the project, the following naming convention is recommended for keys: 'namespace.moduleName'

timeout: number (optional), to limit the time duration of the request, the default value is 3,000ms

Example

```javascript
const config = {

  productID: 'vipngxsample',

  component: 'default',

  version: '1.0.0',

  host: 'https://localhost:8090/', 

  i18nScope: [

      PatternCategories.DATE,

      PatternCategories.NUMBER,

      PatternCategories.CURRENCIES

  ],

  isPseudo: false,

  sourceBundle: ENGLISH,

  timeout: 5000

};
```





##### Blocking Loading

Implement the blocking loading with Angular's 'APP-INITIALIZER' API.

Example

```javascript
import { NgModule, APP_INITIALIZER } from '@angular/core';

import { VIPModule, VIPService, LocaleService, PatternCategories} from '@vmw/ngx-vip';

 

export function initI18nData(service: VIPService, localeService: LocaleService){

          localeService.setCurrentLocale('zh-Hans', 'CN', true);

          return () => service.initData(config);

}

@NgModule({

  imports: [

         HttpClientModule

         VIPModule.forRoot()

         …

  ],

providers: [

        {

            provide: APP_INITIALIZER,

            useFactory: initI18nData,

            deps: [

                VIPService,

                LocaleService

            ],

            multi: true

        }

]

  …

});
```



##### Non-Blocking Loading

Asynchronous data loading.

```javascript
import { NgModule, APP_INITIALIZER } from '@angular/core';

import { VIPModule, VIPService, LocaleService, PatternCategories} from '@vmw/ngx-vip';

@NgModule({

  imports: [

         HttpClientModule

         VIPModule.forRoot()

         …

  ],

…

});

export class AppModule {

    constructor(localeService: LocaleService, vipService: VIPService) {

         localeService.setCurrentLocale('zh-Hans', 'CN', true);

         vipService.initData(config);

    }

}
```



##### Lazy Loading Module

Use the Singleton Angular Client in lazy loading modules.

```
import { VIPModule, VIPService } from '@vmw/ngx-vip';

@NgModule({

           …

                 imports: [

                     …

                           VIPModule.forChild()

                 ],

             …

})

export class LazyModule {

                 constructor(private vipService: VIPService) {

                           this.vipService.initData(config);

           }

}; 

```



### **Translation Tool**

##### L10n Pipe

Use the pipe to get the translation of a string

Usage: {{ key | vtranslate:[ : variables [ : comment ] ]  }}

Parameters

key: string (required), to indicate the key value in sourceBundle

args: number|string[ ] (optional), to indicate the variable for index placeholders in the source string specified by key

Return value

The translation of the source string specified by key in SourceBundlekey.

Example

```javascript
{{ 'app.title'| vtranslate }};  // eg: 你好，世界！

// 单复数示例

{{ 'plural.apples' | vtranslate: 2}}  // eg: 2 apples

```



##### L10n Directive

Use directives to get the translation of a string

Selector: [l10n]

Parameters

key: string (required), to indicate the key value in sourceBundle

params: number|string[ ] (optional), to indicate the variable for index placeholders in the source string specified by key

Return value

The translation of the source string specified by key in SourceBundlekey

Example

```javascript
<span l10n='app.title'></span> // eg: 你好，世界！

// 单复数示例

<span l10n='plural.apples' [params]=[ 2 ] ></span> // eg: 2 apples

```



### **Formatting the Pipe**

##### Formatting the Date and Time

Format the specified date and time according to the locale setting

Usage: {{ value | dateFormat [ : format [ : timezone ] ] }}

Parameters

date: (required), to indicate the standard date object for i18n (ms or ISO-compliant date string). https://www.w3.org/TR/NOTE-datetime

pattern: string (optional) the default value is mediumDate, to indicate the date format shown after i18n

timezone: string (optional), to indicate the time zone, the default value is the current user system time zone

Return value

The formatted date string

Example

```javascript
{{ dateTime | dateFormat: 'short'}}  // eg: 8/19/19, 3:51 PM

```



##### Formatting the number

Format the specified number according to the locale setting.

Usage: {{ value | numberFormat }}

Parameters

value: number, to indicate the number to format

Return value

The formatted numeric string

Example

```javascript
{{ 1123.789 | numberFormat }}   // eg: 1,123.789

```



##### Formatting the percentage

Format the specified number as a percentage according to the locale setting.

Usage: {{ value | percentFormat }}

Parameters

value: number, to indicate the number to format

Return value

The formatted number in percentage

Example

```javascript
{{ 0.123 | percentFormat }};   // eg: 12%

```



##### Formatting the currency

Format the specified currency number as another style according to the locale setting. The default currency is USD.

Usage: {{ value | currencyFormat [ : currencyCode ]  }}

Parameters

value: number, to indicate the string to format

currencyCode: string, to indicate the currency code. See https://en.wikipedia.org/wiki/ISO_4217

Return value

The formatted string

Example

```javascript
{{ 0.23 |  currencyFormat: 'JPY' }};   // eg: 0¥

```



## **Scripting Tool**

##### Collecting the Source Language Strings in Bulk

When the source language strings are extracted into the resource files in the specified format, use the command tool to send them in bulk to Singleton Service, which triggers the translation process.

Parameters in CLI

collect-source-bundle

--source-dir <the folder path of the resource file>

--host <Singleton server address>

--product <Singleton product name>

--component <Singleton component name>

--version <Singleton release number>



Defining Commands in package.json

```javascript
{

    ...

    scripts: {   

        "collect-source ": " collect-source-bundle --source-dir ./src/source --product vipnodesample --component NodeJS --host http://localhost:8090 --version 1.0.0"

    }

    ...

}

```



Running Commands for Bulk Collection

```javascript
npm run collect-source

```



## **Sample Project**

Link to the sample project: [](https://github.com/vmware/singleton/tree/g11n-angular-client/sample)