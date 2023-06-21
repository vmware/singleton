## Singleton Library for Javascript Clients

Singleton for JavaScript Client is common lib developed by VMware G11n team to provide i18n support for JavaScript framework. The Client will communicate with Singleton server for fetching translation and i18n patterns. Besides Localization, JavaScript Client also provides I18n support for Datetime format, number format, currency and percent. JavaScript I18n service is based on CLDR data repository and keep the same scope in supported locales set.

###  Prerequisites

Run the Singleton service by following the instructions in [here](https://github.com/vmware/singleton/blob/master/README.md).

Ensure the following are installed and compatible with ES2015:

- [Git](https://git-scm.com/downloads)
- [Node.js](https://nodejs.org/en/download/package-manager/) 10

> The lib works both for the browser and as a Node.js module which using ES2015 syntax.

###  How to build and use the client library

Clone the repository using Git.

```
git clone git@github.com:vmware/singleton.git g11n-js-client
```

Go to the project's root directory.

```
cd g11n-js-client
```

Checkout the client library branch

```
git checkout g11n-js-client
```

Download dependencies

```
npm install
```

Package the client library

> Note: The client library has two package.json files, the default is package.json to generate the package available in the Node environment, and the other is package.client.json which will generate the package used in the browser environment.

```
npm pack
```

The library will be packaged in the same directory (eg. singleton-i18n-js-core-sdk-server-0.5.7.tgz)

Import the library in your ES2015 application

```
cd <root path of your app>
npm install <path-to-location-of-library-in-previous-step/singleton-i18n-js-core-sdk-server-0.5.7.tgz>
```

Configure your main module file :

```js
const singletonCore = require('@singleton-i18n/js-core-sdk-server');
const bundle = require('./source.l10n');

module.exports.handle = (req, res, next) => {
    let currentLanguage = 'zh-Hans';
    let currentRegion = 'CN'
    let i18nClient = singletonCore.i18nClient.createInstance(
        {
            productID: 'CoreSDK',
            version: '1.0.0',
            component: 'ui',
            host: 'http://localhost:8091',
            language: currentLanguage,
            region: currentRegion,
            sourceBundles: [ bundle.ENGLISH ],
            i18nScope: [
                singletonCore.PatternCategories.DATE,
                singletonCore.PatternCategories.NUMBER,
                singletonCore.PatternCategories.CURRENCIES
            ],
            isPseudo: false,
        }
    );
    i18nClient.coreService.loadI18nData(
        () => {
            req.t = (key, args) => {
                return i18nClient.l10nService.getMessage(key, args);
            };
            req.formatDate = (value, pattern) => {
                return i18nClient.i18nService.formatDate(value, pattern);
            };
            req.formatPercent = (value) => {
                return i18nClient.i18nService.formatPercent(value);
            };
            req.formatNumber = (value) => {
                return i18nClient.i18nService.formatNumber(value);
            };
            req.formatCurrency = (value, currencyCode) => {
                return i18nClient.i18nService.formatCurrency(value,currencyCode);
            };
            next();
        }
    )
}
```

- You may now use the Singleton client library in your Angular application.

###  Sample code

> Now this sample application is running in the Node environment and using the Express Framework, the Browser environment sample will be updated soon.

```js
const express = require('express');
const i18nHandler = require('./i18n/i18nHandler')

const app = express();
const port = process.env.PORT || 8000;

app.use(i18nHandler.handle);

app.get('/', (req, res) => {
  let _Thead=`<thead><tr><th>Type</th><th>Input</th><th>Output</th></tr></thead>`
  let _Tbody=`<tbody>
    <tr align='center'><td>Datetime</td><td>${new Date()}</td><td>${req.formatDate(new Date(),'medium')}</td></tr>
    <tr align='center'><td>Percent</td><td>0.5569</td><td>${req.formatPercent('0.5569')}</td></tr>
    <tr align='center'><td>Number</td><td>1.2345</td><td>${req.formatNumber('1.2345')}</td></tr>
    <tr align='center'><td>Currency</td><td>2.5445</td><td>${req.formatCurrency('2.5445','USD')}</td></tr>
  </tbody>`

  res.send(`${req.t('title')}</br></br><table border='1' width="60%">${_Thead}${_Tbody}</table>`);
});

app.listen(port, (err) => {
  console.log(`Server is listening on port ${port}`);
});

```

###  Existing features

####  I18n Service

formatDate:

```
/**
- Format date time string according to current language
- and region. Timezone uses host system settings as default value
- @param {string | number | Date} date The date to format, as a Date, or milliseconds
- @param {string} pattern The definition of the format
  */
i18nClient.i18nService.formatDate(date: any, pattern: string="mediumDate", timezone?: string); 

```

formatNumber:

```
/**
- Transform a number to a locale string based on the pattern
- @param {number} value The number to format
*/
i18nClient.i18nService.formatNumber(value: any,language?: string); 

```

formatPercent:

```
/**
- Transform a decimal to a locale percentage based on the pattern
- @param {decimal} value The decimal to format
*/
i18nClient.i18nService.formatPercent(value: any);

```

formatCurrency:

```
/**
- Transform a number to a number with a currency symbol according to the currencyCode
- @param {number} value The number to format
- @param {string} currencies symbol
- @param {string = 'USD'} currencies symbol
*/ 
i18nClient.i18nService.formatCurrency(value: any, currencyCode: string = 'USD'); 

```

####  L10N Service

getMessage:

```
/**
- Get localized message from the cache according to the current language
- @param {string} key the key refer to the message
- @param {string[]} args the values of interpolation variables
*/
i18nClient.l10nService.getMessage(key: string, args?: any[] | {});

```

### Sample application

A sample application is provided [here](https://github.com/vmware/singleton/tree/g11n-js-client/samples/server/express).

> Note: If you want to run the sample application, you must start the Singleton service [here](https://github.com/vmware/singleton/blob/master/README.md). Please use HTTP in the test environment, the port number should be modified according to the actual situation.

