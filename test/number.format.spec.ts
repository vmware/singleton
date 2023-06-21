/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { FormatterFactory } from '../src/formatters/number.formatter';
import dePatternData from './locale.de';
import sourcePatternData from '../src/data/locale_en';

describe('number formatter', () => {
    const dataForEn = sourcePatternData.categories.numbers;
    const dataForDe = dePatternData.categories.numbers;
    const formatterFactory = new FormatterFactory();
    const numberFormater = (value: number, type: string, locale: string, min?: number, max?: number) => {
        const data = locale === 'en' ? dataForEn : dataForDe;
        let formatter: Function;
        if (type === 'decimal') {
            formatter = formatterFactory.decimal(data, locale);
        } else {
            formatter = formatterFactory.percent(data, locale);
        }
        return formatter(value, min, max);
    };

    const pluralNumberFormater = (value: number, type: string, locale: string, min?: number, max?: number) => {
        const data = locale === 'en' ? dataForEn : dataForDe;
        let formatter: Function;
        if (type === 'plural') {
            formatter = formatterFactory.roundNumberForPlural(data, locale);
            return formatter(value, min, max);
        }
        return;
    };

    const ORIGION_NUM = 123.7892;
    const ORIGION_PERCENT_NUM = 0.12388;
    describe('decimal format', () => {
        it('in english', () => {
            expect(numberFormater(12345, 'decimal', 'en')).toEqual('12,345');
            expect(numberFormater(123.7892, 'decimal', 'en')).toEqual('123.789');
            expect(numberFormater(.23, 'decimal', 'en')).toEqual('0.23');
            expect(numberFormater(ORIGION_NUM, 'decimal', 'en', 2, 2)).toEqual('123.79');
        });
        it('in other language', () => {
            expect(numberFormater(12345, 'decimal', 'de')).toEqual('12.345');
            expect(numberFormater(123.7892, 'decimal', 'de')).toEqual('123,789');
            expect(numberFormater(.23, 'decimal', 'de')).toEqual('0,23');
            expect(numberFormater(ORIGION_NUM, 'decimal', 'de', 2, 2)).toEqual('123,79');
        });
    });
    describe('percent format', () => {
        it('in english', () => {
            expect(numberFormater(0.123, 'percent', 'en')).toEqual('12%');
            expect(numberFormater(1234, 'percent', 'en')).toEqual('123,400%');
            expect(numberFormater(.23, 'percent', 'en')).toEqual('23%');
            expect(numberFormater(ORIGION_PERCENT_NUM, 'percent', 'en', 2, 2)).toEqual('12.39%');
        });
        it('in other language', () => {
            expect(numberFormater(0.123, 'percent', 'de')).toEqual('12 %');
            expect(numberFormater(1234, 'percent', 'de')).toEqual('123.400 %');
            expect(numberFormater(.23, 'percent', 'de')).toEqual('23 %');
            expect(numberFormater(ORIGION_PERCENT_NUM, 'percent', 'de', 2, 2)).toEqual('12,39 %');
        });
    });
    describe('plural number format', () => {
        it('in english', () => {
            expect(pluralNumberFormater(ORIGION_PERCENT_NUM, 'plural', 'en', 2, 2)).toEqual('0.12');
        });
    });
});
