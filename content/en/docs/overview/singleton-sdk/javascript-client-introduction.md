---
title: "JavaScript Client Introduction"
date: 2019-09-24T20:08:23+08:00
draft: false
weight: 20
---

#### **Overview**

The Singleton JSClient(@singleton-i18n/js-core-sdk) is a Singleton Service-based JavaScript library used for l10n and i18n. It almost supports all JavaScript frameworks, and it can run in browsers, as well as NodeJS environments. With the Singleton JavaScript Client, it makes l10n and i18n implementation more easier in most projects. Now, let's look at what it is and how it works.

#### **Features** 
- JSClient Initilization
- Language and region management
- Load data API
- Provide string localization API
- Provide datetime,number,currency,percentage formatting API
- Source management via CLI scripts
- Runtime / Offline Mode

#### **JSClient Initilization**

How to initilize Singleton JSClient in frontend framework? it requires to figure out the root loading point where JSClient is able to comminicate with Singele Service fetching translations and patterns. E.g. index.js in React framework is the root loading point, during the initilizing process, it needs to provice basic product information (E.g. ProductID, Component, Version) and interacts with Singleton Service runtime, it will load corresponding translations and patterns by language and region when switching locale. Singleton JSClient offers API ***i18nClient.init(Configuration)*** as below snippet which is used to initilize JSClient when App startup. At the same time, Singleton JSClient also provides l10nService and i18nService APIs so that it is able to use their exposed methods to translate strings and format the data regarding datetime, number, percentage and currency. 

Configuration
|  Parameter  |  Type  | Required | <div style="text-align:center">Description</div>                                |
|:-----------:|:----------------:|:----------:|:---------------------------------------------------------------------------------------------------------|
| productID | String | Required | Product name. |
| version | string | Required | Translation version. |
| component | string | Optional | From Singleton service perspective, it typically has backend component,frontend component.And default component is 'default'. |
| host | string | Required | Singleton service with which Singleton JSClient commnicaites to fetch translations and patterns by language and region. |
| language | string | Optional | Language determines which languge translations should be loaded. |
| region | string | Optional | Region determines which region patterns should be loaded.  |
| i18nScope | PatternCategories[] | Optional | i18nScope determines what kinds of patterns data should be loaded, such as Number, Datetime, Percentage, Currency. |
| sourceBundle | { [key: string]: any } | Optional | Mount source strings with key:value pair. |
| sourceBundles | Array<{ [key: string]: any }> | Optional | Mount source strings with key:value Objects.  |
| i18nAssets | string | Optional | This parameter is used to load translations and patterns from specified folder with Offline Mode, once setting this parameter, [host] parameter will disable without having to communicate with Singleton Service. |
| httpOptions | HttpRequestOptions | Optional | This paramter is used to set network request parameters, E.g  timeout，withCredentials. |
| isPseudo | boolean | Optional | isPseudo is used to development debugging and QE testing. |

```
import { i18nClient as jsClient, getBrowserCultureLang, PatternCategories} from '@singleton-i18n/js-core-sdk';

const currentLanguage  = getBrowserCultureLang();

const initI18nClient = () => {

    const i18nClient = jsClient.init({
        productID: 'ProductName',
        version: '1.0.0',
        component: 'ReactUI',
        host: 'http://Single-Service:8091',
        language: currentLanguage,
        i18nScope: [PatternCategories.DATE],
        sourceBundle : {
                'app.title': 'Hello, world!',
                'plural.apples': '{0, one{ # apple}  other{ # apples} }'
            },
        timeout: 5000
    });

    return i18nClient;
}

export const i18nClient = initI18nClient();

```

#### **Language and region management**

Singleton JSClient provides two approaches to resolve Language and Region, the first one is directly detecting current user language preference in the browser settings, The other one is strongly recommended to be adopted in both SaaS and on-premise deployment mode through the language and region list provided by Singleton JSClient API fetching from Singleton service.

##### **Resolve Browser Locale**

Resolve browser locale through the API ***getBrowserCultureLang*** as below snippet.

```
import { getBrowserCultureLang } from '@singleton-i18n/js-core-sdk';

const currentLanguage  = getBrowserCultureLang();

```

##### **User Preference Of Language List**

Singleton JSClient offers the API ***getSupportedLanguages(): Promise<Object[] | null>*** through i18nService to fetch the supported languages of App from Singleton service runtime.

```
import { i18nClient } from '@singleton-i18n/js-core-sdk';

i18nClient.i18nService.getSupportedLanguages()
    .then(
        ...
    );

```

#### **Load Data API**

Singleton JSClient offers the load data API through coreService in order to load latest translations and patterns when switching locale(language and region). there are certain situations as below.
- When App startup, it needs to load translations and patterns according to user perference language and region.
- When user switching language list, it should load corresponding translations and patterns

```
/**
* Load resource prior to perform callback.
* @param callback
*/
async loadI18nData(callback?: () => void): Promise<any>


// Example in React index.jsx
import { i18nClient } from '@singleton-i18n/js-core-sdk';

const render = async () => {
    try {
        await i18nClient.coreService.loadI18nData();
    } catch (e) {
        console.error(e);
    } finally {
        ReactDOM.render();
    }
};

render();

```

#### **Localization API**

Singleton JSClient also offers localization API ***getMessage(key: string, args?: any[] | {}): string*** through l10nService which is mainly used to translate strings along with varibles involved as below snippet.

| Parameter |    Type     | Required | <div style="text-align:center">Description</div>                                                                                         |
|:--------:|:--------:|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------|
|    key    |   string    | Required | Singleton JSClient bases on the key to figure out the translation of its value on Non-English locale.                          |
|    args   | string[]/{} |    No    | When variables involved in one string, it typically replace placeholders in string using these variables, and placeholder is something like {0} {1} ...  | 

```
{
    'product.login.success': '{0} has logged in successfully!',
}

import { i18nClient } from '@singleton-i18n/js-core-sdk';

const username = 'Tom';

i18nClient.l10nService.getMessage('product.login.success', ['product.login.success', [username]);

```

#### **Internationalization API**

Singleton JSClient also provides I18n APIs through i18nService to format the data regarding Datetime, Number, Percentage and Currency.

##### **Datetime Format API**

```
public formatDate(value: any, pattern: string = 'mediumDate', timezone?: string): any

```

| Parameter |    Type     | Required | <div style="text-align:center">Description</div>                                                                                     |
|:--------:|:--------:|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------|
|value|any|Required| A date object or a number (milliseconds since UTC epoch) or an ISO string (https://www.w3.org/TR/NOTE-datetime). |
|pattern|string|Required| The format can be predefined as shown below (all examples are given for en-US) or custom as shown in the table. Default is 'mediumDate'. |
|timezone|string|No| It is used for formatting. It understands UTC/GMT and the continental US time zone abbreviations, but for general use, use a time zone offset, for example, '+0430' (4 hours, 30 minutes east of the Greenwich meridian) If not specified, the local system timezone of the end-user's browser will be used. |


Pre-defined format options
|   Format  |   Unit    | Output |
|:------|:------|:------------------------------------------|
|'shortTime'|'h:mm a'| e.g. 5:40 PM |
|'mediumTime'|'h:mm:ss a'| e.g. 5:40:22 PM |
|'longTime'|'h:mm:ss z'| e.g. 5:40:22 PM GMT+8 |
|'fullTime'|'h:mm:ss zzzz'| e.g. 5:40:22 PM GMT+08:00 |
|'shortDate'|'M/d/yy'| e.g. 2/9/18 |
|'mediumDate'|'MMM d, y'| e.g. Feb 9, 2018 |
|'longDate'|'MMMM d, y'| e.g. February 9, 2018 |
|'fullDate'|'EEEE, MMMM d, y'| e.g. Friday, February 9, 2018 |
|'short'|'M/d/yy, h:mm a'| e.g. 2/9/18, 5:40 PM |
|'medium'|'MMM d, y, h:mm:ss a'| e.g. Feb 9, 2018, 5:40:22 PM |
|'long'|'MMMM d, y, h:mm:ss a z'| e.g. February 9, 2018 at 5:40:22 PM GMT+8 |
|'full'|'EEEE, MMMM d, y, h:mm:ss a zzzz'| e.g. Friday, February 9, 2018 at 5:40:22 PM GMT+08:00 |

##### **Number Format API**

```
public formatNumber(value: any, locale?: string): string

```

| Parameter |    Type     | Required | <div style="text-align:center">Description</div>                                                                                     |
|:--------:|:--------:|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------|
|value|any|Required| A number or a string to be formatted |
|locale|string|No| The method will format number data according to specified locale, if without specified locale, it will resolve the default locale from i18nClient.  |

##### **Percentage Format API**

```
public formatPercent(value: any): string

```

| Parameter |    Type     | Required | <div style="text-align:center">Description</div>                                                                                     |
|:--------:|:--------:|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------|
|value     |  any     | Required| The value to be formatted |

##### **Currency Format API**

```
public formatCurrency(value: any, currencyCode?: string): any 

```

| Parameter |    Type     | Required | <div style="text-align:center">Description</div>                                                                                     |
|:--------:|:--------:|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------|
|value     |  any     | Required| The value to be formatted |
|currencyCode     |  string     | No| Currency code should be in accordance with [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217) standard, such as USD for the US dollar and EUR for the euro. Optional. Default value is USD. |


#### **Source Management via CLI Scripts**

Singleton JSClient similarly exposes two CLI scripts which are seperately used to upload source bundle onto Singleton service for translations and to download translations & patterns from Singleton service by locale.

##### **Upload Source Bundle**

This script typically collects the collection named **ENGLISH** from the files named ***.l10n.js*** or ***.l10n.ts***, and then send them to the Singleton service.

- Command line arguments description

|    Parameter    |       Type       |  Required  | <div style="text-align:center">Description</div>                                                                                                              |
|:---------------:|:----------------:|:--------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------|
|  --source-dir   |      string      | required | The root directory where App source files are involved.                                                                                                       |
|     --host      |      string      | required | This is singleton service which provides clients with translations and pattern.                                                                               |
|    --product    |      string      | required | Product name. For now, singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
|   --component   |      string      | required | Component name.                                                                                                                                               |
|    --version    |      string      | required | Release version.                                                                                                                                              |
| --refresh-token |      string      | No | Refresh token is only required for the CSP environment.                                                                                                       |
|    --verbose    |      -           | No | If set, will show all information during command execution for debug purpose.                                                                                 |
|    --moduletype |      string      | No | Sometimes source files have ES6 statement involved,  due script itself is based on commonJS, so it requires to convert ES6 syntax to commonJS one.          |

- Create source bundle
```
export const ENGLISH = {
    "network-error": 'Network instability.'',
    "data-error": 'Data error.',
    ...
}

```

- Configure script in package.json

```
collect-source-bundle
            --source-dir `pwd`/src/app
            --host https://singleton.service.com:8090
            --product Testing
            --component ReactUI
            --version 1.0
    
```


```
{
    ...
    scripts: {   
        "upload-source-bundle": "collect-source-bundle 
                                --source-dir `pwd`/src/app 
                                --host <Singleton Service host>
                                --product <product>
                                --component <component>
                                --version <product version>
    }
    ...
}
    
```
        
- Run the script

```
npm run upload-source-bundle

```

##### **Download Translations & Patterns**

Download translations and Patterns by language and region when ***Offline Mode***.

- Command line arguments description

|  Parameter  |    Type    |  Value   | <div style="text-align:center">Description</div>                                                                                                              |
|:-----------:|:----------:|:--------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| --directory |   string   | required | The directory should be consistent with the parameter 'i18nAssets' at [Configuration], which is used to store translations and patterns on local.             |
|   --host    |   string   | required | This is singleton service which clients are able to communicate with and fetch translations and patterns.                                                     |
|  --product  |   string   | required | Product name. For now, singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master. |
| --component |   string   | required | Component name.                                                                                                                                               |
|  --version  |   string   | required | Release version.                                                                                                                                              |
| --languages |   string   | required | Specified the languages your product supports; Separated by , for example, zh-cn,en-US.                                                                       |
|  --verbose  |   -        | optional | If set, it will show all information during command execution for debug purpose.                                                                              |

```
load-locale-data
  --directory `pwd`/src/app/assets
  --host https://singleton.service.com:8090
  --product Testing
  --component default
  --version 1.0
  --languages zh-cn,en-US

```

- Configure script in package.json

```
{
    ...
    scripts: {   
        "download-locale-data": "load-locale-data 
                            --directory `pwd`/src/app/assets
                            --host <Singleton Service host>
                            --product <product>
                            --component <component>
                            --version <product version>
    }
    ...
}

```

Run the script

```
npm run download-locale-data

```

#### **Runtime / Offline Mode**

##### **Runtime Mode**

Runtime mode means JSClient has to communicate with Singleton Service runtime fetching translations and patterns, it requires to deploy Singleon Service on backend.

##### **Offline Mode**

Offline mode doesn't require to deploy Singleton Service on backend, it will be enabled no longer communicating with Singleton Service when setting 'i18nAssets' parameter at [Configuration], but it needs to depend on CLI script 'download-locale-data' exposed by Singleton JSClient to asynchronously fetch translations and patterns to local specified directory, then Singleton JSClient is able to load translations and patterns from local.

Link to the sample project: https://github.com/vmware/singleton/tree/g11n-js-client

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
    ul li {
        list-style: circle;
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