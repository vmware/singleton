---
title: "Currency Format Pipe"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

#### **Overview**

Formats a number into currency string. Group sizing and separator and other locale-specific configurations are based on the pattern data.

#### **Usage**

```
{{ value | currencyFormat [ : currencyCode ]  }}

```

#### **Parameters**

|  Parameter   |  Type  |                         Description                          |
| :----------: | :----: | :----------------------------------------------------------: |
| currencyCode | string | Currencycode should be in accordance with [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217) standard, such as USD for the US dollar and EUR for the euro. Optional. Default value is USD. |

#### **Example Code**

![currency-format-pipe-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/currency-format-pipe/currency-format-pipe-1.png)

<style>
    html {
        font-family: Metropolis;
        color: #575757;
    }
    section strong {
        font-weight: 400;
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
    article section.page pre {
        background-color: #fafafa;
        border:1px solid #ccc;
        padding-top: 2rem;
    }
</style>
