---
title: "L10n Service"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 22
---


#### **Overview**

The pipe or directive can't cover all situations, some strings need to be processed in JavaScript module rather than in the template. L10nService provides APIs for translation related information in addition to pipe and directive.

#### **API**

##### **Get source string**

Will return source string in sourceBundle by key.

```
public getSourceString(key: string): string;

```


##### **Get formatted message**

The parameters and return values are the same as the l10n pipe and directive. Get formatted message by key and variables. In order to display the correct translation, parameter locale is required in the live update mode when the locale is unstable. In static refresh mode, the locale and corresponding resource has been initialized at application start, so no need to pass the particular locale as a parameter in this method call. But for the live update, it's required.

```
public getMessage(key: string, variables?: string[]|{}, locale?: string): string;

```

##### **Stream API**

Since the live update is relying on the observable object to notice the available current locale, formatting message based on the l10n service should be performed in the subscription of the 'stream' to ensure the resource is ready for this locale.

```
public stream(): Observable<string | any>;

```


##### **Translate** (deprecated)

Get translation by key source and variables.

```
public translate(key: string, source: string, variables?: string[]|{}, locale?: string): string;

```


#### **Parameters**

| Parameter |   Type   | Required |                         Description                          |
| :-------: | :------: | :------: | :----------------------------------------------------------: |
|    key    |  String  | Required | Define the key to identify the translation, it should name like this: component_module_page_control_shortmsg. |
| variables | string[]/{} |   No   | Variables is an array or object containing the values to replace placeholders with. Required if the source string contains placeholders. |
|  locale   |  string  |    No    | Get the translation of the key in a locale. Default value is current locale. |

#### **Example Code**

![l10n-service-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/l10n-service/l10n-service-1.png)





<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
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
    table thead tr th:nth-child(3) {
        width:10rem;
    }
    article section.page h1:first-of-type {
        text-transform: inherit;
        font-family: inherit;
    }
</style>