---
title: "Translate Pipe"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 20
---

#### **Overview**

Use the translate pipe in your HTML templates to retrieve translations from the singleton service.


#### **API**

##### **L10nPlus pipe**

Get translation by key, will find the source in the sourceBundle according to the key.

```
{{ key | vtranslate:[ : variables ] }}

```

##### **L10n pipe** (deprecated)

```
{{ key | translate: [ : source [ : variables ] ] }}

```

#### **Input**

| Parameter |  Type  |                         Description                          |
| :-------: | :----: | :----------------------------------------------------------: |
|    key    | string | Define the key to identify the translation, it should name like this: component_module_page_control_shortmsg. e.g. web_settings_stats_statsTable_host; |

#### **Parameters**

| Parameter |  Type   | Required |                         Description                          |
| :-------: | :-----: | :------: | :----------------------------------------------------------: |
|  source   | String  |    No    | English string as default value, API will return it when there's no translation found either from cache or remote singleton server. |
| variables | Array[] |    No    | Parameter array, it's used to format the translation/source when they contain placeholders. |


#### **Example Code**

![translate-pipe-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/translate-pipe/translate-pipe-1.png)


![translate-pipe-2](https://github.com/zmengjiao/singleton/raw/website/content/en/images/translate-pipe/translate-pipe-2.png)


<style>
    html {
        font-family: Metropolis;
        color: #575757;
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
    /* table thead tr th:nth-child(3) {
        width:10rem;
    } */
    article section.page h1:first-of-type {
        text-transform: inherit;
        font-family: inherit;
    }
</style>