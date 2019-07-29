import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';
import { ExcelReader } from '../utils/excelreader';
import * as CommonActions from '../utils/commonactions';

debugger

let config = { ...Consts.config, i18nScope: [CoreSDK.PatternCategories.CURRENCIES, CoreSDK.PatternCategories.NUMBER] };

var lastLanguage = null;
var lastRegion = null;
describe('Format Currency', () => {
  var datafile = `${__dirname}` + '/../data/currencydata.xlsx';
  let testdata = new ExcelReader(datafile).readsheet_with_sheetindex(0);
  console.log('Total case number in file is: ' + (testdata.length));
  for (const i in testdata) {
    (function (i) {
      var item = testdata[i];
      let region = item['region'];
      it("language: " + item['language'] + " |region: " + region + "|input: " + item['input'] + " " + item['input2'] + "|feature: " + item['feature'] + "|description: " + item['description'], async function () {
        // console.log("data to test:\n "+JSON.stringify(item));

        var currentLanguage = item['language'];
        if (currentLanguage != lastLanguage || lastRegion != region) {
          let newConfig;
          if (CommonActions.isDefined(region)) {
            newConfig = { ...config, language: currentLanguage, region: region };
          }
          else {
            newConfig = { ...config, language: currentLanguage };
          }
          CoreSDK.i18nClient.init(newConfig);
          await CoreSDK.i18nClient.coreService.loadI18nData();
          lastLanguage = currentLanguage;
          lastRegion = region;
        }
        if (item['error']) {
          expect(() => {
            CoreSDK.i18nClient.i18nService.formatCurrency(item['input'], item['input2']);
          }).toThrowError(item['error']);
        }
        else {
          var actualdata = CoreSDK.i18nClient.i18nService.formatCurrency(item['input'], item['input2']);
          expect(actualdata).toEqual(item['expected']);
        }
      });
    }(i));
  }
});

describe('Format Currency - negative', () => {
  let currencyCode = 'CNY';
  let currencyNumber = '100';

  beforeAll(async () => {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    let newConfig = { ...config, language: currentLanguage };
    CoreSDK.i18nClient.init(newConfig);
    await CoreSDK.i18nClient.coreService.loadI18nData();
  });

  it("Get currency with value as ''", function () {
    expect(() => { CoreSDK.i18nClient.i18nService.formatCurrency(''); }).toThrowError("InvalidParamater: 'Invalid number '' for 'formatCurrency''");
  });
  it("Get currency with value as 'undefined' and currency code as 'CNY'", function () {
    expect(() => { CoreSDK.i18nClient.i18nService.formatCurrency(undefined, currencyCode); }).toThrowError("InvalidParamater: 'Invalid number 'undefined' for 'formatCurrency''");
  });
  it("Get currency with value as 'null'", function () {
    expect(() => { CoreSDK.i18nClient.i18nService.formatCurrency(null, currencyCode); }).toThrowError("InvalidParamater: 'Invalid number 'null' for 'formatCurrency''");
  });
  it("Get currency with value as '" + currencyNumber + "' and currency code as ''", function () {
    var actualdata = CoreSDK.i18nClient.i18nService.formatCurrency(currencyNumber, '');
    expect(actualdata).toEqual('US$100.00');
  });
  it("Get currency with value as '" + currencyNumber + "' and currency code as 'undefined'", function () {
    var actualdata = CoreSDK.i18nClient.i18nService.formatCurrency(currencyNumber, undefined);
    expect(actualdata).toEqual('US$100.00');
  });
  it("Get currency with value as '" + currencyNumber + "' and currency code as 'null'", function () {
    var actualdata = CoreSDK.i18nClient.i18nService.formatCurrency(currencyNumber, null);
    expect(actualdata).toEqual('US$100.00');
  });
});