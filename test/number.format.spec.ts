/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { FormatterFactory } from '../src/formatters/number.formatter';
import dePatternData from './locale.de';
import sourcePatternData from '../src/data/locale_en';

describe('number formatter', () => {
    const dataForEn = sourcePatternData.categories.numbers;
    const dataForDe  = dePatternData.categories.numbers;
    const formatterFactory = new FormatterFactory();
    const numberFormater = (value: any, type: string, locale: string) => {
            const data = locale === 'en' ? dataForEn : dataForDe;
            let formatter: Function;
            if ( type === 'decimal' ) {
                formatter = formatterFactory.decimal(data, locale);
            } else {
                formatter = formatterFactory.percent(data, locale);
            }
            return formatter(value);
        };

    describe('decimal format', () => {
        it('in english', () => {
            expect(numberFormater(12345, 'decimal', 'en')).toEqual('12,345');
            expect(numberFormater(123.7892, 'decimal', 'en')).toEqual('123.789');
            expect(numberFormater(.23, 'decimal', 'en')).toEqual('0.23');
        });
        it('in other language', () => {
            expect(numberFormater(12345, 'decimal', 'de')).toEqual('12.345');
            expect(numberFormater(123.7892, 'decimal', 'de')).toEqual('123,789');
            expect(numberFormater(.23, 'decimal', 'de')).toEqual('0,23');
        });
    });
    describe('percent format', () => {
        it('in english', () => {
            expect(numberFormater(0.123, 'percent', 'en')).toEqual('12%');
            expect(numberFormater(1234, 'percent', 'en')).toEqual('123,400%');
            expect(numberFormater(.23, 'percent', 'en')).toEqual('23%');
        });
        it('in other language', () => {
            expect(numberFormater(0.123, 'percent', 'de')).toEqual('12 %');
            expect(numberFormater(1234, 'percent', 'de')).toEqual('123.400 %');
            expect(numberFormater(.23, 'percent', 'de')).toEqual('23 %');
        });
    });
});
