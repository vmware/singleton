/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

import { I18n } from '@singleton-i18n/g11n-js-sdk';
import * as Consts from '../utils/constants'
import { ExcelReader } from '../utils/excelreader'
import * as CommonActions from '../utils/commonactions'
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
import abcPatternData from '../resources/patterndata_abc';

// debugger

// jest.setTimeout(10000);

describe('Format Number', () => {
    var datafile = `${__dirname}` + '/../data/numberdata.xlsx';
    let testdata = new ExcelReader(datafile).readsheet_with_sheetindex(0);
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
    I18n.registerLocaleData('abc', abcPatternData.categories);

    for (const i in testdata) {
        // if (Number.parseInt(i) > 1) {continue;}
        (function (i) {
            var item = testdata[i];
            
            it("language: " + item['language'] + " |input: " + item['input'] + " description: " + item['description'], async function () {
                console.log(i+": data to test:\n "+JSON.stringify(item));

                const numberFormatter =(value: any, type: string, locale: string) => {
                    const option: I18n.NumberFormatOptions = {numberFormatType: type, currencyCode: ''};

                    const numberFormat = I18n.NumberFormat.getInstance(locale,option);

                    return numberFormat.format(value);
            
                }

                var actualdata = numberFormatter(item['input'], 'decimal', item['language']);
                expect(actualdata).toEqual(item['expected']);

            });
        }(i));
    }



    describe("Format Number - negative", () => {
        let currentLanguage = Consts.SupportedLanguage.zhcn;
        
        console.log("Format Number - negative");

        
        const numberFormatter =(value: any, type: string, locale: string) => {
            const option: I18n.NumberFormatOptions = {numberFormatType: type};

            const numberFormat = I18n.NumberFormat.getInstance(locale,option);

            return numberFormat.format(value);
        }

        it("Locale abc has been registered but no content in category", function () {
            expect(() => {numberFormatter('12345', 'decimal', 'abc'); }).toThrowError("Pattern data for Number should be provided before creating NumberFormat object.");
        });   
        it("Format Number with number as ''", function () {
            // expect(() => {numberFormatter('', 'decimal', currentLanguage); }).toThrowError("InvalidParamater: 'Invalid number '' for 'formatNumber''");
            var actualdata = numberFormatter('', 'decimal', currentLanguage);
            expect(actualdata).toEqual('0');
        });
        it("Format Number with number as 'undefined'", function () {
            // expect(() => {numberFormatter(undefined, 'decimal', currentLanguage); }).toThrowError("InvalidParamater: 'Invalid number 'undefined' for 'formatNumber''");
            var actualdata = numberFormatter(undefined, 'decimal', currentLanguage);
            expect(actualdata).toEqual('0');
        });
        it("Format Number with number as 'null'", function () {
            // expect(() => {numberFormatter(null, 'decimal', currentLanguage); }).toThrowError("InvalidParamater: 'Invalid number 'null' for 'formatNumber''");
            var actualdata = numberFormatter(null, 'decimal', currentLanguage);
            expect(actualdata).toEqual('0');
        });
        // it("Format Number with number as '123456789012345678901234567'", function () {
        //     //const abc = numberFormatter(123456789012345678901234567, 'decimal', currentLanguage);
        //     //console.log("abc:"+ abc)
        //     expect(numberFormatter('123456789012345678901234567', 'decimal', currentLanguage)).toEqual("12,345");
        // });

        let input = '0x27be525';
        let anotherLanguage = Consts.SupportedLanguage.de;
        it("Format Number with language as 'ab' that isn't registed", function () {
            //var actualdata = numberFormatter(input, 'decimal', 'ab');
            //expect(actualdata).toEqual('?');
            expect(() => { numberFormatter(input, 'decimal', 'ab'); }).toThrowError("Locale data should be registered before creating NumberFormat object.");
        });
        it("Format Number with language different from current language", function () {
            var actualdata = numberFormatter(input, 'decimal', anotherLanguage);
            expect(actualdata).toEqual('41.674.021');
        });
    });

});