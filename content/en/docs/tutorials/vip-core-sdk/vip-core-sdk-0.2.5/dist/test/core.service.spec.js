"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const index_1 = require("../index");
const locale_de_1 = require("./locale.de");
describe('i18nClient coreService', () => {
    let client, client2;
    const baseConfig = {
        productID: 'TEST',
        component: 'demo',
        version: '1.0.0',
        host: 'localhost:4000',
        sourceBundle: {
            demo: 'demo string',
            hello: 'Hello {0}'
        }
    };
    client = index_1.i18nClient.init(baseConfig);
    describe('init config', () => {
        it('should get correct configs', () => {
            expect(client.coreService.getProductID()).toEqual('TEST');
            expect(client.coreService.getComponent()).toEqual('demo');
        });
        it('should get correct default configs', () => {
            expect(client.coreService.getLanguage()).toEqual('en');
            expect(client.coreService.getRegion()).toEqual('');
        });
    });
    describe('validate language and locale', () => {
        it('should correct validate source locale', () => {
            expect(client.coreService.isSourceLocale('en', 'US')).toEqual(true);
            expect(client.coreService.isSourceLocale('en-US')).toEqual(true);
            expect(client.coreService.isSourceLocale('en-us')).toEqual(true);
            expect(client.coreService.isSourceLocale('en-GB')).toEqual(false);
        });
        it('should correct validate source language', () => {
            expect(client.coreService.isSourceLanguage('en')).toEqual(true);
            expect(client.coreService.isSourceLanguage('EN')).toEqual(true);
            expect(client.coreService.isSourceLanguage('en-US')).toEqual(true);
            expect(client.coreService.isSourceLanguage('de')).toEqual(false);
        });
    });
    describe('set translation', () => {
        client2 = index_1.i18nClient.createInstance(Object.assign({}, baseConfig, { language: 'de', i18nScope: [index_1.PatternCategories.DATE] }));
        it('should return correct translation', () => {
            client2.coreService.setTranslations('de', {
                demo: 'demo-string',
                hello: 'Hallo {0}'
            });
            expect(client2.l10nService.getMessage('demo')).toEqual('demo-string');
            expect(client2.l10nService.getTranslation('hello', '', ['Welt'])).toEqual('Hallo Welt');
        });
    });
    describe('set Pattern', () => {
        it('should set pattern and get correct format', () => {
            client2.coreService.setPatterns(locale_de_1.default.categories, 'de');
            const date = new Date(2019, 2, 14, 9, 3, 16, 28);
            expect(client2.i18nService.formatDate(date, 'fullDate')).toEqual('Donnerstag, 14. März 2019');
            expect(client2.i18nService.formatDate(date, 'short')).toEqual('14.03.19, 09:03');
        });
        it('handle param incorrect', () => {
            expect(() => {
                client2.coreService.setPatterns(locale_de_1.default.categories, '');
            }).toThrow();
        });
    });
    describe('validate config', () => {
        it('should throw error if reqiured param undifined', () => {
            const errorMsg = `Paramater: 'ProductID' required for 'CoreService'`;
            expect(() => { index_1.i18nClient.createInstance({}); }).toThrowError(errorMsg);
        });
    });
});
