---
title: "I18n Service"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---


#### **Overview**

I18n service provides APIs which are supposed to be used in JavaScript module to get supported languages & regions, and format locale-sensitive data.

![i18n-service-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/i18n-service/i18n-service-1.png)


#### **Supported Language & Region APIs**

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

|  Parameter  |  Type  |                         Description                          |
| :---------: | :----: | :----------------------------------------------------------: |
| languageTag | String | Get supported region list with the localized display name by the 'languageTag'. |


##### **Example Code**

![i18n-service-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/i18n-service/i18n-service-2.png)



#### **Data Formatting APIs**

Data formatting APIs are locale-sensitive, they return depends on the locale. So when using these formatting APIs in JavaScript module, locale is a required parameter which can be fetched through the locale service by synchronous or asynchronous API that depends on the way of i18n data initialization. if the i18n data was loaded in synchronous by 'APP_INITIALIZER' token, the synchronous API can be used to get locale directly. Otherwise, the stream API is a right way to get locale through the subscription. For more details, please refer to the sessions 'Data initialization' and 'Locale Management'.


##### **Format date**

Formats a date according to the given options and locale.


```
formatDate(value: any, pattern: string, locale?: string, timezone?: string): string;

```

| Parameter |  Type  |                         Description                          |
| :-------: | :----: | :----------------------------------------------------------: |
|   value   |  any   | a date object or a number (milliseconds since UTC epoch) or an ISO string (https://www.w3.org/TR/NOTE-datetime). |
|  pattern  | string | indicates which date/time components to include. The format can be predefined as shown [here](../date-time-format-pipe). Default is 'mediumDate'. |
|  locale   | string | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](../locale-management#asynchronous-api) usage. |
| timezone  | string | to be used for formatting. It understands UTC/GMT and the continental US time zone abbreviations, but for general use, use a time zone offset, for example, '+0430' (4 hours, 30 minutes east of the Greenwich meridian) If not specified, the local system time zone of the end-user's browser will be used. |


##### **Format number**

Formats a number according to the given options and locale.

```
formatNumber(value: any, locale?: string): string;

```

| Parameter |     Type      |                         Description                          |
| :-------: | :-----------: | :----------------------------------------------------------: |
|   value   | number/string |                 The number to be formatted.                  |
|  locale   |    string     | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](../locale-management#asynchronous-api) usage. |



##### **Format currency**

Formats a number into currency string according to the given options and locale.


```
formatCurrency(value: any, currencyCode?: string, locale?: string): string;

```

|  Parameter   |     Type      |                         Description                          |
| :----------: | :-----------: | :----------------------------------------------------------: |
|    value     | number/string |                 The number to be formatted.                  |
| currencyCode |    string     | Currency code should be in accordance with [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217) standard, such as USD for the US dollar and EUR for the euro. Optional. Default value is USD. |
|    locale    |    string     | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](../locale-management#asynchronous-api) usage. |


##### **Format relative time**

Formats simple relative dates. [Details](../relative-time-format)

```
formatRelativeTime( from: Date, to: Date, locale?: string, options?: Object ): string

```

##### **Example Code**

![i18n-service-3](https://github.com/zmengjiao/singleton/raw/website/content/en/images/i18n-service/i18n-service-3.png)



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
    table thead tr th:first-child {
        width:13rem;
    }
    table thead tr th:nth-child(2) {
        width:10rem;
    }
</style>