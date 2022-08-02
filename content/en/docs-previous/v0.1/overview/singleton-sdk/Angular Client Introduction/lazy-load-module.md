---
title: "Lazy Load Module"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 18
---


#### **Overview**

As the application is growing, certain modules will be defined as the lazy module. In that situation, the translation and patterns could be loaded as lazy mode to enhance performance. The Singleton Angular client provides this option to load translation and patterns along with lazy module. Using the 'forChild' API to initialize the Singleton module, thus the lazy module will use the separated Singleton services instances created by its own injector.

The following example is the most recommended usage based on non-blocking data loading. Still, it requires subscribing to the stream to ensure that the data has been loaded when consuming synchronous APIs. If you don't want to implement live locale switching or are concerned about subscribing to the stream event, you can load data in a blocking way through **I18nDataGuard** in the corresponding route and then use synchronous APIs directly. More details, please refer to [Data initialization](../data-initialization).

#### **Example**

```
// lazy.module.ts
...
import { VIPModule } from '@singleton-i18n/angular-client';

@NgModule({
    ...
    imports: [
        ...
        VIPModule.forChild()
    ],
    ...
})

export class LazyModule {
    constructor(private service: VIPService) {
    // In the simplified configuration of lazy loading module, 
    // only the component name is required, and other fields are optional.
        service.initLazyModuleData({
            component: 'default',
            sourceBundles: [ENGLISH]
        });
    }
}

```

```
// sample.component.ts

import { L10nService, I18nService, VIPService } from '@singleton-i18n/angular-client';
import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
    selector: 'sample',
    templateUrl: './sample.component.html'
})
export class SampleComponent implements OnInit, OnDestroy {
    subscription: any;
    constructor(private l10nService: L10nService, private i18nService: I18nService, private vipService: VIPService) { }

    ngOnInit() {
        this.subscription = this.vipService.stream.subscribe((locale: string) => {
            this.translation = this.l10nService.getMessage('Singleton.description', [ 'Singleton for Angular client' ], locale);
            this.date = this.i18nService.formatDate(new Date(), 'short', locale );
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