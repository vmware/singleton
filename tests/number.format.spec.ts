/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { SOURCE_LOCALE_DATA, PatternCategories } from '../index';

import { FormatterFactory } from '../src/formatters/number.formatter';
import LOCALE_DATA_DE from './locale_de';




describe( 'Decimal and Currency', () => {
    const getDataForCurrency = (data: any) => {
        return {
            currencyFormats: data[PatternCategories.NUMBER].numberFormats.currencyFormats,
            numberSymbols: data[PatternCategories.NUMBER].numberSymbols,
            currencySymbols: data[PatternCategories.CURRENCIES],
            fractions: data['supplemental'].currencies.fractions
        };
    };
    const EN_CURRENCY_DATA = getDataForCurrency( SOURCE_LOCALE_DATA.categories ),
            DE_CURRENCY_DATA = getDataForCurrency( LOCALE_DATA_DE.categories );
    const  formatterFactory: FormatterFactory = new FormatterFactory();
    const NUMBER_EN = formatterFactory.decimal( SOURCE_LOCALE_DATA.categories[PatternCategories.NUMBER], 'en' ),
        NUMBER_DE = formatterFactory.decimal( LOCALE_DATA_DE.categories[PatternCategories.NUMBER], 'de' ),
        CURRENCY_EN = formatterFactory.currencies( EN_CURRENCY_DATA, 'en' ),
        CURRENCY_DE = formatterFactory.currencies( DE_CURRENCY_DATA, 'de' ),
        PERCENT_EN = formatterFactory.percent( SOURCE_LOCALE_DATA.categories[PatternCategories.NUMBER], 'en'),
        PERCENT_DE = formatterFactory.percent( LOCALE_DATA_DE.categories[PatternCategories.NUMBER], 'de');


    describe('decimal transform', () => {
        it('should return correct value for numbers', () => {
            expect(NUMBER_EN(12345)).toEqual('12,345');
            expect(NUMBER_EN(123.7892)).toEqual('123.789');
            expect(NUMBER_EN(.23)).toEqual('0.23');
        });
        it('should support string', () => {
            expect(NUMBER_EN('12345')).toEqual('12,345');
            expect(NUMBER_EN('123.7892')).toEqual('123.789');
            expect(NUMBER_EN('.23')).toEqual('0.23');
        });
    });

    describe('currency transform', () => {
        it('should return correct value for number', () => {
            expect(CURRENCY_EN('12345', 'USD')).toEqual('$12,345.00');
            expect(CURRENCY_EN('123.7892', 'USD')).toEqual('$123.79');
            expect(CURRENCY_EN('.23', 'USD')).toEqual('$0.23');
        });
    });

    describe('percent transform', () => {
        it('should return correct value for number', () => {
            expect(PERCENT_EN('0.123')).toEqual('12%');
            expect(PERCENT_EN('1234')).toEqual('123,400%');
            expect(PERCENT_EN('.23')).toEqual('23%');
        });
    });

    // TODO add more data
    describe('should transform in other locale', () => {
        it('should return correct value for decimal', () => {
            expect( NUMBER_DE(12345)).toEqual('12.345');
            expect( NUMBER_DE(123.7892)).toEqual('123,789');
            expect( NUMBER_DE('.23')).toEqual('0,23');
        });
        it('should return correct value for percent', () => {
            expect( PERCENT_DE('.23')).toEqual('23 %');
            expect( PERCENT_DE('0.123')).toEqual('12 %');
        });
        it('should return correct value for currency', () => {
            expect( CURRENCY_DE(12345, 'EUR')).toEqual('12.345,00 €');
            expect( CURRENCY_DE('123.7892', 'CNY')).toEqual('123,79 CN¥');
            expect( CURRENCY_DE('.23', 'JPY')).toEqual('0 ¥');
        });
    });

});
