/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

import { ExcelReader } from '../utils/excelreader';
import * as CommonActions from '../utils/commonactions';
import { DateTimeFormat } from '../utils/constants';
import { I18n } from '@singleton-i18n/g11n-js-sdk';
// import * as I18n from '@singleton-i18n/g11n-js-sdk';
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

debugger


describe('Format Datetime', () => {
    var datafile = `${__dirname}` + '/../data/datedata.xlsx';
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

    describe('Format Datetime - positive', () => {
        
        for (const i in testdata) {
            (function (i) {
                var item = testdata[i];
                // console.log("data to test:\n " + JSON.stringify(item));
                //let region = item['region'];
                it("locale: " + item['language'] + "|input: "+ item['input'] + "|timezone: " + item['timezone'] + "|pattern: " + item['pattern'] + "|desc: " + item['description'], async () => {
                    
                    let Locale = item['language'];
                    let input = item['input'];
                    let input_timezone = item['input'];
                    // input = new Date(input);
                    let Timezone = item['timezone'];
                    let Pattern = item['pattern'];
                    let expected = item['expected'];
                    
                    if (Pattern == DateTimeFormat.default) {
                        Pattern = undefined;
                    }

                    const option1: I18n.DateTimeFormatOptions = {pattern: Pattern, timezone: Timezone};
                    const formatter1 = I18n.DateTimeFormat.getInstance(Locale, option1);
                    const time1 = formatter1.getStandardTime(input_timezone);
                    expect(formatter1.format(time1)).toEqual(expected);

                //     const option2: I18n.DateTimeFormatOptions = {pattern: Pattern, minusSign: '-'};
                //     const formatter2 = I18n.DateTimeFormat.getInstance(Locale, option2);
                //     const time2 = formatter2.getStandardTime(input)

                //     // if (Pattern == DateTimeFormat.default) {
                //     //     Pattern = undefined;
                //     // }
                //     if (item['error']) {
                //         if (CommonActions.isDefined(Timezone)) {
                //             expect(() => {
                //                 //CoreSDK.i18nClient.i18nService.formatDate(input, pattern, timezone);
                //                 formatter1.format(time1);
                //             }).toThrowError(item['error']);
                //         }
                //         else {
                //             expect(() => {
                //                 //CoreSDK.i18nClient.i18nService.formatDate(input, pattern);
                //                 formatter2.format(time2);
                //             }).toThrowError(item['error']);
                //         }
                //     }
                //     else {
                //         if (CommonActions.isDefined(Timezone)) {
                //             expect(formatter1.format(time1)).toEqual(expected);
                //         }
                //         else {
                //             expect(formatter2.format(time2)).toEqual(expected);
                //         }
                //     }
                });
            }(i));
        }
    });


    describe('Format Datetime - negative', () => {
        let input = '2018-01-11T09:03:01+0000';
        // let input = '2018-02-22T09:03:01+0000';
        let Pattern = 'medium';
        // let Pattern = 'full';
        let Locale = 'zh-Hans'
    
        it("locale is empty in register", function () {
            expect(() => { I18n.registerLocaleData('', dePatternData.categories); }).toThrowError("RegisterLocaleData failed, locale is not provided when calling registerLocaleData function.");
        });
        it("pattern data is null", function () {
            expect(() => { I18n.registerLocaleData(Locale, null); }).toThrowError("RegisterLocaleData failed, pattern is not provided when calling registerLocaleData function.");
        });
        it("minusSing is .", function () {
            const dt_options: I18n.DateTimeFormatOptions = {pattern: Pattern, minusSign: '.'};
            const formatter = I18n.DateTimeFormat.getInstance(Locale,dt_options);
            console.log("output:",formatter.format(formatter.getStandardTime(input)));
            expect(formatter.format(new Date(input))).toEqual('2018年1月11日 下午5:03:01');
        });
    
        it("format() is incorrect if pattern is not 12 pre-defined format", function () {
            const dt_options: I18n.DateTimeFormatOptions = {pattern: "abc"};
            const formatter = I18n.DateTimeFormat.getInstance(Locale,dt_options);
            console.log("output:",formatter.format(formatter.getStandardTime(input)));
            expect(formatter.format(new Date(input))).toEqual("下午bc");
    
        });
        it("format() is incorrect if pattern is not 12 pre-defined format", function () { 
            const dt_options: I18n.DateTimeFormatOptions = {pattern: 'E, MMM d, y G'};
            const formatter = I18n.DateTimeFormat.getInstance(Locale,dt_options);
            console.log("output:",formatter.format(formatter.getStandardTime(input)));
            expect(formatter.format(new Date(input))).toEqual("周四, 1月 11, 2018 公元");
            
        });
        it("Pattern is null(as mediumdate) in DateTimeFormatOptions:", function () {
            const dt_options: I18n.DateTimeFormatOptions = {pattern: null};
            const formatter = I18n.DateTimeFormat.getInstance(Locale,dt_options);
            //console.log("output:",formatter.format(formatter.getStandardTime(input)));
            expect(formatter.format(new Date(input))).toEqual("2018年1月11日");
    
        });
        it("Pattern is undefined(as mediumdate) in DateTimeFormatOptions", function () {
            const dt_options: I18n.DateTimeFormatOptions = {pattern: undefined};
            const formatter = I18n.DateTimeFormat.getInstance(Locale,dt_options);
            console.log("undefined pattern output:",formatter.format(formatter.getStandardTime(input)));
            expect(formatter.format(formatter.getStandardTime(input))).toEqual("2018年1月11日");
    
        });
        
        it("Locale is not registered ", function () {
            const dt_options: I18n.DateTimeFormatOptions = {pattern: undefined};
            // const formatter = I18n.DateTimeFormat.getInstance("ru",dt_options);
            //console.log("output:",formatter.format(formatter.getStandardTime(input)));
            // expect(formatter.format(new Date(input))).toEqual("");
            expect(() => { I18n.DateTimeFormat.getInstance("ru",dt_options); }).toThrowError("Locale data should be registered before creating DateTimeFormat object.");
        });
        
        // it("Timezone is null", function () {
        //     const dt_options: I18n.DateTimeFormatOptions = {pattern: 'default', timezone: null};
        //     const formatter = I18n.DateTimeFormat.getInstance(Locale,dt_options);
        //     console.log("output: "+ formatter.format(formatter.getStandardTime(input)));
        //     expect(formatter.format(formatter.getStandardTime(input))).toEqual('2018年1月11日');
        // });
        // it("Timezone is +0430", function () {
        //     const dt_options: I18n.DateTimeFormatOptions = {pattern: "full", timezone: '+0700'};
        //     console.log("options:",dt_options);
        //     const formatter = I18n.DateTimeFormat.getInstance("zh-Hans", dt_options);
        //     console.log("formatter:",formatter);
        //     const timeinput = formatter.getStandardTime('2018-02-22T00:03:01+0000');
        //     console.log("time:",timeinput);
        //     console.log("output:",formatter.format(timeinput));
        //     expect(formatter.format(timeinput)).toEqual('2018年2月22日星期四 GMT+07:00 上午7:03:01');
        //     // expect(() => { formatter.format(timeinput) }).toEqual('2018年2月22日星期四 GMT+07:00 上午7:03:01');
        // });
    });
})




