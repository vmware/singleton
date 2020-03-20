---
title: "Number Format Pipe"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---


#### **Overview**

Formats a number as text. Group sizing and separator and other locale-specific configurations are based on the pattern data.

#### **Usage**

```
{{ value | numberFormat }}

```


#### **Input Value**

| Parameter |  Type  |         Description         |
| :-------: | :----: | :-------------------------: |
|   value   | number | The number to be formatted. |


#### **Example Code**

![number-format-pipe-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/number-format-pipe/number-format-pipe-1.png)

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
</style>