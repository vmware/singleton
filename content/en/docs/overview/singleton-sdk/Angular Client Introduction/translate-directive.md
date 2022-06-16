---
title: "Translate Directive"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 21
---


#### **Overview**

Use the translate directive in your HTML templates to retrieve translations from the singleton service.

```
@Directive({ selector: '[l10n]' })
class L10nDirective implements AfterViewInit {
    l10n: string;
    source: string;
    params: string[];
    constructor(el: ElementRef, ...)
    ngAfterViewInit()
}

```

#### **Selector**

[l10n]

#### **Inputs**

| Attribute |  Type   | Required | <div style="text-align:center">Description</div>                                                                                                                                                                                                |
|:---------:|:-------:|:--------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|    key    | String  | Required | Bound to L10nDirective.l10n.<br/>Define the key to identify the translation, it should name like this: component_module_page_control_shortmsg. e.g. web_settings_stats_statsTable_host;                                                         |
|  source   | String  |    No    | Bound to L10nDirective.source.<br/>English string as default value, API will return it when there's no translation found either from cache or remote singleton server.<br/>If source is null, will looking for source string from sourceBundle. |
|  params   | Array[] |    No    | Bound to L10nDirective.params.<br/>Parameter array, it's used to format the translation/source when they contain placeholders.                                                                                                                  |


#### **Example Code**

```
// Translate directive with source from sourceBundle, so there is no source in template
<span l10n='singleton.description' [params]="['Singleton for Angular client']" ></span>

// Deprecated:  Simple translate directive use
<span l10n='some.unique.key' source='English for some.unique.key'></span>

//  Deprecated: Translate directive with substitution
<span l10n='some.unique.key' source='English for some.unique.key with substitution {0}' [params]="['someTemplateVariable']"></span>

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