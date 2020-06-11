Singleton I18n Level2 Common Library 
======================================

Singleton I18n Level2 Common Library are consumed by Angular Clients and Javascript Clients. 
It provides functions for DateTimeFormat, NumberFormat and PluralRules objects.

Below are details on how to use the common library for Angular clients and Javascript clients.

Prerequisites
------------
 * Run the Singleton service by following the instructions in [here](https://github.com/vmware/singleton/blob/master/README.md).
 * Ensure the following are installed and compatible with Angular 7:     
    - [Git](https://git-scm.com/downloads)
    - [Node.js](https://nodejs.org/en/download/package-manager/) 10

How to build and use the common library
------------
 * Clone the repository using Git.
    ```
    git clone git@github.com:vmware/singleton.git g11n-js-sdk
    ```
 * Go to the project's root directory.
    ```
    cd g11n-js-sdk
    ```
 * Checkout the client library branch
    ```
    git checkout g11n-js-sdk
    ```
 * Download dependencies
   ```
   npm install
   ```

   Note：The above steps are used to reuse the source code. 
   The stepscould be skipped if user only wants to use the common library for Angular or Javascript applications.

 * Import the library in your Angular or Javascript application
   ```
   cd <root-of-your-app>

   open package.json file and add the following entry to dependencies section.
      "@singleton-i18n/g11n-js-sdk": "^0.1.0"

   Then run the following command:
      npm install 

   
 * Sample code:
   ```

   import { I18n } from '@singleton-i18n/g11n-js-sdk';

   ...

   // DatetimeFormat

   I18n.registerLocaleData('en', sourcePatternData.categories);

   const dt_options: I18n.DateTimeFormatOptions = {pattern: 'full'};

   const date = new Date(2019, 2, 22, 9, 3, 1, 550);

   // const formatter = new I18n.DateTimeFormat('en', dt_options);

   const IntlDate = I18n.DateTimeFormat.getInstance('en',options);

   const formattedDate = IntlDate.format(date);

   // NumberFormat

   I18n.registerLocaleData('de', dePatternData.categories);

   const numberFormater = (value: any, type: string, locale: string) => {

      if (type === 'currencies') {

         const currencyCode = locale === 'en' ? 'USD' : 'EUR';

         const option: I18n.NumberFormatOptions = {numberFormatType: type, currencyCode: currencyCode};

         const numberFormat = I18n.NumberFormat.getInstance(locale,option);

         return numberFormat.format(value);

      } else if (type === 'plural') {

         const option: I18n.NumberFormatOptions = {numberFormatType: type, currencyCode: ''};
         const numberFormat = I18n.NumberFormat.getInstance(locale,option);

         return numberFormat.format(value);

      } else {

         const option: I18n.NumberFormatOptions = {numberFormatType: type, currencyCode: ''};

         const numberFormat = I18n.NumberFormat.getInstance(locale,option);

         return numberFormat.format(value);
      }
   };

   const dec = numberFormater(12345, 'decimal', 'de');

   const per = numberFormater(0.123, 'percent', 'de');

   const plu = numberFormater(12345, 'plural', 'de');

   const cur = numberFormater(0.123, 'currencies', 'de');

   // PluralRules

   const pluralDE = I18n.PluralRules.getInstance('de');

   I18n.registerLocaleData('de', dePatternData.categories);
   
   const pluralFormat = pluralDE.select(1)

   // For detailed code examples, please refer the unit test cases under test folder.
   
   ```
 

