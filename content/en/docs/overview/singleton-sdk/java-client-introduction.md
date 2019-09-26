---
title: "Java Client Introduction"
date: 2019-09-24T20:07:51+08:00
draft: false
weight: 10
---

As we know in the last article, Singleton provides plenty of APIs to support G11N. However, calling the APIs directly is time-consuming and inefficient. So, Singleton has introduced several Clients for different programming language environments, to encapsulate the API calls to the Service, which significantly improves efficiency, and reduces the cost in development work. These Clients can call the Service's APIs to get the translation and pattern data, then parse the data to provide the translation and data formatting features.

In this article, we'll talk about the Java Clients.

## Features in Java Clients

- Getting the translation.
- Formatting the data: Formatting the date and time, number, currency, and the singular and plural variations is supported at this time. All the data should be CLDR-compliant.
- Collecting the resources to be translated (source): Sends the strings to be translated to Singleton Service, for the translation team to work on.
- Getting the pseudo translation: If the translated string is not there, returns the pseudo translation to check if the Client calls are OK.
- Checking the translation status: Checks if the translated string is there at the server side.
- Providing the cache: Makes quicker responses by avoiding the frequent server connections.

## APIs Available

### Formatting API

##### Formatting the Date and Time

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

### Translation API

##### Getting the translation

**Getting the translation of a string**

Methods: `TranslationMessage.getString`

Parameters

`locale`: (required), to indicate the locale used for the translation.

`component`: (required), to indicate the component using the translation.

`key`: (required), to indicate the key for the translation.

`source`: (optional), to indicate the source for the translation.

`comment`: (optional), to provide the comment.

`args`: (optional) If the translated string contains variables, replaces them with the values in args.

Return value

The translated string.

Example

- 

```
TranslationMessage tMessage = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);tMessage.getString(Locale.forLanguageTag("zh-Hans"), "default", "global_text_username", "User name", "comments of user name", null);

```

**Get the translation of a string according to the key (no source required).**

Methods: `TranslationMessage.getString2`

Parameters

`locale`: (required), to indicate the locale used for the translation.

`component`: (required), to indicate the component using the translation.

`key`: (required), to indicate the key for the translation.

`bundle`: (required), to indicate the file containing the source, which is a collection of key-value pairs.

`args`: (optional) If the translated string contains variables, replaces them with the values in args.

Return value

The translated string.

Example

```
TranslationMessage tMessage = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);tMessage.getString2("default", "messages", Locale.forLanguageTag("zh-Hans"), "global_text_username", null);

```

**Get the translation of a component.**

Methods: `TranslationMessage.getStrings`

Parameters

`locale`: (required), to indicate the locale used for the translation.

`component`: (required), to indicate the component using the translation.

Return value

The translated strings for the component.

Example

```
TranslationMessage tMessage = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);tMessage.getStrings(Locale.forLanguageTag("zh-Hans"), "default");

```

##### Collecting the source string(s)

**Collecting a source string by sending it to the server.**

Methods: `TranslationMessage.postString`

Parameters

`locale`: (required), to indicate the locale.

`component`: (required), to indicate the component using the source string.

`key`: (required), to indicate the key for the source string.

`source`: (required), to indicate the source string to be sent.

`comment`: (optional), to provide the comment.

Return value

true: Sent successfully.

false: Sent failed.

Example

```
TranslationMessage tMessage = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);tMessage.postString(Locale.forLanguageTag("zh-Hans"), "default", "global_text_username", "User name", "comments of user name");

```

**Collecting several source strings by sending them to the server.**

Methods: `TranslationMessage.postStrings`

Parameters

`locale`: (required), to indicate the locale.

`component`: (required), to indicate the component using the source strings.

`sources`: (required), to indicate the source strings to be sent.

Return value

true: Sent successfully.

false: Sent failed.

Example

```
TranslationMessage tMessage = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);List<JSONObject> sources = new ArrayList<JSONObject>();JSONObject s1 = new JSONObject();s1.put("key", "key-1");s1.put("source", "source-1");s1.put("commentForSource", "It's a comment");sources.add(s1);tMessage.postStrings(Locale.forLanguageTag("zh-Hans"), "default", sources);

```

##### Finding the translation status(es)

**Finding the translation status of a string.**

Methods: `TranslationMessage.isAvailable`

Parameters

`locale`: (required), to indicate the locale.

`component`: (required), to indicate the component.

`key`: (required), to indicate the key for the string.

Return value

true: Translation is ready.

false: Translation is not ready.

Example

```
TranslationMessage tMessage = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);tMessage.isAvailable("default", "global_text_username", Locale.forLanguageTag("zh-Hans"));

```

**Finding the translation statuses of all strings in a component.**

Methods: `TranslationMessage.isAvailable`

Parameters

`locale`: (required), to indicate the locale.

`component`: (required), to indicate the component.

Return value

true: Translation is ready.

false: Translation is not ready.

Example

```
TranslationMessage tMessage = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);tMessage.isAvailable("default", Locale.forLanguageTag("zh-Hans"));

```

## Profile

The Client provides several parameters, which can be configured easily through a profile. The parameters include:

- productName: Product name. The server returns the translation resources according to the product name.
- version: Version number. The server returns the translation resources for the version.
- vipServer: Singleton server address. Example: https://localhost:8090.
- initializeCache: Initializes the cache or not. The default value is false.
- *true: When the application starts, the data of all components of the product are loaded into the cache from the server.*
- *false: Caches the translation data of a component only when accessing the component.*
- pseudo: Returns the pseudo translation or not. The default value is false.
- *true: Returns the pseudo translation, regardless of whether the translated string is here.*
- *false: Returns the translation in a normal way.*
- collectSource: Sends the new source string to the server or not. Default: false.
- *true: Sends the new string to the server.*
- *false: Doesn't send the new string to the server.*
- cleanCache: Clears the cache or not. The default value is false.
- *true: Cleans up the cache regularly.*
- *false: Doesn't clean up the cache regularly.*
- cacheExpiredTime: The expiration time of the cache, in milliseconds.
- machineTranslation: Returns the MT (Machine Translation) output or not. The default value is false. Turned off at the server side by default.
- *true: Returns the MT (Machine Translation) output, regardless of whether the existing translation is here.*
- *false: Returns the translation in a normal way.*