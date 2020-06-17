/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

import { I18n } from '@singleton-i18n/g11n-js-sdk';
import * as Consts from '../utils/constants';
import { ExcelReader } from '../utils/excelreader';
import * as CommonActions from '../utils/commonactions'
import { EnglishBundle } from '../resources/EnglishBundle';
import dePatternData from '../resources/patterndata_de';
import sourcePatternData from '../resources/patterndata_en';
import caESPatternData from '../resources/patterndata_ca-ES-VALENCIA';
import enGBPatternData from '../resources/patterndata_en-001';
import enUSPatternData from '../resources/patterndata_en-US';
import esPatternData from '../resources/patterndata_es';
import frCAPatternData from '../resources/patterndata_fr-CA';
import frPatternData from '../resources/patterndata_fr';
import jaPatternData from '../resources/patterndata_ja';
import koPatternData from '../resources/patterndata_ko';
import zhHansPatternData from '../resources/patterndata_zh-Hans';
import zhTWPatternData from '../resources/patterndata_zh-TW';
import arPatternData from '../resources/patterndata_ar';
import abcPatternData from '../resources/patterndata_abc';

debugger

describe('Format Plural', () => {
    var lastLanguage : string = '';
    var lastRegion = '';

    var testdata;
    var datafile = `${__dirname}` + '/../data/pluraldata.xlsx';
    testdata = new ExcelReader(datafile).readsheet_with_sheetname('CommonLib');
    console.log('Total case number in file is: ' + (testdata.length));

    I18n.registerLocaleData('en', sourcePatternData.categories);
    I18n.registerLocaleData('de', dePatternData.categories);
    I18n.registerLocaleData('ca-ES-VALENCIA', caESPatternData.categories);
    I18n.registerLocaleData('en-001', enGBPatternData.categories);
    I18n.registerLocaleData('en-US', enUSPatternData.categories);
    I18n.registerLocaleData('es', esPatternData.categories);
    I18n.registerLocaleData('fr', frPatternData.categories);
    I18n.registerLocaleData('fr-CA', frCAPatternData.categories);
    I18n.registerLocaleData('ja', jaPatternData.categories);
    I18n.registerLocaleData('ko', koPatternData.categories);
    I18n.registerLocaleData('zh-Hans', zhHansPatternData.categories);
    I18n.registerLocaleData('zh-TW', zhTWPatternData.categories);
    I18n.registerLocaleData('ar', arPatternData.categories);
    I18n.registerLocaleData('abc', abcPatternData.categories);

    const pluralEN = I18n.PluralRules.getInstance('en');
    const pluralDE = I18n.PluralRules.getInstance('de');
    const pluralcaES = I18n.PluralRules.getInstance('ca-ES-VALENCIA');
    const pluralenGB = I18n.PluralRules.getInstance('en-001');
    const pluralenUS = I18n.PluralRules.getInstance('en-US');
    const pluralfr = I18n.PluralRules.getInstance('fr');
    const plurales = I18n.PluralRules.getInstance('es');
    const pluralfrCA = I18n.PluralRules.getInstance('fr-CA');
    const pluralja = I18n.PluralRules.getInstance('ja');
    const pluralko = I18n.PluralRules.getInstance('ko');
    const pluralzhHans = I18n.PluralRules.getInstance('zh-Hans');
    const pluralzhTW = I18n.PluralRules.getInstance('zh-TW');
    const pluralabc = I18n.PluralRules.getInstance('abc');


    testdata.forEach(item => {
        let language = (item as any)['language'];
        let category = (item as any)['category'];
        let number = (item as any)['number'];
        let description = (item as any)['description'];

        it("lang: " + language + "; number: '" + number + "'; category: " + category + "; desc: " + description, async function () {
            let argument = (item as any)['argument'];
            let expected = (item as any)['expected'];

            // console.log("data to test:\n "+JSON.stringify(item));
            let plural = I18n.PluralRules.getInstance(language);
            var actualdata = plural.select(number);
            expect(actualdata).toEqual(category);
            

    
            // var actualdata = CoreSDK.i18nClient.l10nService.getTranslation(Consts.TranslationResource.key_plural, Consts.TranslationResource.source_plural, { files: number, place: argument });
            // expect(actualdata).toEqual(expected);

            // var actualdata1 = CoreSDK.i18nClient.l10nService.getMessage(Consts.TranslationResource.key_plural, { files: number, place: argument });
            // expect(actualdata1).toEqual(expected);
        });
    });
});


//todo:  //test files parameter
    // test place parameter

