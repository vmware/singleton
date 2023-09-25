---
title: "Singleton Service APIs"
date: 2023-09-25T14:27:55+08:00
draft: false
---

Singleton Service has two API versions available at present: v1 and v2. API v1 was created in the early times of Singleton, while API v2 has been validated with many VMware products over years, which includes the largest number of APIs, and is closer to the Restful style. It is recommended to use API v2 in practice. This version provides two types of APIs:



1. Formatting API, which is used to format the data like the date time/number/currency/unit of measure/singular and plural variations, just to name a few.
2. Translation API



Next, we'll talk about the usage of the main APIs under the two types.

# **Formatting API**

## *Date Time Formatting API (formatting-date-api)*

Convert the specified time stamp to a new format suitable for the language as requested, using the provided pattern.

#### **GET**/i18n/api/v2/formatting/date/localizedDate

**Parameters:**

locale: **(required)** (String), to indicate the language as requested; e.g. en, zh-CN etc.

longDate: **(required)** (String), to indicate the time stamp in long date format; e.g. 1472728030290

pattern: **(required)** (String), to indicate the pattern to be used; e.g. YEAR = "y", QUARTER = "QQQQ", ABBR_QUARTER ="QQQ", QUARTER_YEAR = "QQQQy" etc.

**Return value:**

(JSON Object) The formatted date time value.

**Example:**

**{Singleton service}**/i18n/api/v2/formatting/date/localizedDate?locale=zh--CN&longDate=1472728030290&pattern=QQQQ

Return value: "第三季度" (the third quarter or Q3 in Chinese)

## *Number Formatting API (formatting-number-api)*

Convert the specified number to a new format suitable for the language as requested, with the decimal place provided.

#### **GET**/i18n/api/v2/formatting/number/localizedNumber

**Parameters:**

locale: **(required)** (String), to indicate the language as requested; e.g. zh-CN etc.

number: **(required)** (String), to indicate the number to be converted; e.g. 123.45

scale: (optional) (Integer), to indicate the decimal place to use in the formatted number; e.g. 2. The default value is 0.

**Return value:**

(JSON Object) The formatted number.

**Example:**

**{Singleton service}**/i18n/api/v2/formatting/number/localizedNumber?locale=zh-CN&number=123.45&scale=1

Return value: "123.5"

## *Date/Number/Currency/Unit of Measure/Singular and Plural Variations Pattern API (formatting-pattern-api)*

With Singleton, you can get the pattern data in two ways: one is by the language and region settings (language [-region], such as en-US); the other is by the combined locale settings. The pattern data in Singleton is extracted from the CLDR (Unicode Common Locale Data Repository) version: 32.0.0.

#### **GET**/i18n/api/v2/formatting/patterns

Get the pattern data of the type as requested according to the language and region settings.

**Parameters:**

language: **(required)** (String), to indicate the language for the pattern data as requested, e.g. en, en-CA, zh-Hans, etc.

region: **(required)** (String), to indicate the region for the pattern data as requested; e.g. US, CN etc.

scope: **(required)** (String), to indicate the type of the pattern data as requested. Only one or more of "dates, numbers, currencies, plurals, measurements" is allowed; Separate the multiple items by comma (,).

**Return value:**

(JSON Object) The pattern data of the type as requested.

**Example:**

**{Singleton service}**/i18n/api/v2/formatting/patterns?language=zh-Hans&region=CN&scope=numbers

Return value:

"numbers": {

​       "numberSymbols": {

​         "decimal": ".",

​         "group": ",",

​         "list": ";",

​         "percentSign": "%",

​         "plusSign": "+",

​         "minusSign": "-",

​         "exponential": "E",

​         "superscriptingExponent": "×",

​         "perMille": "‰",

​         "infinity": "∞",

​         "nan": "NaN",

​         "timeSeparator": ":"

​       },

​       "numberFormats": {

​         "decimalFormats": "#,##0.###",

​         "percentFormats": "#,##0%",

​         "currencyFormats": "¤#,##0.00",

​         "scientificFormats": "#E0"

​        }

**Note:**

\1.    If the combination of the language and region as requested is available and valid (e.g. en-US, zh-Hans-CN), the pattern data returned shall be specific to that combination; In this case, the result of this API is almost the same as the one of the API shown below, except when "plurals" is included in scope;

\2.    If the combination of the requested language and region is invalid (e.g. en-DE), the region takes precedence to return the pattern data, which represents the mostly spoken language in the requested region, with the exception of "plurals" in scope, which is always language-based.

\3.    The "plurals" in scope is always language-based, regardless of whether the combination of the requested language and region is valid or not.

#### **GET**/i18n/api/v2/formatting/patterns/locales/{locale}

Get the pattern data of the type as requested according to the combined locale settings.

**Parameters:**

locale: **(required)** (String), to indicate the region for the pattern data as requested, e.g. en-CA, zh-Hans, zh-Hans-CN etc.

scope: **(required)** (String), to indicate the type of the pattern data as requested. Only one or more of "dates, numbers, currencies, plurals, measurements" is allowed; Separate the multiple items by comma (,).

**Return value:**

(JSON Object) The pattern data of the type as requested.

**Example:**

**{Singleton service}**/ i18n/api/v2/formatting/patterns/locales/zh-Hans-CN?scope=numbers

Return value:

"numbers": {

​       "numberSymbols": {

​         "decimal": ".",

​         "group": ",",

​         "list": ";",

​         "percentSign": "%",

​         "plusSign": "+",

​         "minusSign": "-",

​         "exponential": "E",

​         "superscriptingExponent": "×",

​         "perMille": "‰",

​         "infinity": "∞",

​         "nan": "NaN",

​         "timeSeparator": ":"

​       },

​       "numberFormats": {

​         "decimalFormats": "#,##0.###",

​         "percentFormats": "#,##0%",

​         "currencyFormats": "¤#,##0.00",

​         "scientificFormats": "#E0"

​        }

## *Region-Related API (locale-api)*

The Region-Related API are used to get the current locale settings in the browser, the languages supported by the product, and local translations in specific languages.

#### **GET**/i18n/api/v2/locale/browserLocale

#### **GET**/i18n/api/v2/locale/normalizedBrowserLocale

Get the locale settings in the browser.

**Parameters:**

None

**Return value:**

(JSON Object) The first value in the locale list of the current browser.

**Example:**

**{Singleton service}**/i18n/api/v2/locale/browserLocale

**{Singleton service}**/i18n/api/v2/locale/normalizedBrowserLocale

Return value:

"data": {

"displayName": "English (UnitedStates)",

"locale": "en-US"

}

#### **GET**/i18n/api/v2/locale/supportedLanguageList

Get the list of languages supported by the product.

**Parameters:**

productName:**(required)** (String), to indicate the product name registered in Singleton; e.g. Testing

version:**(required)** (String), to indicate the product version registered in Singleton; e.g. 1.0.0

displayLanguage: (optional) (String), to indicate the language used to show the returned values; e.g. en, zh-Hans; If it is left blank, the returned values shall be shown in its own language, e.g. en->English, zh-Hans->Chinese Simplified

**Return value:**

(JSON Object) The list of languages supported by the product in Singleton.

**Example:**

**{Singleton service}**/i18n/api/v2/locale/supportedLanguageList?productName=Testing&version=1.0.0

Return value:

"languages": [

​      {

​       "languageTag": "zh-Hans",

​       "displayName": "简体中文"

​      },

​      {

​       "languageTag": "en",

​       "displayName": "English"

​      },

​      {

​       "languageTag": "ja",

​       "displayName": "日本語"

​      },

​      {

​       "languageTag": "es",

​       "displayName": "español"

​      },

​      {

​       "languageTag": "fr",

​       "displayName": "français"

​      },

​      {

​       "languageTag": "ko",

​       "displayName": "한국어"

​      },

​      {

​       "languageTag": "de",

​       "displayName": "Deutsch"

​      }]

#### **GET**/i18n/api/v2/locale/regionList

Get the list of regions for the language as requested.

**Parameters:**

supportedLanguageList: **(required)** (String), to indicate the language as requested; e.g. en, zh-Hans, etc.

**Return value:**

(JSON Object) The default region of the language as requested, as well as the full list of regions for this language.

**Example:**

**{Singleton service}**/i18n/api/v2/locale/regionList?supportedLanguageList=zh-Hans

Return value:

{

​     "language": "zh-hans",

​     "defaultRegionCode": "CN",

​     "territories": {

​       "AC": "Ascension Island",

​       "AD": "Andorra",

​       "AE": "United Arab Emirates",

​       "AF": "Afghanistan",

​       "AG": "Antigua and Barbuda",

​       "AI": "Anguilla",

​        ……………………}}

# **Translation-Related API**

## *Product-Level API (translation-product-api)*

Get the component names defined in the whole product, supported languages in the product, and the translations in all those languages.

#### GET /i18n/api/v2/translation/products/{productName}/versions/{version}

Get the translations in all languages supported by the product.

**Parameters:**

productName:**(required)**, (String), to indicate the product name registered in Singleton; e.g. Testing

version:**(required)**, (String), to indicate the product version registered in Singleton; e.g. 1.0.0

pseudo: (String), True or False (Default: False); to return the real translations in Singleton, or pseudo translations

**Return value:**

(JSON Object) The translations in all languages supported by the product as requested.

**Example:**

**{Singleton service}**/i18n/api/v2/translation/products/Testing/versions/1.0.0?pseudo=false

#### **GET**/i18n/api/v2/translation/products/{productName}/versions/{version}/componentlist

It is recommended to define and store strings by product and component/function in Singleton. This API is used to get the list of all components/functions defined in the product.

**Parameters:**

productName:**(required)**, (String), to indicate the product name registered in Singleton; e.g. Testing

version:**(required)**, (String), to indicate the product version registered in Singleton; e.g. 1.0.0

**Return value:**

(JSON Object) The list of all components/functions defined in the product.

**Example:**

**{Singleton service}**/i18n/api/v2/translation/products/Testing/versions/1.0.0/componentlist

#### **GET**/i18n/api/v2/translation/products/{productName}/versions/{version}/localelist

Get the list of languages supported by the product.

**Parameters:**

productName:**(required)**, (String), to indicate the product name registered in Singleton; e.g. Testing

version:**(required)**, (String), to indicate the product version registered in Singleton; e.g. 1.0.0

**Return value:**

(JSON Object) The list of languages supported by the product.

**Example:**

**{Singleton service}***/i18n/api/v2/translation/products/Testing/versions/1.0.0/localelist*

## *Component/Function-Level API (translation-product-component-api)*

#### **GET**/i18n/api/v2/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}

Get the translations in the specified language for a component/function in the product.

**Parameters:**

productName:**(required)** (String), to indicate the product name registered in Singleton; e.g. Testing

component: **(required)** (String), to indicate the component/function name defined in Singleton; e.g. default

version:**(required)** (String), to indicate the product version registered in Singleton; e.g. 1.0.0

locale: **(required)** (String), to indicate the region for which you want to get translations; e.g. en, ja-JP, etc.

pseudo: (Default: False); Setting to True returns the pseudo translation in the language as requested.

machineTranslation: (Default: False); Setting to True returns the MT (Machine Translation) output.

checkTranslationStatus: (Default: False); Setting to True checks if the translation is ready now. The user can determine whether to use the current translation according to the result.

**Return value:**

(JSON Object) The translation in the language of the product for the component/function as requested.

**Example:**

**{Singleton service}***/*i18n/api/v2/translation/products/Testing/versions/1.0.0/locales/zh_CN/components/default?pseudo=false&machineTranslation=false&checkTranslationStatus=false

translation-sync-api

## *String-Level API (translation-product-component-key-api)*

Get the translation of a string.

#### **POST**/i18n/api/v2/translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys/{key}

Get the translation of a string.

**Parameters:**

productName:**(required)** (String), to indicate the product name registered in Singleton; e.g. Testing

version:**(required)** (String), to indicate the product version registered in Singleton; e.g. 1.0.0

locale: **(required)** (String), to indicate the region for which you want to get translations; e.g. en, ja-JP, etc.

component: **(required)** (String), to indicate the component/function name defined in Singleton; e.g. default

key: **(required)** (String), to indicate the unique identifier of the string in the component/function.

source: (optional) (String), to indicate the source language string. If it is left blank, the translation of this string in Singleton is returned; In case that the provided string value is not the same as the one stored in Singleton, the input string value is returned without the translation.

commentForSource: (optional) (String), to provide comments on the source language string

collectSource: (Default: False). Setting to True with the string collection turned on in Singleton, the requested string can be sent directly to Singleton.

pseudo: (Default: False); Setting to True returns the pseudo translation in the language as requested.

machineTranslation: (Default: False); Setting to True returns the MT (Machine Translation) output.

checkTranslationStatus: (Default: False); Setting to True checks if the translation is ready now. The user can determine whether to use the current translation according to the result.

**Return value:**

(JSON Object) The translation in the language of the product for the component/function as requested.

Example:

**{Singleton service}***/i18n/api/v2/translation/products/Testing/versions/1.0.0/locales/zh_CN/components/default/keys/testing.key?collectSource=false&pseudo=false&machineTranslation=false&checkTranslationStatus=false*

## *Adding/Updating Translation API (translation-sync-api)*

Add the translation in another language or update the translation in the current language.

#### PUT/i18n/api/v2/translation/products/{productName}/versions/{version}

Add the translation in another language; add translations for one or more new strings in the current language; update the existing translations of a string, a component/function, or the whole product in one or more languages.

**Parameters:**

productName:**(required)** (String), to indicate the product name registered in Singleton; e.g. Testing

version:**(required)** (String), to indicate the product version registered in Singleton; e.g. 1.0.0

translationData: **(required)** (JSON Object), to include the productName/version/component/locale defined in Singleton, as well as the keys and values of strings for which to add/update translations. Note: **The productName/version should be the same as the ones mentioned above.

**Return value:**

(JSON Object) The relevant response status code and status message.

Example:

**{Singleton service}***/i18n/api/v2/translation/products/Testing/versions/1.0.0*

# *Composite API (translation-with-pattern-api)*

#### **POST**/i18n/api/v2/combination/translationsAndPattern

This API, as the combination of the Formatting API and the Translation API, can be used to get the i18n-formatted data for the date time/number/currency/unit of measure/single and plural variations, as well as translations of a product.

**Parameters:**

data: **(required)** (JSON Object). Requires the following parameter values:

{

"combine": 0,

"components": [

"string"

],

"language": "string",

"productName": "string",

"pseudo": "string",

"region": "string",

"scope": "string",

"version": "string"

}

**Note**:

combine: (Integer). Only two values are allowed: 1 or 2

The value of 1 returns the formatted pattern data according to the provided language and region parameters.

The value of 2 returns the formatted pattern data according to the provided language parameter. In this case, the region parameter can be empty.

**Return value:**

(JSON Object) The formatted pattern data and the translations as requested.

# **Rules to Define Parameters in Singleton Service**

1. productName: Only the English letters and numbers are allowed
2. version: Only the English numbers and the period (.) are allowed
3. componentName: Only the English letters, numbers, period (.), strikethroughs, and underscore are allowed; It is recommended to define any meaningful names according to the product features or certain pages
4. locale: Only the English letters, numbers, underscores, and strikethroughs are allowed
5. key: Only the English letters, numbers, period (.), underscores, and strikethroughs are allowed; It is recommended to define any meaningful key values according to the product features
6. collectSource: "true" or "false"
7. pseudo: "true" or "false"
8. machineTranslation: "true" or "false"
9. checkTranslationStatus: "true" or "false"

# **API - Get Version list API**
This API is used to get a product name's all available versions

#### **GET**/i18n/api/v2/translation/products/{productName}/versionlist

**Parameters:**

productName: **(required)** (String), to indicate the product name registered in Singleton; e.g. Testing

**Return value:**

   **type:** (JSON Object), **description:** The request productName's all version list.

Example:
**{Singleton service}**/i18n/api/v2/translation/products/MULTCOMP/versionlist

```
{
 "response":{"code":200,"message":"OK","serverTime":""},
 "signature":"",
 "data":{"versions":["1.0.0"],"productName":"MULTCOMP"}
}
```

# **API - Get Multiple Versions Key translations**
The API is used to get multiple versions key's translations

#### **GET**/i18n/api/v2/translation/products/{productName}/multiVersionKey

**Parameters:**

productName: **(required)** **type (String)**, to indicate the product name registered in Singleton; e.g. Testing

versions: **(required)** **type (String)**, a string contains multiple translation version, separated by commas,(if versions string is ‘all’, It will get all version translation)

locale: **(required)** **type (String)**, locale string

component: **(required)** **type (String)**, component name

key: **(required)** **type (String)**, an id value to identify translation 

**Return value:**

**type:** (JSON Object), **description:** According to multiple versions key request parameter get translation list

Example:
Request all the versions have key translations

**{Singleton service}**/i18n/api/v2/translation/products/MULTCOMP/multiVersionKey?versions=1.0.0%2C2.0.0&locale=en&component=component1&key=key.test1

```
{
  "response": {
    "code": 200,
    "message": "OK",
    "serverTime": ""
  },
  "signature": "",
  "data": [
    {
      "productName": "MULTCOMP",
      "version": "1.0.0",
      "dataOrigin": "",
      "pseudo": false,
      "machineTranslation": false,
      "source": "",
      "translation": "this is a test11",
      "locale": "en",
      "key": "key.test1",
      "component": "component1",
      "status": "Source is not provided, the existing translation is found and returned"
    },
    {
      "productName": "MULTCOMP",
      "version": "2.0.0",
      "dataOrigin": "",
      "pseudo": false,
      "machineTranslation": false,
      "source": "",
      "translation": "this is a test11",
      "locale": "en",
      "key": "key.test1",
      "component": "component1",
      "status": "Source is not provided, the existing translation is found and returned"
    }
  ]
}
```
**{Singleton service}**/i18n/api/v2/translation/products/MULTCOMP/multiVersionKey?versions=all&locale=en&component=component1&key=key.test1

```
{
  "response": {
    "code": 200,
    "message": "OK",
    "serverTime": ""
  },
  "signature": "",
  "data": [
    {
      "productName": "MULTCOMP",
      "version": "1.0.0",
      "dataOrigin": "",
      "pseudo": false,
      "machineTranslation": false,
      "source": "",
      "translation": "this is a test11",
      "locale": "en",
      "key": "key.test1",
      "component": "component1",
      "status": "Source is not provided, the existing translation is found and returned"
    },
    {
      "productName": "MULTCOMP",
      "version": "2.0.0",
      "dataOrigin": "",
      "pseudo": false,
      "machineTranslation": false,
      "source": "",
      "translation": "this is a test11",
      "locale": "en",
      "key": "key.test1",
      "component": "component1",
      "status": "Source is not provided, the existing translation is found and returned"
    }
  ]
}
```
According to request parameters: productName, versions list, component, locale, key, the result only part of versions(in request version list) have key translations

**{Singleton service}**/i18n/api/v2/translation/products/MULTCOMP/multiVersionKey?versions=2.0.0%2C3.0.0&locale=en&component=component1&key=key.test1

```
{
  "response": {
    "code": 207,
    "message": "Part of the translation is available",
    "serverTime": ""
  },
  "signature": "",
  "data": [
    {
      "productName": "MULTCOMP",
      "version": "2.0.0",
      "dataOrigin": "",
      "pseudo": false,
      "machineTranslation": false,
      "source": "",
      "translation": "this is a test11",
      "locale": "en",
      "key": "key.test1",
      "component": "component1",
      "status": "Source is not provided, the existing translation is found and returned"
    }
  ]
}
```
