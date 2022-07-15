---
title: "Relative Time Format"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 26
---

#### **Overview**

Formats simple relative dates. Try to give the best relative time span representation based on start time and end time.

#### **API**

```
public formatRelativeTime( from: Date, to: Date, locale?: string, options?: Object ): string

```


#### **Parameters**

| Parameter |  Type  | Required | <div style="text-align:center">Description</div>                                                                                                                                                                                   |
|:---------:|:------:|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|   from    |  Date  | Required | Relative time start time.                                                                                                                                                                                                          |
|    to     |  Date  | Required | Relative time end time.                                                                                                                                                                                                            |
|  locale   | String | Optional | The locale is only required in async mode for non-blocking loading. Please refer to the [stream API](../locale-management#asynchronous-api) usage.                                                                                 |
|  options  | Object | Optional | The numeric default value is 'always'. If numeric: 'auto' option is passed, it will produce the string yesterday or tomorrow instead of 1 day ago or in 1 day, this allows to not always have to use numeric values in the output. |


#### **Time Range and Unit**

|           Range           |  Unit  |     Output     |
|:-------------------------:|:------:|:--------------:|
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

```
import { I18nService } from ' @singleton-i18n/angular-client';
import { Component, OnInit, OnDestroy } from '@angular/core';


@Component({
    selector: 'test',
    templateUrl: './test.component.html'
})
export class TestComponent implements OnInit, OnDestroy {
    subscription: any;
    time:string;
    constructor(private i18nService: I18nService) {}

    ngOnInit() {
        this.subscription = this.i18nService.stream.subscribe((locale: string) => {
            // Assuming locale is en-US :
            const from = new Date(2019, 8, 1),
                to = new Date(2019, 9, 1);

            this.time = this.i18nService.formatRelativeTime( from, to, locale);
            // Output: in 1 month

            this.time = this.i18nService.formatRelativeTime( to, from, locale);
            // Output: 1 month ago

            this.time = this.i18nService.formatRelativeTime( from, to, locale, { numeric: 'auto' });
            // Output: next month

            this.time = this.i18nService.formatRelativeTime( to, from, locale, { numeric: 'auto' });
            // Output: last month
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.subscription = undefined;
    }
}

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