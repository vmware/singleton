import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';
import { ExcelReader } from '../utils/excelreader';
import * as CommonActions from '../utils/commonactions';

debugger

let config = { ...Consts.config, i18nScope: [CoreSDK.PatternCategories.NUMBER] };

describe('Format Percent', () => {
    var datafile = `${__dirname}` + '/../data/percentdata.xlsx';
    let testdata = new ExcelReader(datafile).readsheet_with_sheetindex(0);
    console.log('Total case number in file is: ' + (testdata.length));
    for (const i in testdata) {
        (function (i) {
            var item = testdata[i];
            let region = item['region'];
            it("language: " + item['language'] + " |region: " + region + " input: " + item['input'] + " description: " + item['description'], async function () {
                console.log(i+": data to test:\n "+JSON.stringify(item));

                var currentLanguage = item['language'];
                let newConfig;
                if (CommonActions.isDefined(region)) {
                    newConfig = { ...config, language: currentLanguage, region: region };
                }
                else {
                    newConfig = { ...config, language: currentLanguage };
                }
                console.log('Start to initialize I18N service...');
                CoreSDK.i18nClient.init(newConfig);
                await CoreSDK.i18nClient.coreService.loadI18nData();
                console.log('End to initialize I18N service...');

                if (item['error']) {
                    expect(() => {
                        CoreSDK.i18nClient.i18nService.formatPercent(item['input']);
                    }).toThrowError(item['error']);
                }
                else {
                    var actualdata = CoreSDK.i18nClient.i18nService.formatPercent(item['input']);
                    expect(actualdata).toEqual(item['expected']);
                }
            });
        }(i));
    }
});


describe('Format Percent - negative', () => {
    let currentLanguage = Consts.SupportedLanguage.zhcn;

    beforeAll(async () => {
        console.log('beforeAll');
        console.log('Start to initialize I18N service...');
        let newConfig = { ...config, language: currentLanguage };
        CoreSDK.i18nClient.init(newConfig);
        await CoreSDK.i18nClient.coreService.loadI18nData();
        console.log('End to initialize I18N service...');
        console.log('End beforeAll');
    });

    it("Format Percent with value as ''", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatPercent(''); }).toThrowError("InvalidParamater: 'Invalid number '' for 'formatPercent''");
    });
    it("Format Percent with value as 'undefined'", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatPercent(undefined); }).toThrowError("InvalidParamater: 'Invalid number 'undefined' for 'formatPercent''");
    });

    it("Format Percent with value as 'null'", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatPercent(null); }).toThrowError("InvalidParamater: 'Invalid number 'null' for 'formatPercent''");
    });
});