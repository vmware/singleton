# Prerequisites
- Build and run the [Singleton Service](https://github.com/vmware/singleton).

> Note:This sample application has been preconfigured to work with default Singleton service settings. Change the configurations in [i18nHandler.js](https://github.com/vmware/singleton/tree/g11n-js-client/samples/server/express/i18n/i18nHandler.js) as needed.

> Note:Language and Region can be changed by modifying the 'currentLanguage' and 'currentRegion' variables in  [i18nHandler.js](https://github.com/vmware/singleton/tree/g11n-js-client/samples/server/express/i18n/i18nHandler.js). Currently, only Chinese and English are supported in this sample application.

- Build the Singleton [JS client library](https://github.com/vmware/singleton/tree/g11n-js-client). The JS client library's location is preconfigured in this sample app's package.json. Change as needed.
```
"@singleton-i18n/js-core-sdk-server": "file:../../../singleton-i18n-js-core-sdk-server-0.5.7.tgz"
```

## Install 
```
$ npm i
```

# Run the sample application

```
$ npm start
```

open: http://localhost:8000