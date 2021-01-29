/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { i18nClient, PatternCategories } from '../index';
import dePatternData from './locale.de';
import sourcePatternData from '../src/data/locale_en';

describe('i18nService', () => {
    const baseConfig = {
        productID: 'TEST',
        version: '1.0.0',
        host: 'localhost:4000',
        i18nScope: [
            PatternCategories.DATE,
            PatternCategories.NUMBER,
            PatternCategories.CURRENCIES,
            PatternCategories.PLURAL
        ]
    };
    const clientEN = i18nClient.createInstance(baseConfig),
            clientDe = i18nClient.createInstance({ ...baseConfig, ...{ language: 'de'}});
            dePatternData.categories.supplemental = sourcePatternData.categories.supplemental;
            clientDe.coreService.setPatterns( dePatternData.categories,  'de');

    const date = new Date(2019, 2, 22, 9, 3, 1, 550);

    describe('formatDate', () => {
        it('should return correct formatted date in various language', () => {
            const deReg = /Freitag, 22. März 2019 um 09:03:01 GMT(\+|-)\d{2}:\d{2}/;
            expect(clientDe.i18nService.formatDate(date, 'full')).toMatch( deReg );
        });
        it('should throw error when param unavailable', () => {
            expect( () => { clientDe.i18nService.formatDate('ABC', 'full'); } ).toThrow( );
        });
    });
    describe('formatDecimal', () => {
        it('should return correct formatted decimal', () => {
            expect(clientEN.i18nService.formatNumber(12345)).toEqual('12,345');
            expect(clientEN.i18nService.formatNumber(123.7892)).toEqual('123.789');
            expect(clientDe.i18nService.formatNumber('123.7892')).toEqual('123,789');
            expect(clientDe.i18nService.formatNumber('.23')).toEqual('0,23');
        });
        it('should throw error when param unavailablendle', () => {
            expect( () => { clientDe.i18nService.formatNumber(NaN); }).toThrow();
            expect( () => { clientDe.i18nService.formatNumber('abc'); }).toThrow();
        });
    });
    describe('formatPercent', () => {
        it('should return correct value', () => {
            expect(clientEN.i18nService.formatPercent(1234)).toEqual('123,400%');
            expect(clientDe.i18nService.formatPercent(0.123)).toEqual('12 %');
        });
        it('should throw error when param unavailablendle', () => {
            expect( () => { clientDe.i18nService.formatPercent('abc'); }).toThrow();
        });
    });
    describe('formatCurrency', () => {
        describe('curency format', () => {
            it('in english', () => {
                expect(clientEN.i18nService.formatCurrency(12345)).toEqual('$12,345.00');
                expect(clientEN.i18nService.formatCurrency(123.7892)).toEqual('$123.79');
                expect(clientEN.i18nService.formatCurrency(.23)).toEqual('$0.23');
            });
            it('in other language', () => {
                expect(clientDe.i18nService.formatCurrency(12345, 'EUR')).toEqual('12.345,00 €');
                expect(clientDe.i18nService.formatCurrency(123.7892, 'CNY')).toEqual('123,79 CN¥');
                expect(clientDe.i18nService.formatCurrency(.23, 'JPY')).toEqual('0 ¥');
            });
        });
    });
    describe('getPluralCategoryType', () => {
        it('should return correct plural type', () => {
            expect(clientEN.i18nService.getPluralCategoryType(0, 'en')).toEqual('other');
            expect(clientEN.i18nService.getPluralCategoryType(1, 'en')).toEqual('one');
            expect(clientDe.i18nService.getPluralCategoryType(0, 'en')).toEqual('other');
            expect(clientDe.i18nService.getPluralCategoryType(1, 'en')).toEqual('one');
        });
        it('should throw error when param unavailablendle', () => {
            expect( () => { clientDe.i18nService.getPluralCategoryType('abc' as any); }).toThrow();
        });
    });
});
