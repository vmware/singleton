# Prerequisites

- Build and run the [Singleton Service](https://github.com/vmware/singleton).

> Note:This sample application has been preconfigured to work with default Singleton service settings. Change the configurations in [i18n.utils.js](https://github.com/vmware/singleton/tree/g11n-js-client/samples/client/src/i18n.utils.js) as needed.

> Note:Language and Region can be changed by adding the locale in the cookie with name 'language' (https://github.com/vmware/singleton/tree/g11n-js-client/samples/client/src/i18n.utils.js). Currently, only Chinese and English are supported in this sample application.

- Build the Singleton [JS client library](https://github.com/vmware/singleton/tree/g11n-js-client).
  By default the core sdk server is build so you need to replace the package.json with package.client.json in order to build the core SDK client. The library's location is preconfigured in this sample app's package.json. Change as needed.

- You can load the translation by two ways, either from the Singleton service or from your predefined sources. If you want to use custom sources you have to add them in the src/translations
  with the following format: translation_locale.json and uncomment the code in the src/i18n.utils.js on row 30. The src/translations/translation_zh.json is example. 
```
"@singleton-i18n/js-core-sdk": "file:../../../singleton-i18n-js-core-sdk-0.5.7.tgz"
```

## Install

```
$ npm i
```

#Build

```
$ npm run build
```

# Run the sample application

```
$ npm start
```

open: http://localhost:3000/example

# In the dev tools terminal change the lang

```
$ document.cookie = "vcs_locale=zh"
```
