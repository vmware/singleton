---
title: "Locale Management"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

#### **Overview**

A locale consists of a number of categories for which country-dependent formatting or other specifications exist. A program's locale defines its code sets, date and time formatting conventions, monetary conventions, decimal formatting conventions, and collation (sort) order. A locale name can be composed of a base language, country (territory) of use, and codeset.


For example, German language is de, an abbreviation for Deutsch, while Swiss German is de_CH, CH being an abbreviation for Confederation Helvetica. This convention allows for specific differences by country, such as currency unit notation. The locale naming convention is:**language[_territory][.codeset][@modifier]**


VIP client uses language to determine the translations that need to be displayed in the user interface, as well as the rules for singular plural processing and relative time display. The combination of region and language determines the format of the date time, number, currency, and other string display.


#### **Get available languages & regions**

If using a single language from the browser, this language will be treated as a locale for both L2 and L3 support.


##### **Language**

Provides two ways to get the supported languages: user browser language setting and the language from the user preference. There is one API to get the supported language of the current product, user can pick up one language as a preferred language, then stores into the backend DB. The recommended way is collecting both language and region information from the user as a user profile, this will work well with some complex situations.


Get user language from browser:

```
getBrowserCultureLang(): string

```

![locale-management-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-1.png)


Get supported languages list from VIP:

```
getSupportedLanguages(): Promise<languagesList>;

```

![locale-management-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-2.png)


##### **Region**

As mentioned above, normally VIP client accepts two parameters to determine which translations and patterns should be used in the current. The available regions can be got from VIP client API, user can pick up the correct one and store into the user profile.


```
getSupportedRegions( languageTag: string): Promise<languagesList>;

```

|  Parameter  |  Type  | Required |                        Description                         |
| :---------: | :----: | :------: | :--------------------------------------------------------: |
| languageTag | string | required | Define which language will be used to display region list. |


![locale-management-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-3.png)



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

|  Parameter   |  Type  |                      Description                      |
| :----------: | :----: | :---------------------------------------------------: |
| languageCode | string |  The language code for translation and plural rule.   |
| languageName | string | The language name corresponding to the language code. |
|  regionCode  | string |    The region code for the l2 formatting patterns.    |
|  regionName  | string |   The region name corresponding to the region code.   |


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

|  Parameter  |  Type  |                    Description                     |
| :---------: | :----: | :------------------------------------------------: |
| languageTag | string | The language code for translation and plural rule. |
| regionCode  | string |  The region code for the l2 formatting patterns.   |


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

![locale-management-4](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-4.png)


##### **Asynchronous API**

The stream is an observable object can be subscribed, the current locale will be emitted only when the resource for the selected locale is loaded.
In the live update mode, the translation method in L10nService or the formatting method in I18nService should be invoked as a callback function which reacts to the current locale arriving via the stream.

API

```
get stream(): Observable<string|any>

```

![locale-management-5](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-5.png)


#### **Live Update**

Automatically refresh the UI when the language and region are changed. The translations and formatting strings will be updated on UI when the i18n data is loaded.


![locale-management-6](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-6.png)


#### **Static Refresh**

Besides live update, the other choice is using browser refresh to reload the resource according to the new language and region which can be stored in LocalStorage.


![locale-management-7](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-7.png)

![locale-management-8](https://github.com/zmengjiao/singleton/raw/website/content/en/images/locale-management/locale-management-8.png)







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
        background-color: #fafafa;
        border:1px solid #ccc;
        padding-top: 2rem;
    }
    article section.page table th {
        font-weight:500;
        text-transform: inherit;
    }
</style>