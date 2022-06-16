---
title: "Data Initialization"
date: 2019-09-24T20:08:31+08:00
draft: false
weight: 16
---


In general, for most of the products, it's good enough to load all translations and patterns one time in root module when the application starts, but for some special requirements, such like: isolated translation for feature module or using lazy module to enhance performance, please refer to [Isolated feature module](../isolated-feature-module) and [Lazy Load Module](../lazy-load-module). singleton client provides two ways to load i18n data and initialize the services in root module:

##### **Blocking loading**

**For preloading modules** - Create a factory function that loads i18n resource data and provide that function to the APP_INITIALIZER token. The function is executed during the application bootstrap process, and the needed data is available on startup.

```
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

**For lazy-loading modules** - Configure i18nDataGuard as part of the route object to load i18n resource for a lazy-loading module by reading lite Singleton configuration from route parameters, asynchronously load the data, and have it ready by the time the module activates and initializes.

```
const routes: Routes = [
{
        path: '',
        component: SampleComponent,
        canActivate: [I18nDataGuard],
        data: {
        // 'vipConfig' is specified keyword of Singleton configuration.
        // In the simplified configuration of lazy loading module, 
        // only the component name is required, and other fields are optional.
            vipConfig: {
                component: 'sample',
                sourceBundles: [ENGLISH]
            }
        }
    }
]

```


##### **Non-blocking loading**

Whether it is preloading modules or lazy-loading modules, in addition to loading data in a blocking way, you can also use a non-blocking way.

>Note that since data is loaded in a non-blocking way, when calling synchronous 'getMessage' or data formatting-related methods, 
be sure to use these synchronous methods safely by subscribing to the 'stream' API.

```
// app.module.ts

export class AppModule {
    constructor( vipService: VIPService, localeService: LocaleService ) {
        const currentlocale: string  = getBrowserCultureLang();
        localeService.init(currentlocale);
        vipService.initData(I18nConfig);
    }
}

```


```
// lazy.module.ts

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