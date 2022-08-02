---
title: "Shared Feature Module"
date: 2022-06-16T13:19:02+03:00
draft: false
weight: 33
---


#### **Overview**

Creating shared modules allows you to organize and streamline your code. You can put commonly used directives, pipes, and components into one module and then import just that module wherever you need it in other parts of your application.
Therefore from the perspective of internationalization, singleton Angular client also provides corresponding support for the shared modules.

#### **Resource Isolation**

By default, the resources of each Singleton component are isolated and registered in different namespaces to avoid duplicate keys causing content to be unexpectedly overwritten.

#### **Example Code**

##### **Configuration**

```
// i18n.config.ts
import { ENGLISH } from './header.l10n';
import { PatternCategories } from '@singleton-i18n/angular-client';

const I18nConfig = {
    productID: 'vipuiheader',
    component: 'default',
    version: '1.0.0',
    i18nScope: [
        PatternCategories.DATE,
        PatternCategories.NUMBER,
        PatternCategories.CURRENCIES
    ],
    host: 'https://singleton.service.com:8090/',
    // By default, the resources of each component are isolated.
    // Please set isolated as false in a shared module.
    isolated: false,
    sourceBundles: [ENGLISH]
};

```

##### **Configuration Registration**

> Note: Never call a forRoot static method in a shared module. You might end up with different instances of the service in your injector tree.

```
// shared-module.module.ts
import { VIPModule, VIPService } from ' @singleton-i18n/angular-client';
import { libConfig } from './i18n.util';

@NgModule({
    imports: [
        ...
        VIPModule
    ]
})

export class SharedFeatureModule {
    constructor(private vipService: VIPService) {
        // register feature module configuration
        vipService.registerComponent(libConfig);
    }
}

```

##### **Usages**

Since the resource loading method in a shared module is uncertain, it may be synchronous or asynchronous depending on the calling scenario, when calling synchronous 'getMessage' or data formatting-related methods,
be sure to use these synchronous methods safely by subscribing to the 'stream' API. All pipe utilities encapsulated in the library support both synchronous and asynchronous scenarios without any difference.

```
// shared-module.module.ts
import { L10nService, I18nService, VIPService } from '@singleton-i18n/angular-client';
import { Component, OnInit, OnDestroy } from '@angular/core';
    
@Component({
    selector: 'shared',
    templateUrl: './shared.component.html'
})
export class SharedComponent implements OnInit, OnDestroy {
    subscription: any;
    constructor(private l10nService: L10nService, private i18nService: I18nService, private vipService: VIPService) { }

    ngOnInit() {
        this.subscription = this.vipService.stream.subscribe((locale: string) => {
            this.translation = this.l10nService.getMessage('singleton.description', [ 'Singleton for Angular client' ], locale);
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
   blockquote {
        background: #f5dddb;
        border: 1px solid #f8b5b4;
        color: #575757;
    }    
    blockquote>p {
        display: inline-block;
        margin: 1rem 0;
    }
</style>