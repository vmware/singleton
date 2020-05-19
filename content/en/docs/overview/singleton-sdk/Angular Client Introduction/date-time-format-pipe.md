---
title: "Date Time Format Pipe"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 26
---


#### **Overview**

Use the dateFormat pipe in your HTML templates to format a date according to locale rules.

#### **Usage**

```
{{ value | dateFormat [ : format [ : timezone ] ] }}

```

#### **Input Value**

| Parameter | Type |                         Description                          |
| :-------: | :--: | :----------------------------------------------------------: |
|   value   | any  | a date object or a number (milliseconds since UTC epoch) or an ISO string (https://www.w3.org/TR/NOTE-datetime). |

#### **Parameters**

| Parameter |  Type  |                         Description                          |
| :-------: | :----: | :----------------------------------------------------------: |
|  format   | string | indicates which date/time components to include. The format can be predefined as shown below (all examples are given for en-US) or custom as shown in the table. Default is 'mediumDate'. |
| timezone  | string | to be used for formatting. It understands UTC/GMT and the continental US time zone abbreviations, but for general use, use a time zone offset, for example, '+0430' (4 hours, 30 minutes east of the Greenwich meridian) If not specified, the local system timezone of the end-user's browser will be used. |

#### **Pre-defined format options**

|    Format    |               Unit                | Output                                                |
| :----------: | :-------------------------------: | :---------------------------------------------------- |
| 'shortTime'  |             'h:mm a'              | e.g. 5:40 PM                                          |
| 'mediumTime' |            'h:mm:ss a'            | e.g. 5:40:22 PM                                       |
|  'longTime'  |            'h:mm:ss z'            | e.g. 5:40:22 PM GMT+8                                 |
|  'fullTime'  |          'h:mm:ss zzzz'           | e.g. 5:40:22 PM GMT+08:00                             |
| 'shortDate'  |             'M/d/yy'              | e.g. 2/9/18                                           |
| 'mediumDate' |            'MMM d, y'             | e.g. Feb 9, 2018                                      |
|  'longDate'  |            'MMMM d, y'            | e.g. February 9, 2018                                 |
|  'fullDate'  |         'EEEE, MMMM d, y'         | e.g. Friday, February 9, 2018                         |
|   'short'    |         'M/d/yy, h:mm a'          | e.g. 2/9/18, 5:40 PM                                  |
|   'medium'   |       'MMM d, y, h:mm:ss a'       | e.g. Feb 9, 2018, 5:40:22 PM                          |
|    'long'    |     'MMMM d, y, h:mm:ss a z'      | e.g. February 9, 2018 at 5:40:22 PM GMT+8             |
|    'full'    | 'EEEE, MMMM d, y, h:mm:ss a zzzz' | e.g. Friday, February 9, 2018 at 5:40:22 PM GMT+08:00 |



#### **Example Code**

Assuming 'date' is (year: 2018, month: 2, day: 09, hour: 17, minute: 40, second: 22) in the local time and locale is 'en-US':

![date-time-format-pipe-1](https://github.com/zmengjiao/singleton/raw/website/content/en/images/date-time-format-pipe/date-time-format-pipe-1.png)




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
        width:26rem;
    }
    article section.page h1:first-of-type {
        text-transform: inherit;
        font-family: inherit;
    }
</style>

