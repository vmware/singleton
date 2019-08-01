import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants'
import PatternData_de from '../resources/patterndata_de';
import PatternData_zh from '../resources/patterndata_zh-Hans';
import * as StringUtil from '../utils/stringutil';

describe('Language only - part1', () => {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    let i18nClient = CoreSDK.i18nClient.createInstance({ ...Consts.config, language: currentLanguage });

    beforeAll( () => {
        i18nClient.coreService.setPatterns(PatternData_de.categories, currentLanguage);
    });

    it('Test number ', function () {
        let input = '1000.053456';
        let expected = '1.000,053';

        var actualdata = i18nClient.i18nService.formatNumber(input);
        expect(actualdata).toEqual(expected);
    });

    it('Test percent ', function () {
        let input = '10.114';
        let expected = '1.011 %';

        var actualdata = i18nClient.i18nService.formatPercent(input);
        expect(actualdata).toEqual(expected);
    });


    it('Test date ', function () {
        let input = '2018-02-22T09:03:01+0000';
        let expected = 'Donnerstag, 22. Februar 2018 um 17:03:01 GMT+08:00';

        var actualdata = i18nClient.i18nService.formatDate(input, Consts.DateTimeFormat.FULL);
        expect(actualdata).toEqual(expected);
    });


    it('Test plural', function () {
        let num = 1.123;
        let argument = '桌子';
        //category other - {place}上有 # 文件。
        // let expected = "category other - " + argument + "上有 " + "1,123" + " 文件。";
        let expected = "abc";

        // await i18nClient.coreService.loadI18nData();
        let actualValue = i18nClient.l10nService.getTranslation(Consts.TranslationResource.key_plural, expected, { files: num, place: argument });
        expect(actualValue).toEqual(expected);
    });
});

describe('Language & Region', () => {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    let region = 'de';
    let i18nClient = CoreSDK.i18nClient.init({ ...Consts.config, language: currentLanguage, region: region });

    beforeAll( () => {
        i18nClient.coreService.setPatterns(PatternData_de.categories, currentLanguage, region);
    });


    it('Test number ', function () {
        let input = '1000.053456';
        let expected = '1.000,053';

        var actualdata = i18nClient.i18nService.formatNumber(input);
        expect(actualdata).toEqual(expected);
    });

    it('Test percent ', function () {
        let input = '10.114';
        let expected = '1.011 %';

        var actualdata = i18nClient.i18nService.formatPercent(input);
        expect(actualdata).toEqual(expected);
    });


    it('Test date ', function () {
        let input = '2018-02-22T09:03:01+0000';
        let expected = 'Donnerstag, 22. Februar 2018 um 17:03:01 GMT+08:00';

        var actualdata = i18nClient.i18nService.formatDate(input, Consts.DateTimeFormat.FULL);
        expect(actualdata).toEqual(expected);
    });


    it('Test plural', function () {
        let num = 1.123;
        let argument = '桌子';
        //category other - {place}上有 # 文件。
        // let expected = "category other : " + argument + "上有 " + "1,123" + " 文件。";
        let expected = "abc";

        //todo: 没有load resource，怎么还可以获取正确结果？
        let actualValue = i18nClient.l10nService.getTranslation(Consts.TranslationResource.key_plural, expected, { files: num, place: argument });
        expect(actualValue).toEqual(expected);
    });

    describe('Test Currency ', function () {
        let input = '-20180903.001';
        let currencyType = 'DEM';

        it('Set to another(not current) language', async function () {
            i18nClient.init({ ...Consts.config, language: Consts.SupportedLanguage.zhcn, region: region });
            await i18nClient.coreService.loadI18nData();
            
            i18nClient.coreService.setPatterns(PatternData_zh.categories, Consts.SupportedLanguage.zhcn, region);

            i18nClient.coreService.setPatterns(PatternData_de.categories, Consts.SupportedLanguage.ja, region);

            let expected_old = '-DEM20,180,903.00';
            var actualdata_ja = i18nClient.i18nService.formatCurrency(input, currencyType);
            expect(actualdata_ja).toEqual(expected_old);
        });

        it('Set to current language', async function () {
            let currentLanguage = Consts.SupportedLanguage.zhTW;
            i18nClient.init({ ...Consts.config, language: currentLanguage, region: region });
            await i18nClient.coreService.loadI18nData();

            i18nClient.coreService.setPatterns(PatternData_de.categories, currentLanguage, region);

            let expected_new = '-20.180.903,00 pattern_de';
            var actualdata = i18nClient.i18nService.formatCurrency(input, currencyType);
            expect(actualdata).toEqual(expected_new);
        });
    });
});

describe('Language only - part2', () => {

    it('Test Currency - Set to another language', async function () {
        let currentLanguage = Consts.SupportedLanguage.zhcn;
        let i18nClient = CoreSDK.i18nClient.createInstance({ ...Consts.config, language: currentLanguage });
    
        let input = '-20180903.001';
        let currencyType = 'DEM';
        let expected_old = '-DEM20,180,903.00';

        i18nClient.init({ ...Consts.config, language: Consts.SupportedLanguage.zhcn });
        await i18nClient.coreService.loadI18nData().then( res => {
            // console.log(res);
        });

        //Set to another language
        i18nClient.coreService.setPatterns(PatternData_zh.categories, Consts.SupportedLanguage.zhcn);
        i18nClient.coreService.setPatterns(PatternData_de.categories, Consts.SupportedLanguage.ja);
        var actualdata_ja = i18nClient.i18nService.formatCurrency(input, currencyType);
        expect(actualdata_ja).toEqual(expected_old);

    });
    it('Test Currency - Set to current language', async function () {
        let currentLanguage = Consts.SupportedLanguage.zhTW;
        let i18nClient = CoreSDK.i18nClient.createInstance({ ...Consts.config, language: currentLanguage });
    
        let input = '-20180903.001';
        let currencyType = 'DEM';
        let expected_new = '-20.180.903,00 pattern_de';

        i18nClient.init({ ...Consts.config, language: currentLanguage });
        await i18nClient.coreService.loadI18nData();

        //Set to current language
        i18nClient.coreService.setPatterns(PatternData_de.categories, currentLanguage);
        var actualdata = i18nClient.i18nService.formatCurrency(input, currencyType);
        expect(actualdata).toEqual(expected_new);
    });
    it('Set Currency as empty(fallback to English)', async function () {
        let currentLanguage = Consts.SupportedLanguage.ko;
        let i18nClient = CoreSDK.i18nClient.createInstance({ ...Consts.config, language: currentLanguage });
    
        let input = '-20180903.001';
        let currencyType = 'DEM';
        let expected_new = '-DEM20,180,903.00';

        i18nClient.init({ ...Consts.config, language: currentLanguage });
        await i18nClient.coreService.loadI18nData();

        let newPatternData = {...PatternData_de.categories, currencies: {}};
        i18nClient.coreService.setPatterns(newPatternData, currentLanguage);
        var actualdata = i18nClient.i18nService.formatCurrency(input, currencyType);
        expect(actualdata).toEqual(expected_new);
    });
});


// describe('negative', () => {
//     beforeAll( () => {
//         let currentLanguage = 'zh';
//         let region = 'cn';

//         i18nClient.init({ ...Consts.config, language: currentLanguage, region: region });
//         await i18nClient.coreService.loadI18nData();
//         i18nClient.coreService.setPatterns(PatternData.categories, currentLanguage, region);
//     });

//     it('Test currency ', function () {

//         let input = '-20180903.001';
//         let currencyType = 'DEM';
//         let expected_new = '-20.180.903,00 DEM';

//         var actualdata = i18nClient.i18nService.formatCurrency(input, currencyType);
//         expect(actualdata).not.toEqual(expected_new);
//     });

// });
//todo : use exception data to set pattern('', undefined, null)