import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';
import { ExcelReader } from '../utils/excelreader';
import * as CommonActions from '../utils/commonactions'
import { EnglishBundle } from '../resources/EnglishBundle';

debugger

let config = { ...Consts.config, i18nScope: [CoreSDK.PatternCategories.PLURAL, CoreSDK.PatternCategories.NUMBER]};

describe('Format Plural', () => {
    var lastLanguage : string = '';
    var lastRegion = '';

    var testdata;
    var datafile = `${__dirname}` + '/../data/pluraldata.xlsx';
    testdata = new ExcelReader(datafile).readsheet_with_sheetname('data_lan&reg');
    console.log('Total case number in file is: ' + (testdata.length));

    testdata.forEach(item => {
        let language = (item as any)['language'];
        let region = (item as any)['region'];
        let category = (item as any)['category'];
        let number = (item as any)['number'];
        let description = (item as any)['description'];

        it("lang: " + language + "; region: " + region + "; number: '" + number + "'; category: " + category + "; desc: " + description, async function () {
            let argument = (item as any)['argument'];
            let expected = (item as any)['expected'];

            // console.log("data to test:\n "+JSON.stringify(item));

            var currentLanguage = (item as any)['language'];
            if (lastLanguage != currentLanguage || lastRegion != region) {
                let newConfig;
                if (CommonActions.isDefined(region)) {
                    newConfig = { ...config, sourceBundle: EnglishBundle, language: currentLanguage, region: region };
                }
                else {
                    newConfig = { ...config, sourceBundle: EnglishBundle, language: currentLanguage };
                }
                CoreSDK.i18nClient.init(newConfig);
                await CoreSDK.i18nClient.coreService.loadI18nData();
                lastLanguage = currentLanguage;
                lastRegion = region;
            }

            var actualdata = CoreSDK.i18nClient.l10nService.getTranslation(Consts.TranslationResource.key_plural, Consts.TranslationResource.source_plural, { files: number, place: argument });
            expect(actualdata).toEqual(expected);

            var actualdata1 = CoreSDK.i18nClient.l10nService.getMessage(Consts.TranslationResource.key_plural, { files: number, place: argument });
            expect(actualdata1).toEqual(expected);
        });
    });
});


//todo:  //test files parameter
    // test place parameter

