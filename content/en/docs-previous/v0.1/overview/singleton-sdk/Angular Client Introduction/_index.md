---
title: "Angular Client Introduction"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 30
---

Singleton for Angular client is common lib developed by VMware G11n team to provide i18n support for Angular framework. The client will communicate with singleton server for fetching translation and i18n patterns.

Most translations provided by singleton service comes from Human translators, when new application is connected to singleton service, source strings (English strings), required language and related information will be sent to singleton service, singleton service doesn’t return real translation immediately since the human translators need time to figure out the quilted translation, at this moment, singleton service just delivers pseudo tag as placeholder to indicate singleton client works well with singleton service and App itself in development stage. Once the translations are ready, when the client side calls singleton service again, the real translations will be delivered via HTTP request. Please note that under English locale, the client doesn't make a network call to singleton service, but return English directly.

Besides Localization, singleton client also provides I18n support for Datetime format, number format, currency and plural. singleton I18n service is based on CLDR data repository and keep the same scope in supported locales set. For the specific usages and API details please refer to following sections, if you find below APIs can't cover all usages of your application requirement, please feel free to contact with our team to add related API.



#### **Features**

- Level-2 Dates, Times, Numbers and Currencies formatting support. For more details refer to i18n Level-2.
- Level-3 support. For more details refer to i18n Level-3.


#### **Version**

- 10.x (support Angular10)
- 9.x (support Angular9)
- 8.x (support Angular8)
- 7.x (support Angular7.x)
- 6.x (support Angular6.x)
- 1.x (support Angular4.x & Angular5.x)
- 0.7.x is no longer maintained, only for bugfix

#### **Installing the NPM module**

The module is published internal to VMware only, on the build-artifactory NPM registry. To install it,
you need to set your NPM registry either in your .npmrc or on the command line with --registry.

The command below will install this library and all its peer dependencies. The internal registry is added as an argument.

```
npm install @i18n-singleton/angualr-client \
    @angular/core@^10.0.0 \
    debug@^4.1.0 \
    html-element@^2.2.0 \
    rxjs@^7.0.0 \
    superagent@^5.2.2 \
    typescript@^4.0.0 \
    --save

```

#### **Strings externalization**

```
// intro.l10n.ts

export const ENGLISH = {
    ...
    'some.unique.key' : 'English for some.unique.key',
    'singleton.description' : '{0} is common lib developed by VMware G11n team.',

    // if the comment is required, please use array as the value
    // the first item represents actual value, the second is the comment
    'create.time': ['This example was created on {0}', 'the variable is a date']
    ...
};

```

```
// error.l10n.ts

export const ENGLISH = {
    ...
    'timeout': 'network connection timeout'
    ...
};

```

#### **Configuration**

```
// i18n.config.ts
// Since the variable name exported in the resource file can only be ENGLISH,
// when importing the resource file, please use import as to distinguish the resource files.

import { ENGLISH as IntroMessages } from './intro.l10n';
import { ENGLISH as ErrorMessages } from './error.l10n';
import { PatternCategories } from '@singleton-i18n/angular-client';

export const I18nConfig = {
    productID: 'SingletonNgxSample',
    component: 'default',
    version: '1.0.0',
    i18nScope: [
        PatternCategories.DATE,
        PatternCategories.NUMBER,
        PatternCategories.CURRENCIES
    ],
    host: 'https://singleton.service.com:8090/',
    isPseudo: false,
    sourceBundles: [IntroMessages, ErrorMessages],
    timeout: 5000
};

```


#### **Parameters**

|     Parameter      | Required |              Type              | <div style="text-align:center">Description</div>                                                                                                                                                                                                                                                                                                                                                                                                                             |
|:------------------:|:--------:|:------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|     productID      | required |             string             | Product name. singleton service doesn’t explicitly restrict name of product, but it's better to keep short and sync with the name in release master.                                                                                                                                                                                                                                                                                                                         |
|     component      | required |             string             | Component name. singleton internal concept is used to divide UI strings into different units according to certain rules, make sure each component doesn’t exceed 5000 strings.                                                                                                                                                                                                                                                                                               |
|      version       | required |             string             | Release version. singleton internal concept is used to indicate the current version of the UI strings.                                                                                                                                                                                                                                                                                                                                                                       |
|        host        | required |             string             | URL of Singleton host, please refer to the known Singleton Instances. The host can be optional when an isolated feature module which configured with translationBundles.                                                                                                                                                                                                                                                                                                     |
|      isolated      | optional |            boolean             | Determine whether the resources of this singleton component are isolated. The default value is true. The resources are isolated between different singleton components via different namespaces. If this value is false, the current singleton component's resources will be shared with other singleton components. It is generally recommended to set this field to false in the shared singleton component configuration for the shared module or standalone Angular lib. |
|     i18nScope      | optional |               []               | Define the scope of i18n support. The default scope is empty.<br/>PatternCategories.DATE means date&time format support.<br/>PatternCategories.NUMBER means decimal and percent format support.<br/>PatternCategories.PLURAL means single plural support.<br/>PatternCategories.CURRENCIES means currency format support.<br/>PatternCategories.DATEFIELDS means relative time format support.                                                                               |
|      isPseudo      | optional |             string             | A flag to determine whether to show pseudo translation in debugging or developing stage.                                                                                                                                                                                                                                                                                                                                                                                     |
|   sourceBundles    | optional | {key: string]: source: string} | Array of the source string collections.The source strings of the project can be managed in the multiple objects and set as sourceBundles in the configuration. If there are duplicate keys among the source collections, a warning message will popped up in the console. Use the command line util to collect them to the singlethon server.                                                                                                                                |
|      timeout       | optional |             number             | An integer denoting milliseconds. Default value is 3000. Limit i18n and l10n resource request time, if the request exceeds this time, the request will be cancelled and a timeout error will be generated.                                                                                                                                                                                                                                                                   |
|     i18nAssets     | optional |             string             | The folder path is used to save translation files. Files naming rule: translation_${language}.json<br/>The all translations could be fetched from singleton service into local bundle files at build time, thus the application can support localization without singleton service at runtime.                                                                                                                                                                               |
| translationBundles | optional |   { [key: string]: object }    | A collection of languages and their corresponding translations. Naming rule: { ${language}: { key: translation_string } }<br/>The difference with 'i18nAssets' is this object will compiled into the final bundle, but 'i18nAssets' relies on Http call to get translation bundle per language.                                                                                                                                                                              |


#### **Singleton module initialization**

The **forRoot** static method is a convention that provides and configures services at the same time. 
Make sure you only call this method in the root module of your application, most of the time called AppModule.

>Note: Never call a **forRoot** static method in the shared module. You might end up with different instances of the service in your injector tree.

```
// app.module.ts
...
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { VIPModule, VIPService, LocaleService, getBrowserCultureLang } from '@singleton-i18n/angular-client';
import { I18nConfig } from './i18n.config';

export function initVIPConfig(vipService: VIPService, localeService: LocaleService) {
    // Specify locale, either from browser language or user's profile.
    const currentlocale: string  = getBrowserCultureLang();
    localeService.init(currentlocale);
    return () => vipService.initData(I18nConfig);
}

@NgModule({
    ...
    imports: [
        ...
        HttpClientModule,
        VIPModule.forRoot(),
        ...
    ],
    providers: [{
        provide: APP_INITIALIZER,
        useFactory: initVIPConfig,
        deps: [
            VIPService,
            LocaleService
        ],
        multi: true
    }],
    ...
})
export class AppModule {} 

```

#### **Usages**

After importing the VIPModule and initializing the related services, now you can start using the utilities provided by ngx-vip to 
consume translations and format data according to different locale patterns.


#### **Html Template Usage**

```
// app.component.html

// Translate Pipe
{{'some.unique.key' | vtranslate }}

{{'singleton.description' | vtranslate: 'Singleton for Angular client'}}

// Translate Directive
<span l10n='some.unique.key' ></span>

<span l10n='singleton.description' [params]="['Singleton for Angular client']" >

// Date & Time Format Pipe
{{ date | dateFormat }}

// Number Format Pipe
{{ num | numberFormat }}

```

#### **Component Usage**

```
// shared.component.ts

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

#### **Getting Help**

If you cannot find the necessary documentation or the answer to your question by looking through the [code](https://gitlab.eng.vmware.com/core-build/vmw-ngx-components/tree/master), reach out in the [#ngx](slack://channel?id=CNCHU5R4N&team=T024JFTN4) Slack channel.






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
    ul li {
        list-style: circle;
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