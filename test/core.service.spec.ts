/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { i18nClient, PatternCategories } from '../index';
import dePatternData from './locale.de';

describe( 'i18nClient coreService', () => {
    let client: any,
        client2: any;
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
    client = i18nClient.init(baseConfig);
    describe( 'init config', () => {
        it('should get correct configs', () => {
            expect( client.coreService.getProductID() ).toEqual('TEST');
            expect( client.coreService.getComponent() ).toEqual('demo');
        });
        it('should get correct default configs', () => {
            expect( client.coreService.getLanguage() ).toEqual('en');
            expect( client.coreService.getRegion() ).toEqual('');
        });
    });
    describe( 'validate language and locale', () => {
        it( 'should correct validate source locale', () => {
            expect( client.coreService.isSourceLocale('en', 'US') ).toEqual( true );
            expect( client.coreService.isSourceLocale('en-US') ).toEqual( true );
            expect( client.coreService.isSourceLocale('en-us') ).toEqual( true );
            expect( client.coreService.isSourceLocale('en-GB') ).toEqual(false);
        });
        it( 'should correct validate source language', () => {
            expect( client.coreService.isSourceLanguage('en') ).toEqual( true );
            expect( client.coreService.isSourceLanguage('EN') ).toEqual( true );
            expect( client.coreService.isSourceLanguage('en-US') ).toEqual( true );
            expect( client.coreService.isSourceLanguage('de') ).toEqual(false);
        });
    });
    describe( 'set translation', () => {
        client2 = i18nClient.createInstance({
            ...baseConfig,
            ...{ language: 'de', i18nScope: [ PatternCategories.DATE ]}
        });
        it('should return correct translation', () => {
            client2.coreService.setTranslations('de', {
                demo: 'demo-string',
                hello: 'Hallo {0}'
            });
            expect( client2.l10nService.getMessage('demo') ).toEqual('demo-string');
            expect( client2.l10nService.getTranslation('hello', '', ['Welt']) ).toEqual('Hallo Welt');
        });
    });
    describe( 'set Pattern', () => {
        it('should set pattern and get correct format', () => {
            client2.coreService.setPatterns( dePatternData.categories,  'de');
            const date = new Date( 2019, 2, 14, 9, 3, 16, 28);
            expect( client2.i18nService.formatDate( date, 'fullDate') ).toEqual('Donnerstag, 14. März 2019');
            expect( client2.i18nService.formatDate( date, 'short') ).toEqual('14.03.19, 09:03');
        });
        it('handle param incorrect', () => {
            expect( () => {
                client2.coreService.setPatterns( dePatternData.categories, '');
            }).toThrow();
        });
    });

    describe( 'validate config', () => {
        it('should throw error if reqiured param undifined', () => {
            const errorMsg = `Paramater: 'ProductID' required for 'CoreService'`;
            expect( () => { i18nClient.createInstance({} as any); } ).toThrowError(errorMsg);
        });
    });
});
