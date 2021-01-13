---
title: "Java Client Introduction"
date: 2019-09-24T20:07:51+08:00
draft: false
weight: 10
---

#Singleton Java Client Library
To facilitate consumption of the Singleton Service API, internationalization(i18n) client libraries have been
developed in several programming languages. These libraries encapsulate API calls to the Singleton service, 
significantly improving development efficiency and reducing the cost of development work. 
This section gives a high-level overview of the Singleton Java Client Library.

## Features
- Translation - retrieving localized/translated text
- Formatting - retrieving formatted data using CLDR-compliant, localized patterns (e.g. date, time, number, currency) 
- Pluralization - retrieving singular/plural variations of data 
- Pseudo-translation - generating a fake(pseudo) translation from source text. This is often used for testing purposes.for testing purposes
- Client-side Localization(L10n) Cache - allows the client application to quickly serve L10n data 
without frequently communicating with the Singleton service.

## Localization Modes
- Online   
- Offline  

## Configuration
The following parameters make up the Singleton configuration of a client application:

- productName - (Required) The name of the product with resources that are localized in the Singleton service.
- version: (Required) Corresponds to the versioned set of localized resources of the specified product.  
- vipServer: (Optional) The HTTP url of the Singleton service's host (<http/s>://\<hostname>/\<ip>:\<port>>).
If this parameter is not present, it means that Online Mode is not supported.
- offlineResourcesBaseUrl: (Optional if viServer is present) The classpath of locally bundled localized resources.
If this parameter is present, then Offline Mode is supported. If the vipServer parameter is also present, 
then Offline Mode will only be used as fallback mechanism in case of a Singleton service call failure.
- initializeCache - (Default: false) If true, it initializes the client-side G11n cache at application start up.
- pseudo - (Default: false) If true, pseudo-translation is enabled which means that the get translation API endpoint will 
return a fake(pseudo) translation generated from the source text instead of returning the text in its localized form.
- machineTranslation - (Default: false) Returns the MT (Machine Translation) output or not. The default value is false. Turned off at the server side by default.
- *true: Returns the MT (Machine Translation) output, regardless of whether the existing translation is here.*
- *false: Returns the translation in a normal way.*

## API
### Translation

See Javadoc at https://vmware.github.io/singleton/docs/overview/singleton-sdk/java-client-introduction/javadoc

### Formatting

##### Date and Time

**Format the specified date and time according to the locale setting.**

Methods: `DateFormatting.formatDate`

Parameters

`obj`: (required), to indicate the date time object to be formatted. Supported types: Calendar, Date, Number, String.

`pattern`: (optional), to indicate the format type. Supports 12 types of formats at this time: full, long, medium, short, fullDate, longDate, mediumDate, shortDate, fullTime, longTime, mediumTime, shortTime. The default value is medium.

`locale`: (required), to indicate the target locale for formatting. Example: en-US, zh-Hans, etc.

Return value

The formatted string.

Example

```
DateFormatting df = (DateFormatting) I18nFactory.getInstance().getFormattingInstance(DateFormatting.class);df.formatDate(1511156364801l, "long", Locale.forLanguageTag("zh-Hans"));
返回:2017年11月20日 GMT+8 下午1:39:24
```

**Format the specified date and time according to the locale and time zone settings.**

Methods: `DateFormatting.formatDate`

Parameters

`obj`: (required), to indicate the date time object to be formatted.

`pattern`: (optional), to indicate the format type. The default value is medium.

`timeZone`: (optional) Specify the time zone. If no time zone is provided, the default time zone of Java VMs is used.

`locale`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
DateFormatting df = (DateFormatting) I18nFactory.getInstance().getFormattingInstance(DateFormatting.class);df.formatDate(1511156364801l, "long", "GMT-8", Locale.forLanguageTag("zh-Hans"));
返回:2017年11月19日 GMT-8 下午9:39:24
```

**Format the specified date and time according to the language and locale settings.**

Methods: `DateFormatting.formatDat`

Parameters

`obj`: (required), to indicate the date time object to be formatted.

`pattern`: (optional), to indicate the format type. The default value is medium.

`timeZone`: (optional) Specify the time zone. If no time zone is provided, the default time zone of VMs is used.

`language`: (required), to indicate the target language for formatting.

`region`: (required), to indicate the target locale for formatting. Example: us, cn, etc.

Return value

The formatted string.

**Note**

If the combination of the requested language and region is invalid (e.g. en-DE), the region takes precedence to return the pattern data, which represents the mostly spoken language in the requested region.

Example

```
DateFormatting df = (DateFormatting) I18nFactory.getInstance().getFormattingInstance(DateFormatting.class);df.formatDate(1511156364801l, "long", "GMT-8", "zh-Hans", "CN");
返回:2017年11月19日 GMT-8 下午9:39:24
```

##### Formatting the Number

**Format the specified number as another style according to the locale setting.**

Methods: `NumberFormatting.formatNumber`

Parameters

`value`: (required), to indicate the number to be formatted. Example: 123.45.

`locale`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatNumber(123.45, Locale.forLanguageTag("zh-Hans"));
返回:123.45
```

**Format the specified number as another style according to the locale setting, and the specified decimal place.**

Methods: `NumberFormatting.formatNumber`

Parameters

`value`: (required), to indicate the number to be formatted.

`fractionSize`: (optional), to indicate the decimal place. The default value depends on the language.

`locale`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatNumber(123.45, 1, Locale.forLanguageTag("zh-Hans"));
返回:123.4
```

**Format the specified number as another style according to the language and locale settings.**

Methods: `NumberFormatting.formatNumber`

Parameters

`value`: (required), to indicate the number to be formatted.

`language`: (required), to indicate the target language for formatting.

`region`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatNumber(123.45, "zh-Hans", "CN");
返回:123.45
```

**Format the specified number as another style according to the language and locale settings, as well as the specified decimal place.**

Methods: `NumberFormatting.formatNumber`

Parameters

`value`: (required), to indicate the number to be formatted.

`fractionSize`: (optional), to indicate the decimal place. The default value depends on the language.

`language`: (required), to indicate the target language for formatting.

`region`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatNumber(123.45, 1, "zh-Hans", "CN");
返回:123.4
```

##### Formatting the Percentage

**Format the specified percentage number as another style according to the locale setting.**

Methods: `NumberFormatting.formatPercent`

Parameters

`value`: (required), to indicate the percentage number to be formatted. Example: 0.12.

`locale`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatPercent(0.1345, Locale.forLanguageTag("zh-Hans"));
返回:13%
```

**Format the specified percentage number as another style according to the locale setting, and the specified decimal place.**

Methods: `NumberFormatting.formatPercent`

Parameters

`value`: (required), to indicate the percentage number to be formatted.

`fractionSize`: (optional), to indicate the decimal place. The default value depends on the language.

`locale`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatPercent(0.1345, 1, Locale.forLanguageTag("zh-Hans"));
返回:13.5%

```

**Format the specified percentage number as another style according to the language and locale settings.**

Methods: `NumberFormatting.formatPercent`

Parameters

`value`: (required), to indicate the percentage number to be formatted.

`language`: (required), to indicate the target language for formatting.

`region`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatPercent(0.1345, "zh-Hans", "CN");
返回:13%

```

**Format the specified percentage number as another style according to the language and locale settings, as well as the specified decimal place.**

Methods: `NumberFormatting.formatPercent`

Parameters

`value`: (required), to indicate the percentage number to be formatted.

`fractionSize`: (optional), to indicate the decimal place. The default value depends on the language.

`language`: (required), to indicate the target language for formatting.

`region`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatPercent(0.1345, 1, "zh-Hans", "CN");
返回:13.5%

```

##### Formatting the Currency

**Format the specified currency number as another style according to the locale setting. The default currency is USD.**

Methods: `NumberFormatting.formatCurrency`

Parameters

`amount`: (required), to indicate the currency number to be formatted. Example: 123.45

`locale`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatCurrency(123.45, Locale.forLanguageTag("zh-Hans"));
返回:US$123.45

```

**Format the specified currency number as another style according to the locale and the specified currency code.**

Methods: `NumberFormatting.formatCurrency`

Parameters

`amount`: (required), to indicate the currency number to be formatted.

`currencyCode`: (required), to indicate the currency code. Example: EUR. For all the currency codes, see https://en.wikipedia.org/wiki/ISO_4217.

`locale`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatCurrency(123.45, "EUR", Locale.forLanguageTag("zh-Hans"));
返回:€123.45

```

**Format the specified currency number as another style according to the language and region settings.**

Methods: `NumberFormatting.formatCurrency`

Parameters

`amount`: (required), to indicate the currency number to be formatted.

`language`: (required), to indicate the target language for formatting.

`region`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatCurrency(123.45, "zh-Hans", "CN");
返回:US$123.45

```

**Format the specified currency number as another style according to the language and region settings, as well as the specified currency code.**

Methods: `NumberFormatting.formatCurrency`

Parameters

`amount`: (required), to indicate the currency number to be formatted.

`currencyCode`: (required), to indicate the currency code.

`language`: (required), to indicate the target language for formatting.

`region`: (required), to indicate the target locale for formatting.

Return value

The formatted string.

Example

```
NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);nf.formatCurrency(123.45, "EUR", "zh-Hans", "CN");
返回:€123.45

```

