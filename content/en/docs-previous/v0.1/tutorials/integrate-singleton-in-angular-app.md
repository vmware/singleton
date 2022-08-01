---
title: "Integrate Singleton in Angular App"
date: 2019-09-24T20:10:10+08:00
draft: false
weight: 30
---
```
This guideline demonstrates how to integrate Singleton AngularClient with your Angular Framework on your application.

Singleton AngularClient is a common lib providing I18n support for Angular framework on your application. The client will communicate with
Singleton service to dynamically fetch translation and I18n pattern data for providing globalization support in messages and formatting.

For I18n formatting, Singleton AngularClient also provides I18n support for datetime format, number format, currency and plural.
Singleton AngularClient formatting is based on CLDR data repository and keeps the same scope in supported locale set.
```

## Install 
```Javascript
npm install @singleton-i18n/angular-client --save 
 ```

## Initialization

```Javascript
//Configure app.module.ts for loading data
import { APP_INITIALIZER } from '@angular/core';    //These function are injected at application startup and executed during app initialization, singleton rely on it to initilize singleton for loading data
import { HttpClientModule } from '@angular/common/http';    // Angular-Client based on this to communicate with Singleton Service
import {
    VIPModule,
    VIPService,
    LocaleService,
    PatternCategories,
    getBrowserCultureLang,
} from '@singleton-i18n/angular-client';
import { ENGLISH } from 'app.l10n'; //ENGLISH is source strings extracted from Angular Code


export function initSingletonConfig(service: VIPService, localeService: LocaleService) {
    const browserLanguage = getBrowserCultureLang();    //get browser's language
    localeService.init(browserLanguage);
    return () => service.initData({
        productID: 'ProductName',
        component: 'default',
        version: '1.0.0',
        host: 'https://singleton-service',
        i18nScope: [
            PatternCategories.DATE,
            PatternCategories.NUMBER,
            PatternCategories.PLURAL,
            PatternCategories.CURRENCIES
        ],
        isPseudo: false,
        collectSource: false,
        sourceBundle: ENGLISH,
    });
}

@NgModule({
    ...
    imports: [
        ...
        HttpClientModule,
        VIPModule.forRoot(),
        ...
    ],
    providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: initSingletonConfig,
            deps: [
                VIPService,
                LocaleService,
            ],
            multi: true
        },
    ],
    ...
})
export class AppModule {}

 ```

## Adoption

---
#### Translation
1. **Implement translation in html using 'vtranslate' pipe**
```html
<div class="section-title">Getting started</div>  //Original frontend code
```

```javascript
//extracted source string into app.l10n.ts file
export const ENGLISH = {
    STARTED: 'Getting started'
} 
```
```html
<div class="section-title">{{ 'STARTED' | vtranslate }}</div>  //use vtranslate pipe to implement translation
```
2. **Source string along with variables needs to translate**
```html
<span class="page-count-info">{{ pagination.firstItem + 1 }} - {{ pagination.lastItem + 1 }} of {{ pagination.totalItems }} requests</div>  //Original frontend code
```

```javascript
//extracted source string into app.l10n.ts file
export const ENGLISH = {
    REQ_PAGE: '{0} - {1} of {2} requests'
} 
```
```html
<span class="page-count-info">{{ 'REQ_PAGE' | vtranslate : pagination.firstItem + 1 : pagination.lastItem + 1 : pagination.totalItems }}</div>  //use vtranslate pipe to implement translation
```

3. **Implement translation in html using 'l10n' directive**
```html
<div class="section-title">Getting started</div>  //Original frontend code
```

```javascript
//extracted source string into app.l10n.ts file
export const ENGLISH = {
    STARTED: 'Getting started'
} 
```
```html
<div l10n="STARTED" class="section-title" ></div>  //use l10n directive to implement translation
```

4. **Source strings in ts file needs to translate**
```javascript
//extracted source string into app.l10n.ts file
export const ENGLISH = {
    IDENTITY_VERSION: 'Identify Manager with version {0} is supported'  // this text includes one variable which is repalced by {0} placeholder
} 
```

```Typescript
import { L10nService } from from '@singleton-i18n/angular-client';
@Component({
    ...
})
export class DemoComponent implements OnInit {
    constructor(private l10nService: L10nService){}
    getVersions(){
        ...
       //this.supportedVersionText = 'Identify Manager with version ' + data.versions + ' is supported';  //original code
       this.supportedVersionText = this.l10nService.getMessage('IDENTITY_VERSION',[data.versions]);
        ...
    }
}

```

---
#### Internationalization
1. **Fomatting date via dateFormat pipe**
```Javascript
{{ date | dateFormat }} // output '5:40 PM' 

{{ date | dateFormat:'longTime' }}  // output '5:40:22 PM GMT+8' 

{{ date | dateFormat: 'fullDate'}}  //output 'Friday, February 9, 2018'
 ```

 2. **Fomatting number via numberFormat pipe**
```Javascript
// input '201703.5416926'
{{ num | numberFormat }}
// output '201,703.542'

// input '1.149999'
{{ num | numberFormat }}
// output '1.150'

// input '2.31'
{{ num | numberFormat }}
// output '2.31'

// input '2017120107'
{{ num | numberFormat }}
// output '2,017,120,107'

// input '.23'
{{ num | numberFormat }}
// output '0.23'
 ```
3. **Fomatting currency via currencyFormat pipe**
```Javascript
//Usage
{{ value | currencyFormat [ : currencyCode ]  }}

// input '201703.5416926'
{{ num | currencyFormat }}
// output '$201,703.54'

// input '1.149999'
{{ num | currencyFormat: 'JPY' }}
// output '¥1'

// input '2.31'
{{ num | currencyFormat: 'CNY' }}
// output 'CN¥2.31'

// input '2017120107'
{{ num | currencyFormat: 'EUR' }}
// output '€2,017,120,107.00'
 ```

 4. **Fomatting percent via percentFormat pipe**
```Javascript
// input '0.1'
{{ num | percentFormat }}
// output '10%'

// input '0.1234'
{{ num | percentFormat }}
// output '12%'

// input '12345'
{{ num | percentFormat }}
// output '1,234,500%'
 ```

## Collect source strings

Angular-Client expose command to collect source strings
```Javascript
//add script in package.json
"scripts": {
    ...
    "collect:source-bundle": "collect-source-bundle --source-dir `pwd`/src --product ProductName --component UI --host https://singleton-service --version 1.0.0",
    ...
  }

 ```

 ```Javascript
    npm run collect:source-bundle  // collect source strings to Singleton Service
 ```

