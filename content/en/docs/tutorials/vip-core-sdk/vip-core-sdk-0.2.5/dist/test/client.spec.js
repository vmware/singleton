"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const index_1 = require("../index");
const plugins_mock_1 = require("./plugins.mock");
describe('i18nClient', () => {
    const newInstance1 = index_1.i18nClient.createInstance({
        productID: 'TEST',
        host: 'localhost:4000',
        version: '1.0',
    });
    it('init config', () => {
        expect(newInstance1.coreService.getProductID()).toEqual('TEST');
        expect(newInstance1.coreService.getVersion()).toEqual('1.0');
    });
    describe('createInstance', () => {
        const newInstance2 = index_1.i18nClient.createInstance({
            productID: 'TEST1',
            host: 'localhost:4000',
            version: '1.1',
        });
        it('should create instance', () => {
            expect(newInstance2).not.toEqual(newInstance1);
        });
        it('should have correct config', () => {
            expect(newInstance2.coreService.getVersion()).toEqual('1.1');
        });
    });
    describe('plugins', () => {
        index_1.i18nClient
            .plug(new plugins_mock_1.CustomLoader())
            .plug(new plugins_mock_1.CustomParser())
            .init({
            productID: 'TEST',
            host: 'localhost:4000',
            version: '1.0',
            language: 'en-GB',
            i18nScope: [index_1.PatternCategories.DATE]
        });
        describe('validate response ', () => {
            it('loadI18nData', () => {
                index_1.i18nClient.coreService.loadI18nData().then(res => {
                    expect(res[0]).toEqual(plugins_mock_1.mockResponse.trans);
                    expect(res[1]).toEqual(plugins_mock_1.mockResponse.pattern.categories);
                });
            });
            it('getSupportedLanguages', () => {
                index_1.i18nClient.coreService.getSupportedLanguages().then(res => {
                    expect(res).toEqual(plugins_mock_1.mockResponse.languages);
                });
            });
            it('getSupportedRegions', () => {
                index_1.i18nClient.coreService.getSupportedRegions('test').then(res => {
                    expect(res).toEqual(plugins_mock_1.mockResponse.regions);
                });
            });
        });
    });
});
