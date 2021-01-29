/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { i18nClient, PatternCategories} from '../index';
import { CustomLoader, CustomParser, mockResponse } from './plugins.mock';
describe('i18nClient', () => {
    const newInstance1 = i18nClient.createInstance({
        productID: 'TEST',
        host: 'localhost:4000',
        version: '1.0',
    });
    it('init config', () => {
        expect(newInstance1.coreService.getProductID()).toEqual('TEST');
        expect(newInstance1.coreService.getVersion()).toEqual('1.0');
    });
    describe('createInstance', () => {
        const newInstance2 = i18nClient.createInstance({
            productID: 'TEST1',
            host: 'localhost:4000',
            version: '1.1',
        });
        it('should create instance', () => {
            expect( newInstance2 ).not.toEqual( newInstance1 );
        });
        it('should have correct config', () => {
            expect(newInstance2.coreService.getVersion()).toEqual('1.1');
        });
    });
    describe('plugins', () => {
        i18nClient
        .plug( new CustomLoader() )
        .plug(new CustomParser() )
        .init({
            productID: 'TEST',
            host: 'localhost:4000',
            version: '1.0',
            language: 'en-GB',
            i18nScope: [ PatternCategories.DATE ]
        });

        describe('validate response ', () => {
            it('loadI18nData', () => {
                i18nClient.coreService.loadI18nData().then( res => {
                    expect( res.components ).toEqual( mockResponse.res.data.components);
                    expect( res.pattern.categories ).toEqual( mockResponse.res.data.pattern.categories);
                });
            });
            it('getSupportedLanguages', () => {
                i18nClient.coreService.getSupportedLanguages().then( res => {
                    expect( res ).toEqual( mockResponse.languages);
                });
            });
            it('getSupportedRegions', () => {
                i18nClient.coreService.getSupportedRegions('test').then( res => {
                    expect( res ).toEqual( mockResponse.regions);
                });
            });
        });
    });

});
