---
title: "Relative Time Format"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

#### **Overview**

Formats simple relative dates. Try to give the best relative time span representation based on start time and end time.

#### **API**

```
public formatRelativeTime( from: Date, to: Date, locale?: string, options?: Object ): string

```


#### **Parameters**

| Parameter |  Type  | Required |                         Description                          |
| :-------: | :----: | :------: | :----------------------------------------------------------: |
|   from    |  Date  | Required |                  Relative time start time.                   |
|    to     |  Date  | Required |                   Relative time end time.                    |
|  locale   | String | Optional | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](https://ngx.eng.vmware.com/@vmw/ngx-vip/locale-management/documentation#Asynchronous-API) usage. |
|  options  | Object | Optional | The numeric default value is 'always'. If numeric: 'auto' option is passed, it will produce the string yesterday or tomorrow instead of 1 day ago or in 1 day, this allows to not always have to use numeric values in the output. |


#### **Time Range and Unit**

|           Range           |  Unit  |     Output     |
| :-----------------------: | :----: | :------------: |
|      0s ~ 44 seconds      | second |  x second(s)   |
|      45 ~ 89 seconds      | minute |    1 minute    |
|  90 seconds ~ 44 minutes  | minute | 2 ~ 44 minutes |
|      45 ~ 89 minutes      |  hour  |    an hour     |
|   90 minutes ~ 21 hours   |  hour  |  2 ~ 21 hours  |
|       22 ~ 35 hours       |  day   |     a day      |
|    36 hours ~ 25 days     |  day   |   2 ~ 25 day   |
|       26 ~ 45 days        | month  |    a month     |
|       45 ~ 319 days       | month  | 2 ~ 10 months  |
| 320 ~ 547 days(1.5 years) |  year  |     a year     |
|         548 days+         |  year  |    2+ years    |


#### **Example Code**


![relative-time-format-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/relative-time-format/relative-time-format-1.png)


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
</style>