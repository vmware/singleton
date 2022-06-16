---
title: "Locale Management"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 17
---

#### **Overview**

A locale consists of a number of categories for which country-dependent formatting or other specifications exist. A program's locale defines its code sets, date and time formatting conventions, monetary conventions, decimal formatting conventions, and collation (sort) order. A locale name can be composed of a base language, country (territory) of use, and codeset.


For example, German language is de, an abbreviation for Deutsch, while Swiss German is de_CH, CH being an abbreviation for Confederation Helvetica. This convention allows for specific differences by country, such as currency unit notation. The locale naming convention is:**language[_territory][.codeset][@modifier]**


singleton client uses language to determine the translations that need to be displayed in the user interface, as well as the rules for singular plural processing and relative time display. The combination of region and language determines the format of the date time, number, currency, and other string display.


#### **Get available languages & regions**

If using a single language from the browser, this language will be treated as a locale for both L2 and L3 support.


##### **Language**

Provides two ways to get the supported languages: user browser language setting and the language from the user preference. There is one API to get the supported language of the current product, user can pick up one language as a preferred language, then stores into the backend DB. The recommended way is collecting both language and region information from the user as a user profile, this will work well with some complex situations.


Get user language from browser:

```
getBrowserCultureLang(): string

```

```
import { getBrowserCultureLang } from '@singleton-i18n/angular-client';
const language = getBrowserCultureLang(); // eg: zh-CN

```


Get supported languages list from singleton:

```
getSupportedLanguages(): Promise<languagesList>;

```

```
import { I18nService } from '@singleton-i18n/angular-client';
...
this.i18nService.getSupportedLanguages().then( languages => {
    this.languages = languages;
    // eg: [ { displayName: "English", languageTag: "en" },
    //       { displayName: "français", languageTag: "fr } ]
});
...

```


##### **Region**

As mentioned above, normally singleton client accepts two parameters to determine which translations and patterns should be used in the current. The available regions can be got from singleton client API, user can pick up the correct one and store into the user profile.


```
getSupportedRegions( languageTag: string): Promise<languagesList>;

```

|  Parameter  |  Type  | Required | <div style="text-align:center">Description</div>           |
|:-----------:|:------:|:--------:|:-----------------------------------------------------------|
| languageTag | string | required | Define which language will be used to display region list. |


```
import { I18nService } from '@singleton-i18n/angular-client';
...
this.i18nService.getSupportedRegions('fr').then( regions => {
    this.regions = regions;
    // eg: assuming that language is fr
    // [ [ 'FR': 'France'],
    //   [ 'GE': 'Géorgie' ],
    //   [ 'CN': 'Chine' ] ]
});
...

```



#### **Set locale**


##### **Set default locale**


Set default locale and initialize current language and current region. Default value is en-US. It will be used as fallback locale. The content in the sourceBundle should correspond to the default locale. This function should be executed when the application start.


API

```
setDefaultLocale({
    languageCode: string,
    languageName: string,
    regionCode: string,
    regionName: string
})

```

Parameters

|  Parameter   |  Type  | <div style="text-align:center">Description</div>      |
|:------------:|:------:|:------------------------------------------------------|
| languageCode | string | The language code for translation and plural rule.    |
| languageName | string | The language name corresponding to the language code. |
|  regionCode  | string | The region code for the l2 formatting patterns.       |
|  regionName  | string | The region name corresponding to the region code.     |


##### **Set current locale**

Provides APIs to set the current locale/language/region used in the application.

API

```
setCurrentLocale(languageTag: string, regionCode?: string)

```
```
setCurrentLanguage(languageTag: string)

```
```
setCurrentRegion(regionCode: string)

```

Parameters

|  Parameter  |  Type  | <div style="text-align:center">Description</div>   |
|:-----------:|:------:|:---------------------------------------------------|
| languageTag | string | The language code for translation and plural rule. |
| regionCode  | string | The region code for the l2 formatting patterns.    |


#### **Get current locale**

There are two ways to get current locale, the first one is invoking the synchronous API to get locale directly that requires the all i18n data has to be loaded before application start using the 'Blocking loading' and relying on browser refresh to reload the data if either of language or region is changed. The specific setting can be referred to 'Blocking loading' in Date initialization session. The other way is more flexible and can be adapted in all situations including non-blocking loading and lazy loading, just through subscribing the locale stream to get current locale asynchronously.


##### **Synchronous API**

Get the current locale language or region from LocaleService synchronously.

API

```
getCurrentLocale(): string

```
```
getCurrentLanguage(): string

```
```
getCurrentRegion(): string

```

```
this.currentLocale = this.localeService.getCurrentLocale();

```


##### **Asynchronous API**

The stream is an observable object can be subscribed, the current locale will be emitted only when the resource for the selected locale is loaded.
In the live update mode, the translation method in L10nService or the formatting method in I18nService should be invoked as a callback function which reacts to the current locale arriving via the stream.

API

```
get stream(): Observable<string|any>

```

```
this.i18nService.stream.subscribe( locale => {
    this.formattedDate = this.i18nService.formatDate(new Date(), 'short', locale);
});

```


#### **Live Update**

Automatically refresh the UI when the language and region are changed. The translations and formatting strings will be updated on UI when the i18n data is loaded.

```
import { LocaleService } from '@singleton-i18n/angular-client';
...
// use language from browser as locale
this.localeService.setCurrentLocale(locale);
...

// use language and region from UI
this.localeService.setCurrentLanguage(language);
this.localeService.setCurrentRegion(region);
...

```


#### **Static Refresh**

Besides live update, the other choice is using browser refresh to reload the resource according to the new language and region which can be stored in LocalStorage.


```
changeLocale() {
    ...
    localStorage.setItem('language', language);
    localStorage.setItem('region', region);
    window.location.reload();
}

```

```
// app initial
...
const language = localStorage.getItem('language'),
    region = localStorage.getItem('region');
localeService.init(language, region);
vipService.initData(I18nConfig);
...

```







<style>  
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
    }
    section p>strong {
        font-weight: 600;
    }
    article section.page pre {
        background-color: #444;
        border: 0.5px solid #DBDBDB; 
        padding: 1.5rem 1rem 1.5rem 1rem;
        border-radius: 5px;
        margin: 16px auto;
    }
    article section.page code {
        font-size: 90%;
        color: #17ff0b;  
        white-space: pre-wrap;
    }
    article section.page pre span.copy-to-clipboard {
        color: #b0bec5;
        cursor: pointer;
    }
    article section.page table th {
        font-weight:500;
        text-transform: inherit;
    }
    table thead tr th:first-child {
        width:13rem;
    }
    table thead tr th:nth-child(2) {
        width:10rem;
    }
    table thead tr th:nth-child(3) {
        width:10rem;
    }
    article section.page h1:first-of-type {
        text-transform: inherit;
        font-family: inherit;
    }
</style>