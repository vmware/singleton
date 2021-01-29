/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { i18nClient, PatternCategories } from '../index';

describe('l10nService', () => {
    const baseConfig = {
        productID: 'TEST',
        component: 'demo',
        version: '1.0.0',
        host: 'localhost:4000',
        sourceBundle: {
            demo: 'demo string',
            hello: 'Hello {0}'
        },
        i18nScope: [ PatternCategories.NUMBER, PatternCategories.PLURAL ],
        isPseudo: true
    };
    let client = i18nClient.init( baseConfig );
    describe('get source string from source bundle', () => {
        expect( client.l10nService.getSourceString('demo') ).toEqual('demo string');
        expect( client.l10nService.getSourceString('hello') ).toEqual('Hello {0}');
    });
    describe('get message', () => {
        it( 'from source bundle' , () => {
            expect( client.l10nService.getMessage('demo') ).toEqual('demo string');
            expect( client.l10nService.getMessage('hello', ['World']) ).toEqual('Hello World');
        });
        it ('from translations', () => {
            const config = Object.assign({}, baseConfig, { language: 'de'});
            client = i18nClient.createInstance(config);
            client.coreService.setTranslations('de', {
                demo: 'demo-string',
                hello: 'Hallo {0}'
            });
            expect( client.l10nService.getMessage('demo') ).toEqual('demo-string');
            expect( client.l10nService.getMessage('hello', ['Welt']) ).toEqual('Hallo Welt');
        });
        it('should return oorigin string with pseudo tag when translation missed', () => {
            expect( client.l10nService.getTranslation('pseuao', 'pseudo string') ).toEqual('@@pseudo string@@');
        });
        it('should return null when key undifined', () => {
            expect( client.l10nService.getMessage('') ).toEqual(null);
        });
        it('should return correct translation with plural', () => {
            const key = 'plural',
                    message = '{ 0, plural, =0 {No message.} one{One message.} other {There are # messages.}}';
            const other = client.l10nService.getTranslation( key, message, ['3']);
            const one = client.l10nService.getTranslation( key, message, ['1']);
            expect( other ).toEqual('@@There are 3 messages.@@');
            expect( one ).toEqual('@@One message.@@');
        });
    });
});

