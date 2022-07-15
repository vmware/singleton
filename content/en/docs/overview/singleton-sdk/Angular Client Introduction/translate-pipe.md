---
title: "Translate Pipe"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 20
---

#### **Overview**

Use the translate pipe in your HTML templates to retrieve translations from the singleton service.


#### **API**

##### **L10n pipe**

Get translation by key, will find the source in the sourceBundle according to the key.

```
{{ key | vtranslate:[ : variables ] }}

```

#### **Input**

| Parameter |  Type   | Required | <div style="text-align:center">Description</div>                                                                                                       |
|:---------:|:-------:|----------|:-------------------------------------------------------------------------------------------------------------------------------------------------------|
|    key    | string  | required | Define the key to identify the translation, it should name like this: component_module_page_control_shortmsg. e.g. web_settings_stats_statsTable_host; |
| variables | Array[] | optional | Parameter array, it's used to format the translation/source when they contain placeholders.                                                            |                                        |


#### **Example Code**

```
export const ENGLISH = {
    'some.unique.key' : 'English for some.unique.key',
    'singleton.description' : '{0} is common lib developed by VMware G11n team.',
    'innerHTML.usage' : 'For more details, please refer to [Singleton Angular Client](https://github.com/vmware/singleton/tree/g11n-angular-client).'
    // innerHTML usage: use the innerHTML attribute with the pipe on any element. Not only for plain text node, also for raw HTML tags in source strings as inline tags.
    // Note: Convert " to &quot; for the value of attribute.
}

```


```
// L10n pipe
{{'some.unique.key' | vtranslate }}

{{'singleton.description' | vtranslate: 'Singleton for Angular client'}}

// L10n pipe innerHTML sample
<label [innerHTML]="'innerHTML.usage' | vtranslate"></label>

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