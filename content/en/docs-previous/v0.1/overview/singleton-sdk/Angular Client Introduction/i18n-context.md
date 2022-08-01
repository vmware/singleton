---
title: "I18n Context"
date: 2022-06-16T12:43:05+03:00
draft: false
weight: 24
---


#### **Overview**

I18n context is designed to manage the variables from different environments which carries pseudoEnabled, i18nEnabled, preferredLanguage, and other user-scoped values, provides a way to load and share these values between services without having to explicitly pass a parameter through every level of the service. In this way, users can use this interface to modify the default behavior regardless of the development or testing phase.
The storage of these variables is configurable, and the default is saved in localstorage.

```
import { I18nContext } from "@singleton-i18n/angular-client";

```

#### **Context Attributes**

|     Attribute     |  Type   | Writable | Default   | <div style="text-align:center">Description</div>                                                                                                                                                                    |
|:-----------------:|:-------:|----------|-----------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|   pseudoEnabled   | boolean | readonly | undefined | The pseudo setting from localStorage, determine whether to show pseudo translation in debugging or developing stage. The value can be changed by localStorage, it will override the pseudo setting in i18n configs. |
|    i18nEnabled    | boolean | writable | undefined | The i18n enable setting from localStorage, determine whether to use Singleton features in testing or product stage.                                                                                                 |
| preferredLanguage | string  | writable | undefined | The preferred language from localStorage.                                                                                                                                                                           |


##### **Default storage**

The default context storage is based on localstorage, which can be replaced by cookie, session storage, etc. Developers and testers can directly modify the corresponding attribute values in the localstorage to change the default behavior of the application to achieve the purpose of testing. However, this requires certain development work in the application itself. The specific development and usage scenarios will be explained in detail in the following sessions.

|          Key          | <div style="text-align:center">Description</div>                                                     |
|:---------------------:|:-----------------------------------------------------------------------------------------------------|
|   vip.pseudoEnabled   | The key in localStorage determines whether to enable pseudo. The value can be true or false.         |
|    vip.i18nEnabled    | The key in localStorage determines whether to enable i18n. The value can be true or false.           |
| vip.preferredLanguage | The key in the local storage to store the preferred language which is controlled by the application. |


#### **Usage Scenarios**

##### **I18n feature switch**

Under normal circumstances, international development is cross-release. Before the official release of the internationalization function, we need to disable the function of i18n at the code level, but in the testing and development stage, we need to debug and test i18n, so it is suitable for using context in this situation to override the default settings.

```
const i18nEnabled = false;
// Initialize the current state of i18n feature.
i18nContext.i18nEnabled = i18nContext.i18nEnabled || i18nEnabled;

```

##### **Pseudo translation**

To override the pseudo setting in the testing and development stage.

```
const pseudoEnabled = false;
i18nContext.pseudoEnabled = i18nContext.pseudoEnabled || pseudoEnabled;

```

##### **Preferred language**

It is mainly used to load the login page in the user's preferred language before loading the user preference.

```
const browserLanguage: string = getBrowserCultureLang();
const currentLanguage = i18nContext.preferredLanguage ||
    localeService.normalizeLanguageCode(browserLanguage);

```

```
import {
    getBrowserCultureLang,
    I18nContext,
    LocaleService,
    VIPService,
} from '@singleton-i18n/angular-client';

// Initialize the current state of i18n feature.
const i18nEnabled = false;
i18nContext.i18nEnabled = i18nContext.i18nEnabled || i18nEnabled;

const browserLanguage: string = getBrowserCultureLang();
const currentLanguage = i18nContext.preferredLanguage || localeService.normalizeLanguageCode(browserLanguage);

localeService.init(currentLanguage);
return () => vipService.initData(I18nConfig);

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
   blockquote {
        background: #f5dddb;
        border: 1px solid #f8b5b4;
        color: #575757;
    }    
    blockquote>p {
        display: inline-block;
        margin: 1rem 0;
    }
</style>
