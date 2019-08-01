import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants'
import { ExcelReader } from '../utils/excelreader'
import * as CommonActions from '../utils/commonactions'

// debugger

jest.setTimeout(10000);

let config = { ...Consts.config, i18nScope: [CoreSDK.PatternCategories.NUMBER] };

describe('Format Number', () => {
    var datafile = `${__dirname}` + '/../data/numberdata.xlsx';
    let testdata = new ExcelReader(datafile).readsheet_with_sheetindex(0);
    console.log('Total case number in file is: ' + (testdata.length));
    for (const i in testdata) {
        // if (Number.parseInt(i) > 1) {continue;}
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
                        CoreSDK.i18nClient.i18nService.formatNumber(item['input'], currentLanguage);
                    }).toThrowError(item['error']);
                }
                else {
                    var actualdata = CoreSDK.i18nClient.i18nService.formatNumber(item['input'], currentLanguage);
                    // console.log('actual: ');
                    // console.log(CommonActions.toUTF8Array(actualdata));
                    // console.log('expected: ');
                    // console.log(CommonActions.toUTF8Array(item['expected']));
                    expect(actualdata).toEqual(item['expected']);
                }
            });
        }(i));
    }
});


let suitename = 'Format Number - negative';
describe(suitename, () => {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    
    console.log(suitename);

    beforeAll(async () => {
        console.log('beforeAll');
        console.log('Start to initialize I18N service...');
        let newConfig = { ...config, language: currentLanguage };
        CoreSDK.i18nClient.init(newConfig);
        await CoreSDK.i18nClient.coreService.loadI18nData();
        console.log('End to initialize I18N service...');

        console.log('End beforeAll');

    });


    it("Format Number with number as ''", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatNumber(''); }).toThrowError("InvalidParamater: 'Invalid number '' for 'formatNumber''");
    });
    it("Format Number with number as 'undefined'", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatNumber(undefined); }).toThrowError("InvalidParamater: 'Invalid number 'undefined' for 'formatNumber''");
    });
    it("Format Number with number as 'null'", function () {
        expect(() => { CoreSDK.i18nClient.i18nService.formatNumber(null); }).toThrowError("InvalidParamater: 'Invalid number 'null' for 'formatNumber''");
    });

    // let input = '-1000.053456';
    // let anotherLanguage = Consts.SupportedLanguage.de;
    // it("Format Number with language as 'undefined'", function () {
    //     var actualdata = CoreSDK.i18nClient.i18nService.formatNumber(input, undefined);
    //     expect(actualdata).toEqual('?');
    // });
    // it("Format Number with language different from current language", function () {
    //     var actualdata = CoreSDK.i18nClient.i18nService.formatNumber(input, anotherLanguage);
    //     expect(actualdata).toEqual('?');
    // });
});

