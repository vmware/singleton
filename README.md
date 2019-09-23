Singleton Library for Angular Clients
============

Several client libraries are provided to support sending API requests to Singleton service. Each library, when integrated on the client side, also provides additional features such as post-processing of localized resources and caching.

Below are details on how to use the library for Angular 7 clients.

Prerequisites
------------
 * Run the Singleton service by following the instructions in [here](https://github.com/vmware/singleton/blob/master/README.md).
 * Ensure the following are installed and compatible with Angular 7:     
    - [Git](https://git-scm.com/downloads)
    - [Node.js](https://nodejs.org/en/download/package-manager/) 10

How to build and use the client library
------------
 * Clone the repository using Git.
    ```
    git clone git@github.com:vmware/singleton.git g11n-angular-client
    ```
 * Go to the project's root directory.
    ```
    cd g11n-angular-client
    ```
 * Checkout the client library branch
    ```
    git checkout g11n-angular-client
    ```
 * Download dependencies
   ```
   npm install
   ```
 * Package the client library
    ```
    npm pack
    ```
    The library will be packaged in the same directory (eg. singleton-i18n-angular-client-0.1.0.tgz)
 * Import the library in your Angular 7 application
   ```
   cd <root-of-your-app>
   npm install <path-to-location-of-library-in-previous-step/singleton-i18n-angular-client-0.1.0.tgz>
   ```
 * Configure your source bundle :
   ```ts
   export const ENGLISH = {
      'application.title': 'Welcome to Singleton Angular sample application!',
      'demo.string.description': 'Singleton Angular client supports both {0} and {1}.',
      'demo.plural.users': '{0, plural, one {Singleton Angular client has a user.} other {Singleton Angular client has # users.}}'
   };
   ``` 
 * Configure your main module file (app.module.ts) : 
   ```ts
   ...
   import { NgModule, APP_INITIALIZER } from '@angular/core';
   import { HttpClientModule } from '@angular/common/http';
   import { VIPModule, VIPService, LocaleService, PatternCategories, getBrowserCultureLang } from '@singleton-i18n/angular-client';
   import { ENGLISH } from './app.l10n';

   export function initVIPConfig(service: VIPService, localeService: LocaleService) {
      // Get the browser's language by replacing 'es' with getBrowserCultureLang()
      localeService.init('es');
      return () => service.initData({
         productID: 'SingletonSample',
         component: 'default',
         version: '1.0.0',
         i18nScope: [
            PatternCategories.DATE,
            PatternCategories.NUMBER,
            PatternCategories.CURRENCIES
         ],
         host: 'https://localhost:8090/',
         isPseudo: false,
         collectSource: false,
         sourceBundle: ENGLISH,
         timeout: 5000
      });
   }
    
   @NgModule({
      imports: [
         ...
         HttpClientModule,
         VIPModule.forRoot(),
         ...
      ],
      providers: [
         ...
         {
            provide: APP_INITIALIZER,
            useFactory: initVIPConfig,
            deps: [
               VIPService,
               LocaleService
            ],
            multi: true
         }
         ...
      ],
      bootstrap: [AppComponent]
   })
   export class AppModule {}
   ``` 
   > Note: The settings above have been preconfigured to work with default Singleton service settings (See Prerequisites). Change the properties inside service.initData as needed.
   Locale has been preconfigured to 'es' to demonstrate Spanish translation. Change it to get the browser's locale by replacing 'es' with getBrowserCultureLang().
 
   
Sample code
------------

L10nPlus Pipe:
```html
{{ 'application.title'| vtranslate }}
// output: Welcome to Singleton Angular sample application!

{{ 'demo.plural.users'| vtranslate: 1 }}
// output: Singleton Angular client has a user.
```

L10n Directive:
```html
<span l10n='application.title'></span>
// output: Welcome to Singleton Angular sample application!
```

Date Time Formatting Pipe:
```
// component.ts: this.date = new Date();
{{ date | dateFormat: 'medium'}} // output: 'Jul 23, 2019, 2:41:24 PM'
```

Number Formatting Pipe:
```
{{ 123456789 | numberFormat }} // output: '123,456,789'
```

Currency Formatting Pipe:
```
{{ 1.149999 | currencyFormat: 'JPY' }} // output: '¥1'
```

Percent Formatting Pipe:
```
{{ 0.1 | percentFormat }} // output: '10%'
```
 
Existing features
------------
* I18n data initialization
  * load data before application start
  * load data async
* Locale management
* Get translation
  * l10n pipe
  * l10n directive
  * l10n service
* Formatting strings
  * date formatting pipe
  * number formatting pipe
  * currency formatting pipe
  * I18n service
* Lazy loading compliant
* Share module support
* Independent module support (Angular lib support)
* Source collection
* Stream APIs
* Offline mode support
* Loader customization
 
Upcoming features 
------------
 * 
 
Request for contributions from the community
------------
 * 
    
Sample application
------------
 * A sample application is provided [here](https://github.com/vmware/singleton/tree/g11n-angular-client/sample)
   > Note: To ensure correct UI display, please run the [Singleton service](https://github.com/vmware/singleton/blob/master/README.md) locally and configure its URL in your app.module.ts file (eg. host: 'https://localhost:8090/').

