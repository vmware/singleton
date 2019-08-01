import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';

debugger

beforeAll(async () => {
    CoreSDK.i18nClient.init({ ...Consts.config });
});

it('getSupportedLanguages', async () => {
    let languages = await CoreSDK.i18nClient.i18nService.getSupportedLanguages();
    expect(languages.length).not.toBeNull();
});

it('getSupportedRegions', async () => {
    let regions = await CoreSDK.i18nClient.i18nService.getSupportedRegions(Consts.SupportedLanguage.de);
    expect(regions).not.toBeNull();
    let regions_not_supportedlanguage = await CoreSDK.i18nClient.i18nService.getSupportedRegions(Consts.TranslationResource.InvalidLanguage);
    expect(regions_not_supportedlanguage).toBeNull();
});


describe('getBrowserCultureLang', () => {
    it('Normal get', async () => {
        (<any>navigator)['__defineGetter__']('language', function () {
            return Consts.TranslationResource.InvalidLanguage;
        });
        (<any>navigator)['__defineGetter__']('languages', function () {
            return [navigator.language];
        });

        expect(CoreSDK.getBrowserCultureLang()).toEqual(Consts.TranslationResource.InvalidLanguage);
    });

    it('navigator.language is undefined', async () => {
        (<any>navigator)['__defineGetter__']('language', function () {
            return undefined;
        });
        (<any>navigator)['__defineGetter__']('languages', function () {
            return [Consts.TranslationResource.InvalidLanguage];
        });

        expect(CoreSDK.getBrowserCultureLang()).toEqual(Consts.TranslationResource.InvalidLanguage);
    });

    it('navigator.languages is undefined', async () => {
        (<any>navigator)['__defineGetter__']('language', function () {
            return Consts.TranslationResource.InvalidLanguage;
        });
        (<any>navigator)['__defineGetter__']('languages', function () {
            return undefined;
        });

        expect(CoreSDK.getBrowserCultureLang()).toEqual(Consts.TranslationResource.InvalidLanguage);
    });

    // it('length of navigator.languages is 0', async () => {
    //     (<any>navigator)['__defineGetter__']('language', function () {
    //         return Consts.TranslationResource.InvalidLanguage;
    //     });
    //     (<any>navigator)['__defineGetter__']('languages', function () {
    //         return [];
    //     });

    //     expect(CoreSDK.getBrowserCultureLang()).toEqual(Consts.TranslationResource.InvalidLanguage);
    // });

    it('Bothe navigator.language and navigator.languages are undefined', async () => {
        (<any>navigator)['__defineGetter__']('language', function () {
            return undefined;
        });
        (<any>navigator)['__defineGetter__']('languages', function () {
            return undefined;
        });

        expect(CoreSDK.getBrowserCultureLang()).toEqual(undefined);
    });
});

