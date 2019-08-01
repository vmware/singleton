import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';
import { ExcelReader } from '../utils/excelreader';
import { EnglishBundle } from '../resources/EnglishBundle';
import * as Common from '../utils/commonactions';
import * as CommonActions from '../utils/commonactions'

debugger

let config = { ...Consts.config, i18nScope: [] };

describe('negative - UnsupportedLanguage', () => {
    let currentLanguage = Consts.UnsupportedLanguage.it;
    let i18nClient = CoreSDK.i18nClient.createInstance({ ...config, language: currentLanguage });

    beforeAll(async () => {
        await i18nClient.coreService.loadI18nData();
    });

    it("getTranslation, source is provided.", function () {
        let sourceForTest = 'sourceForTest';
        let expected = 'Message-en';
        let actualValue3 = i18nClient.l10nService.getTranslation(Consts.TranslationResource.key_onlystring, sourceForTest);
        expect(actualValue3).toEqual(expected);
    });

    it("getMessage by key, source isn't there.", function () {
        let actualValue4 = i18nClient.l10nService.getMessage(Consts.TranslationResource.key_onlystring);
        expect(actualValue4).toEqual('Message-en');
    });

    it("getMessage by key, source is ready.", async function () {
        i18nClient.init({ ...config, sourceBundle: EnglishBundle, language: currentLanguage });
        // await i18nClient.coreService.loadI18nData();
        let actualValue5 = i18nClient.l10nService.getMessage(Consts.TranslationResource.key_onlystring);
        expect(actualValue5).toEqual('Message-en');
    });
});

// it('Pesudo string', async () => {
//     const pseudoTag = '#@';

//     let currentLanguage = Consts.SupportedLanguage.zhcn;
//     const cliet = CoreSDK.i18nClient.init({ ...config, language: currentLanguage, isPseudo: true, sourceBundle: EnglishBundle });
//     await cliet.coreService.loadI18nData();
//     let actualValue = cliet.l10nService.getMessage(Consts.TranslationResource.key_onlystring);
//     expect(actualValue).toEqual(pseudoTag + "Message-latest" + pseudoTag);
// });

describe("getMessage - negative", function () {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    let i18nClient = CoreSDK.i18nClient.createInstance({ ...config, language: currentLanguage });

    beforeAll(async () => {
        await i18nClient.coreService.loadI18nData();
    });

    it("get by key, Key doesn't exist", function () {
        let actualValue41 = i18nClient.l10nService.getMessage(Consts.TranslationResource.key_nonexistent);
        expect(actualValue41).toEqual(Consts.TranslationResource.key_nonexistent);
    });

    it("get by key, source isn't there.", function () {
        let actualValue4 = i18nClient.l10nService.getMessage(Consts.TranslationResource.key_onlystring);
        expect(actualValue4).toEqual('消息');
    });

    it("get by key, source is ready.", async function () {
        i18nClient.init({ ...config, sourceBundle: EnglishBundle });
        await i18nClient.coreService.loadI18nData();
        let actualValue5 = i18nClient.l10nService.getMessage(Consts.TranslationResource.key_onlystring);
        expect(actualValue5).toEqual(Consts.TranslationResource.source_onlystring);
    });
});


describe('Get Source - positive', () => {
    it('Normal Get', () => {
        let key = 'key_to_get';
        let source = 'The message for getSource';
        CoreSDK.i18nClient.init({ ...config, sourceBundle: { key_to_get: source } });
        let actualSource: String = CoreSDK.i18nClient.l10nService.getSourceString(key);
        expect(actualSource).toEqual(source);
    });
});

describe('Get Source - negative', () => {
    it('Get a nonexistent key', () => {
        let source = 'The message for getSource';
        CoreSDK.i18nClient.init({ ...config, sourceBundle: { key_to_get: source } });

        let nonexistent_key = Consts.TranslationResource.key_nonexistent;
        let actualSource_nonexistent_key: String = CoreSDK.i18nClient.l10nService.getSourceString(nonexistent_key);
        expect(actualSource_nonexistent_key).toEqual(nonexistent_key);
    });

    it('Get without initializing source bundle', () => {
        let key = 'com.vmware.um.testgetsource';
        CoreSDK.i18nClient.init({ ...config });
        let actualSource: String = CoreSDK.i18nClient.l10nService.getSourceString(key);
        expect(actualSource).toEqual(key);
    });
});

describe('Get translation and message - positive - language only', () => {
    var datafile = `${__dirname}` + '/../data/translationdata.xlsx';
    let testdata = new ExcelReader(datafile).readsheet_with_sheetindex(0);
    let args = ['一', '二', '三'];

    testdata.forEach(item => {
        let key: string = item['key'];
        let source = item['source'];
        let language = item['language'];
        it("language: " + language + "|Key: " + key + "|source: " + source, async function () {
            let currentLanguage = language;
            let i18nClient = CoreSDK.i18nClient.createInstance({ ...config, language: currentLanguage, sourceBundle: EnglishBundle });
            await i18nClient.coreService.loadI18nData();

            let expected_trans = item['expected'];
            if (key == Consts.TranslationResource.key_onlystring) {
                let actualTrans = i18nClient.l10nService.getMessage(key);
                expect(currentLanguage + ': ' + actualTrans).toEqual(currentLanguage + ': ' + expected_trans);
                let actualTrans2 = i18nClient.l10nService.getTranslation(key, source)
                expect(currentLanguage + ': ' + actualTrans2).toEqual(currentLanguage + ': ' + expected_trans);
            }
            else if (key == Consts.TranslationResource.key_withargs) {
                expected_trans = Common.replaceArgs(expected_trans, args);
                let actualTrans = i18nClient.l10nService.getMessage(key, args);
                expect(currentLanguage + ': ' + actualTrans).toEqual(currentLanguage + ': ' + expected_trans);
                let actualTrans2 = i18nClient.l10nService.getTranslation(key, source, args)
                expect(currentLanguage + ': ' + actualTrans2).toEqual(currentLanguage + ': ' + expected_trans);
            }
        }, 15000);
    });

});

it("'Get translation and message - positive - language & region", async function () {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    let currentRegion = 'tw';
    let i18nClient = CoreSDK.i18nClient.createInstance({ ...config, language: currentLanguage, region: currentRegion });
    await i18nClient.coreService.loadI18nData();

    let expected_trans = '消息';

    let actualValue = i18nClient.l10nService.getTranslation(Consts.TranslationResource.key_onlystring, Consts.TranslationResource.source_onlystring);
    expect(actualValue).toEqual(expected_trans);
    let actualValue2 = i18nClient.l10nService.getMessage(Consts.TranslationResource.key_onlystring);
    expect(actualValue2).toEqual(expected_trans);
});







describe("getTranslation - negative", function () {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    let i18nClient = CoreSDK.i18nClient.createInstance({ ...config, language: currentLanguage });
    beforeAll(async () => {
        await i18nClient.coreService.loadI18nData();
    });

    it("Key doesn't exist, will return source.", function () {
        let sourceForTest = 'sourceForTest';
        let actualValue3 = i18nClient.l10nService.getTranslation(Consts.TranslationResource.key_nonexistent, sourceForTest);
        expect(actualValue3).toEqual(sourceForTest);
    });
});


 //todo : use exception data to test('', undefined, null)