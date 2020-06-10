---
title: "Integrate Singleton in Javascript App"
date: 2019-09-24T20:09:53+08:00
draft: false
weight: 20
---
```
This guideline demonstrates how to integrate Singleton JSClient with your frontend Framework on your application.

Singleton JSClient is a common lib providing I18n support theoretically for any frontend framework on your application. The client
will communicate with Singleton service to dynamically fetch translation and I18n pattern data for providing globalization support
in messages and formatting.

For I18n formatting, Singleton JSClient also provides I18n support for datetime format, number format, currency and plural.
Singleton JSClient formatting is based on CLDR data repository and keeps the same scope in supported locale set.
```

## Install
```Javascript
npm install @singleton-i18n/js-core-sdk --save 
 ```

## Integration
* **Create Init method**
```
Before integration with Singleton JSClient, it needs to create init method using initialization of basic configuration
regarding your application, such as productID, version and so on. Example as below.
```
```js
//i18nClient.js
import { i18nClient as jsClient, getBrowserCultureLang, invalidParamater,PatternCategories } from "@singleton-i18n/js-core-sdk";
import { ENGLISH } from "./source.l10n";
const initI18nClient = () => {
    const currentLanguage = getBrowserCultureLang();
    jsClient.init({
      productID: "iReact",
      component: "UI",
      version: "1.0.0",
      isPseudo: true,
      host: "https://singleton-service:8090",
      language: currentLanguage,
      i18nScope: [PatternCategories.DATE, PatternCategories.NUMBER],
      sourceBundle: ENGLISH,
      httpOptions: {
        timeout: 3000,
        withCredentials: true,
      },
    });
    jsClient.paramError = invalidParamater;
    return jsClient;
  };
  export const i18nClient = initI18nClient();
```

* **Root loading point**

```
While integrating with Singleton, it needs to load data regarding translations and formating from Singleton Service,so it has to figure out
frontend framework root loading point where jsClient can fetch data ahead of pages rendering。
Generally speaking each frontend framework has a root loading file, for example, it is the app.module.ts file on Angular,
the index.js file on React.Here take Integration with React as example as below.
```
```js
//index.js
import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { i18nClient } from './i18n/i18nClient';

(async () => {
  await i18nClient.coreService.loadI18nData(); // loading data from Singleton Service
  ReactDOM.render(
    <App />,
    document.getElementById('root')
  );
})();
```

* **Util method**
```
After loading data, it needs to translate strings extracted or format level2 such as datetime through jsClient API as below example
```
```js
//I18nUtil.js
import { i18nClient } from "./i18nClient";
// create the translation method 'l10n' consumed by where to need translate
export const l10n = (key, args) => i18nClient.l10nService.getMessage(key, args);
// carete formating datetime method 'formatDate' consumed by datetime areas
export const formatDate = (date, pattern) =>  i18nClient.i18nService.formatDate(date, pattern);
```

* **Extract strings from jsx/js to source file**
```
For strings in .jsx/.js files which need to translate, just extract them into a source file by convention named with l10n.js or l10n.ts as extension.
```
```js
//source.l10n.js
export const ENGLISH = {
    LEARN : "Learn React",
    EDIT: "Edit <code>{0}</code> and save to reload.",
    PLURAL: "{0, plural,one {# day}other {# days}}",
}
```

* **Implement translation and formating datetime**
```
After extracting strings out into source file, it needs to replace strings using 'l10n' method above created.
```
```js
//App.js
import React from 'react';
import { l10n } from './i18n/I18nUtil';
function App(){
    return(
        <div className="App">
            <header className="App-header">
                <a>
                    {l10n('LEARN')}
                </a>
            </header>
        </div>
    );
}

```
## Collect Source Strings
```
Collecting source strings mean collecting all strings which are already extracted into *.l10n.js or *.l10n.ts files, Singleton Service will 
responsible for translating these strings into different languages according to products requirements.
Singleton JSClient also provides CLI to collect source strings, you just need to configure script in frontend project package.json file as below.
```
```js
//package.json
"scripts":{
    "sollect:source-bundle": collect-source-bundle
                            --source-dir `pwd`/src/i18n
                            --product 'iReact'
                            --version '1.0.0'
                            --component 'UI'
                            --host "https://singleton-service:8090"
    }
```
