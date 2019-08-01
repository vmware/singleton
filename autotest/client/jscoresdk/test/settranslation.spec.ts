import * as CoreSDK from '@vip/vip-core-sdk-dev';
import * as Consts from '../utils/constants';

debugger

describe('language only', () => {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    beforeAll(() => {
        CoreSDK.i18nClient.init({ ...Consts.config, language: currentLanguage });
    });

    it("setTranslations and get", function () {
        let trans = '新的翻译';
        CoreSDK.i18nClient.coreService.setTranslations(currentLanguage, {
            newTranslation: trans,
        });
        let actualValue = CoreSDK.i18nClient.l10nService.getTranslation('newTranslation', '');
        expect(actualValue).toEqual(trans);
    });


    it('Test plural - language ', function () {
        let num = 1.123;
        let argument = '桌子';
        let trans = "{files, plural, one{category one - # il y a un fichier sur {place}.} other {category other - # il n'y a pas de fichiers sur {place}.}}";
        let expected = "category other - " + num + " il n'y a pas de fichiers sur " + argument + ".";
        CoreSDK.i18nClient.init({ ...Consts.config, language: currentLanguage });
        CoreSDK.i18nClient.coreService.setTranslations(currentLanguage, {
            newTranslation: trans,
        });
        let actualValue = CoreSDK.i18nClient.l10nService.getTranslation('newTranslation', '', { files: num, place: argument });
        expect(actualValue).toEqual(expected);
    });
});

describe('language & region', () => {
    let currentLanguage = Consts.SupportedLanguage.zhcn;
    let currentRegion = 'cn';
    beforeAll(() => {
        CoreSDK.i18nClient.init({ ...Consts.config, language: currentLanguage, region: currentRegion });
    });

    it("setTranslations and get", function () {
        let trans = '新的翻译';
        CoreSDK.i18nClient.coreService.setTranslations(currentLanguage, {
            newTranslation: trans,
        });
        let actualValue = CoreSDK.i18nClient.l10nService.getTranslation('newTranslation', '');
        expect(actualValue).toEqual(trans);
    });

    it('Test plural - language&region', function () {
        let num = 1.123;
        let argument = '桌子';
        let trans = "{files, plural, one{category one - # il y a un fichier sur {place}.} other {category other - # il n'y a pas de fichiers sur {place}.}}";
        let expected = "category other - " + num + " il n'y a pas de fichiers sur " + argument + ".";
        CoreSDK.i18nClient.coreService.setTranslations(currentLanguage, {
            newTranslation: trans,
        });
        let actualValue = CoreSDK.i18nClient.l10nService.getTranslation('newTranslation', '', { files: num, place: argument });
        expect(actualValue).toEqual(expected);
    });
});

