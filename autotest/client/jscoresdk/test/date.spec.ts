import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';
import { ExcelReader } from '../utils/excelreader';
import { DateTimeFormat } from '../utils/constants';
import * as CommonActions from '../utils/commonactions';

debugger

let config = { ...Consts.config, i18nScope: [CoreSDK.PatternCategories.DATE] };

var lastLanguage = null;
var lastRegion = null;

describe('Format Datetime', () => {
    var datafile = `${__dirname}` + '/../data/datedata.xlsx';
    let testdata = new ExcelReader(datafile).readsheet_with_sheetindex(0);
    console.log('Total case number in file is: ' + (testdata.length));

    for (const i in testdata) {
        (function (i) {
            var item = testdata[i];
            // console.log("data to test:\n " + JSON.stringify(item));
            let region = item['region'];
            it("language: " + item['language'] + " |region: " + region + "|input: " + item['input'] + "|timezone: " + item['timezone'] + "|pattern: " + item['pattern'] + "|desc: " + item['description'], async () => {
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

                let input = item['input'];
                let timezone = item['timezone'];
                let pattern = item['pattern'];
                let expected = item['expected'];

                if (pattern == DateTimeFormat.default) {
                    pattern = undefined;
                }
                if (item['error']) {
                    if (CommonActions.isDefined(timezone)) {
                        expect(() => {
                            CoreSDK.i18nClient.i18nService.formatDate(input, pattern, timezone);
                        }).toThrowError(item['error']);
                    }
                    else {
                        expect(() => {
                            CoreSDK.i18nClient.i18nService.formatDate(input, pattern);
                        }).toThrowError(item['error']);
                    }
                }
                else {
                    if (CommonActions.isDefined(timezone)) {
                        expect(CoreSDK.i18nClient.i18nService.formatDate(input, pattern, timezone)).toEqual(expected);
                    }
                    else {
                        expect(CoreSDK.i18nClient.i18nService.formatDate(input, pattern)).toEqual(expected);
                    }
                }
            });
        }(i));
    }
});

describe('Format Datetime - negative | pattern medium', () => {
    let input = '2018-01-11T09:03:01+0000';
    let pattern = 'medium';

    beforeAll(async () => {
        let currentLanguage = Consts.SupportedLanguage.zhcn;
        let newConfig = { ...config, language: currentLanguage };
        CoreSDK.i18nClient.init(newConfig);
        await CoreSDK.i18nClient.coreService.loadI18nData();
    });

    it("Format Datetime with value as ''", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatDate('', pattern); }).toThrowError("InvalidParamater: 'Invalid date '' for 'formatDate''");
    });
    it("Format Datetime with value as undefined", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatDate(undefined, pattern); }).toThrowError("InvalidParamater: 'Invalid date 'undefined' for 'formatDate''");
    });
    it("Format Datetime with value as null", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatDate(null, pattern); }).toThrowError("InvalidParamater: 'Invalid date 'null' for 'formatDate''");
    });

    //this should have been included in the above suite.
    it("Format Datetime with value as '" + input + "' and pattern as ''", function () {
        var actualdata = CoreSDK.i18nClient.i18nService.formatDate(input, '');
        expect(actualdata).toEqual('');
    });
    it("Format Datetime with value as '" + input + "' and pattern as 'undefined'", function () {
        var actualdata = CoreSDK.i18nClient.i18nService.formatDate(input, undefined);
        expect(actualdata).toEqual('2018年1月11日');
    });
    it("Format Datetime with value as '" + input + "' and pattern as 'null'", function () {
        var actualdata = CoreSDK.i18nClient.i18nService.formatDate(input, null);
        expect(actualdata).toEqual('');
    });
    it("Format Datetime with value as '" + input + "' and pattern as '" + pattern + "' and timezone as 'null'", function () {
        var actualdata = CoreSDK.i18nClient.i18nService.formatDate(input, pattern, null);
        expect(actualdata).toEqual('2018年1月11日 下午5:03:01');
    });
});


