/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

import dePatternData from './locale_de';
import sourcePatternData from '../src/data/locale_en';
import {I18n} from '../index';

describe('number formatter', () => {

    I18n.registerLocaleData('en', sourcePatternData.categories);
    I18n.registerLocaleData('de', dePatternData.categories);

    const numberFormater = (value: any, type: string, locale: string) => {
        if (type === 'currencies') {
            const currencyCode = locale === 'en' ? 'USD' : 'EUR';
            const option: I18n.NumberFormatOptions = {numberFormatType: type, currencyCode: currencyCode};
            const numberFormat = I18n.NumberFormat.getInstance(locale,option);
            return numberFormat.format(value);
        } else if (type === 'plural') {
            const option: I18n.NumberFormatOptions = {numberFormatType: type, currencyCode: ''};
            const numberFormat = I18n.NumberFormat.getInstance(locale,option);
            return numberFormat.format(value);
        } else {
            const option: I18n.NumberFormatOptions = {numberFormatType: type, currencyCode: ''};
            const numberFormat = I18n.NumberFormat.getInstance(locale,option);
            return numberFormat.format(value);
        }
    };

    const pluralEN = I18n.PluralRules.getInstance('en');
    const pluralDE = I18n.PluralRules.getInstance('de');
    //test singleton mode.
    const pluralDE1 = I18n.PluralRules.getInstance('de');

    describe('decimal format', () => {
        it('in English', () => {
            expect(numberFormater(12345, 'decimal', 'en')).toEqual('12,345');
            expect(numberFormater(123.7892, 'decimal', 'en')).toEqual('123.789');
            expect(numberFormater(.23, 'decimal', 'en')).toEqual('0.23');
            expect(numberFormater(undefined, 'decimal', 'en')).toEqual('0');
            expect(numberFormater(null, 'decimal', 'en')).toEqual('0');
            expect(numberFormater('', 'decimal', 'en')).toEqual('0');
        });
        it('in German', () => {
            expect(numberFormater(12345, 'decimal', 'de')).toEqual('12.345');
            expect(numberFormater(123.7892, 'decimal', 'de')).toEqual('123,789');
            expect(numberFormater(.23, 'decimal', 'de')).toEqual('0,23');
            expect(numberFormater(undefined, 'decimal', 'de')).toEqual('0');
            expect(numberFormater(null, 'decimal', 'de')).toEqual('0');
            expect(numberFormater('', 'decimal', 'de')).toEqual('0');
        });
    });

    describe('percent format', () => {
        it('in English', () => {
            expect(numberFormater(0.123, 'percent', 'en')).toEqual('12%');
            expect(numberFormater(1234, 'percent', 'en')).toEqual('123,400%');
            expect(numberFormater(.23, 'percent', 'en')).toEqual('23%');
            expect(numberFormater(undefined, 'percent', 'en')).toEqual('0%');
            expect(numberFormater(null, 'percent', 'en')).toEqual('0%');
            expect(numberFormater('', 'percent', 'en')).toEqual('0%');
        });
        it('in German', () => {
            expect(numberFormater(0.123, 'percent', 'de')).toEqual('12 %');
            expect(numberFormater(1234, 'percent', 'de')).toEqual('123.400 %');
            expect(numberFormater(.23, 'percent', 'de')).toEqual('23 %');
            expect(numberFormater(undefined, 'percent', 'de')).toEqual('0 %');
            expect(numberFormater(null, 'percent', 'de')).toEqual('0 %');
            expect(numberFormater('', 'percent', 'de')).toEqual('0 %');
        });
    });

    describe('plural format', () => {
        it('in English', () => {
            expect(numberFormater(0.123, 'plural', 'en')).toEqual('0.123');
            expect(numberFormater(1234, 'plural', 'en')).toEqual('1234');
            expect(numberFormater(.23, 'plural', 'en')).toEqual('0.23');
            expect(numberFormater(undefined, 'plural', 'en')).toEqual('0');
            expect(numberFormater(null, 'plural', 'en')).toEqual('0');
            expect(numberFormater('', 'plural', 'en')).toEqual('0');
        });
        it('in German', () => {
            expect(numberFormater(0.123, 'plural', 'de')).toEqual('0.123');
            expect(numberFormater(1234, 'plural', 'de')).toEqual('1234');
            expect(numberFormater(.23, 'plural', 'de')).toEqual('0.23');
            expect(numberFormater(undefined, 'plural', 'de')).toEqual('0');
            expect(numberFormater(null, 'plural', 'de')).toEqual('0');
            expect(numberFormater('', 'plural', 'de')).toEqual('0');
        });
    });

    describe('currency format', () => {
        it('in English', () => {
            expect(numberFormater(0.12, 'currencies', 'en')).toEqual('$0.12');
            expect(numberFormater(1234.56, 'currencies', 'en')).toEqual('$1,234.56');
            expect(numberFormater(.23, 'currencies', 'en')).toEqual('$0.23');
            expect(numberFormater(undefined, 'currencies', 'en')).toEqual('$0.00');
            expect(numberFormater(null, 'currencies', 'en')).toEqual('$0.00');
            expect(numberFormater('', 'currencies', 'en')).toEqual('$0.00');
        });
        it('in German', () => {
            expect(numberFormater(0.123, 'currencies', 'de')).toEqual('0,12 €');
            expect(numberFormater(1234.56, 'currencies', 'de')).toEqual('1.234,56 €');
            expect(numberFormater(.23, 'currencies', 'de')).toEqual('0,23 €');
            expect(numberFormater(undefined, 'currencies', 'de')).toEqual('0,00 €');
            expect(numberFormater(null, 'currencies', 'de')).toEqual('0,00 €');
            expect(numberFormater('', 'currencies', 'de')).toEqual('0,00 €');
        });
    });

    describe('plural select', () => {
        it('in English', () => {
            expect(pluralEN.select(1)).toEqual('one');
            expect(pluralEN.select(2)).toEqual('other');
        });
        it('in German', () => {
            expect(pluralDE.select(1)).toEqual('one');
            expect(pluralDE.select(2)).toEqual('other');
            expect(pluralDE1.select(3)).toEqual('other');
        });
    });
});
