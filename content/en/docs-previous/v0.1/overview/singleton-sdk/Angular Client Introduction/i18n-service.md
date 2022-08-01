---
title: "I18n Service"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 23
---


#### **Overview**

I18n service provides APIs which are supposed to be used in JavaScript module to get supported languages & regions, and format locale-sensitive data.

```
import { I18nService } from "@singleton-i18n/angular-client";

```

#### **Supported Language & Region & City APIs**

##### **getSupportedLanguages**

Get the supported languages which are available in singleton service for the specific product.

```
getSupportedLanguages(): Promise<languagesList>;

```

##### **getSupportedRegions**

Get the supported regions from singleton service, the supported region list should be unified in all products.

```
getSupportedRegions(languageTag: string): Promise<regionsList>;

```

##### **getCities**

Get the cities list of the specified region and display the cities' names in a specific language.

>Note that the current city list is only available in English, and there is no translation in other languages.

```
getCities(regionCode: string, languageTag: string): Promise<citiesList>;

```

|  Parameter  |  Type  | Required | <div style="text-align:center">Description</div>                                |
|:-----------:|:------:|----------|:--------------------------------------------------------------------------------|
| languageTag | String | Required | Get supported region list with the localized display name by the 'languageTag'. |
| regionCode  | string | Required | Get the cities list of corresponding region.                                    |


##### **Example Code**

```
this.i18nService.getSupportedLanguages().then( languages => {
    this.languages = languages;
    // eg: [ { displayName: "English", languageTag: "en" },
    //       { displayName: "français", languageTag: "fr" },
    //       { displayName: "简体中文", languageTag: "zh-Hans" }
    //      ]
});
this.i18nService.getSupportedRegions('fr').then( regions => {
        this.regions = regions;
        // eg: assuming that languageTag is fr
        // [ [ 'FR': 'France'],
        //   [ 'GE': 'Géorgie' ],
        //   [ 'CN': 'Chine' ] ]
});

```


#### **Data Formatting APIs**

Data formatting APIs are locale-sensitive; their return depends on the locale. So when using these formatting APIs in the JavaScript module, locale pattern data is required, which can be loaded synchronously or asynchronously depending on the way of i18n data initialization. If the i18n data was loaded in synchronous, these APIs could be used to format data directly. Otherwise, the stream API is the right way to get locale through the subscription and consume these APIs in an observer. For more details, please refer to the session 'Data initialization' and sample code below.

##### **Format date**

Formats a date according to the given options and locale.


```
formatDate(value: any, pattern: string, locale?: string, timezone?: string): string;

```

| Parameter |  Type  | Required | <div style="text-align:center">Description</div>                                                                                                                                                                                                                                                              |
|:---------:|:------:|----------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|   value   |  any   | Required | a date object or a number (milliseconds since UTC epoch) or an ISO string (https://www.w3.org/TR/NOTE-datetime).                                                                                                                                                                                              |
|  pattern  | string | Required | indicates which date/time components to include. The format can be predefined as shown [here](../date-time-format-pipe). Default is 'mediumDate'.                                                                                                                                                             |
|  locale   | string | No       | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](../locale-management#asynchronous-api) usage.                                                                                                                                                            |
| timezone  | string | No       | to be used for formatting. It understands UTC/GMT and the continental US time zone abbreviations, but for general use, use a time zone offset, for example, '+0430' (4 hours, 30 minutes east of the Greenwich meridian) If not specified, the local system time zone of the end-user's browser will be used. |


##### **Format number**

Formats a number according to the given options and locale.

```
formatNumber(value: any, locale?: string, formatOptions?: NumberFormatOptions): string;

```

|   Parameter   |     Type      | Required | <div style="text-align:center">Description</div>                                                                                                   |
|:-------------:|:-------------:|----------|:---------------------------------------------------------------------------------------------------------------------------------------------------|
|     value     | number/string | Required | The number to be formatted.                                                                                                                        |
|    locale     |    string     | No       | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](../locale-management#asynchronous-api) usage. |
| formatOptions |    object     | No       | The results formats can be customized using the formatOptions argument. See formatOptions for further details.                                     |


##### **Format percent**

Formats a number into percentage string according to the given options and locale.


```
formatPercent(value: any, locale?: string, formatOptions?: NumberFormatOptions): string;

```

|   Parameter   |     Type      | Required | <div style="text-align:center">Description</div>                                                                                                                         |
|:-------------:|:-------------:|----------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|     value     | number/string | Required | The number to be formatted.                                                                                                                                              |
|    locale     |    string     | No       | The locale is only required in async mode for non-blocking loading. Please refer to the stream API usage.                                                                |
| formatOptions |    object     | No       | The results formats can be customized using the formatOptions argument. Formatting percentage not support compact number formats. See formatOptions for further details. |

##### **Format currency**

Formats a number into currency string according to the given options and locale.


```
formatCurrency(value: any, currencyCode?: string, locale?: string, formatOptions?: NumberFormatOptions): string;

```

|   Parameter   |     Type      | Required | <div style="text-align:center">Description</div>                                                                                                                                            |
|:-------------:|:-------------:|----------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|     value     | number/string | Required | The number to be formatted.                                                                                                                                                                 |
| currencyCode  |    string     | No       | Currency code should be in accordance with [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217) standard, such as USD for the US dollar and EUR for the euro. Optional. Default value is USD. |
|    locale     |    string     | No       | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](../locale-management#asynchronous-api) usage.                                          |
| formatOptions |    object     | No       | The results formats can be customized using the formatOptions argument. See formatOptions for further details.                                                                              |


formatOptions

|     Attribute     |  Type  | Required | <div style="text-align:center">Description</div>                                                                                                                                                                                                                                         |
|:-----------------:|:------:|----------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| minIntegerDigits  | number | Optional | The minimum digits of integer numbers used. The possible values are from 1 to 21, and the default value is 1.                                                                                                                                                                            |
| minFractionDigits | number | Optional | The minimum digits of fraction numbers used. The possible values are from 0 to 20, and the default value is 0. In currency format: the default value comes from ISO 4217 currency code list (if the list is not provided, the default value is 2).                                       |
| maxFractionDigits | number | Optional | The maximum digits of fraction numbers used. The possible values are from 0 to 20, and the default value is 3. In currency format: the default value is taken the larger of minimumfractiondigits and ISO 4217 currency code list (if the list is not provided, the default value is 2). |
|     notation      | string | Optional | The format in which this number should be displayed. For now only support 'compact' for compact number formats. The default is "standard". Not supported in percent format.                                                                                                              |
|  compactDisplay   | string | Optional | The 'compactDisplay' is only used when notation is "compact". The possible value is "short" (default) or "long", and the default value is "short".                                                                                                                                       |


##### **Format relative time**

Formats simple relative dates. [Details](../relative-time-format)

```
formatRelativeTime( from: Date, to: Date, locale?: string, options?: Object ): string

```

##### **Get localized Pattern**

Get a localized version of the predefined patterns. If the input pattern is not the predefined pattern, return the input pattern directly. If the locale data is missing for the current locale, fallback to source locale and return source pattern instead.


```
getLocalizedPattern(pattern: string, locale?: string): string;

```

| Parameter |  Type  | Required | <div style="text-align:center">Description</div>                                                                                                          |
|:---------:|:------:|----------|:----------------------------------------------------------------------------------------------------------------------------------------------------------|
|  pattern  | string | Required | The pattern string needs to be localized. Currently, this API only supports predefined patterns here.                                                     |
|  locale   | string | No       | The default value is the current locale in use. The locale is only required in async mode for non-blocking loading. Please refer to the stream API usage. |


##### **Stream API**

Since the live update is relying on the observable object to notice the available current locale, formatting data based on the i18n service should be performed in the subscription of the 'stream' to ensure the pattern data is ready for this locale.

```
public stream(): Observable<string | any>;

```

##### **Example Code**

```
import { I18nService } from '@singleton-i18n/angular-client';
import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
    selector: 'test',
    templateUrl: './test.component.html'
})
export class TestComponent implements OnInit, OnDestroy {
    subscription: any;
    time: string;
    currency: string;
    constructor(private i18nService: I18nService) {}

    ngOnInit() {
        this.subscription = this.i18nService.stream.subscribe((locale: string) => {
            this.time = this.i18nService.formatDate(new Date(), 'short', locale );
            this.currency = this.i18nService.formatCurrency(10, 'CNY', locale);

            // By default, the locale is the current locale, eg: 'zh-Hans'
            // output: y/M/d ah:mm
            const localizedPattern = this.i18nService.getLocalizedPattern('short');
            
            // Explicitly set locale parameter, eg: 'en-US'
            // output: M/d/yy, h:mm a
            const EnPattern = this.i18nService.getLocalizedPattern('short', 'en-US');

            // format options usage
            let formatOptions = { notation: 'compact', compactDisplay: 'long' };
            const compactNumber = this.i18nService.formatNumber( 1000000, locale, formatOptions);
            // output: 1 million

            formatOptions = { maxFractionDigits: 6 };
            const roundedNumber = this.i18nService.formatNumber( 1.23456789, locale, formatOptions);
            // output: 1.234568
        });
    }
    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.subscription = undefined;
    }
}

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