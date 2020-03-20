---
title: "Translate Directive"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---


#### **Overview**

Use the translate directive in your HTML templates to retrieve translations from the VIP service.

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

| Attribute |  Type   | Required | Description                                                  |
| :-------: | :-----: | :------: | ------------------------------------------------------------ |
|    key    | String  | Required | Bound to L10nDirective.l10n.<br/>Define the key to identify the translation, it should name like this: component_module_page_control_shortmsg. e.g. web_settings_stats_statsTable_host; |
|  source   | String  |    No    | Bound to L10nDirective.source.<br/>English string as default value, API will return it when there's no translation found either from cache or remote VIP server.<br/>If source is null, will looking for source string from sourceBundle. |
|  params   | Array[] |    No    | Bound to L10nDirective.params.<br/>Parameter array, it's used to format the translation/source when they contain placeholders. |


#### **Example Code**

![translate-directive-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/translate-directive/translate-directive-1.png)


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
</style>