/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { isDefined, resolveLanguageTag, getBrowserCultureLang } from '../src/utils';

describe('utils', () => {

    describe('isDefined', () => {
        it( 'should return false when value is null undifined or empty string', () => {
            expect( isDefined( null ) ).toBeFalsy();
            expect( isDefined( undefined ) ).toBeFalsy();
            expect( isDefined( '' ) ).toBeFalsy();
        });
        it('should return true when value is defined and not empty', () => {
            expect( isDefined( 1 ) ).toBeTruthy();
            expect( isDefined( 'a' ) ).toBeTruthy();
            expect( isDefined( ' ' ) ).toBeTruthy();
        });
    });
    describe('resolveLanguageTag', () => {
        it('should transform _ to - in laguage tag and to lower case', () => {
            expect( resolveLanguageTag('en-US') ).toEqual('en-us');
            expect( resolveLanguageTag('en_US') ).toEqual('en-us');
        });
        it('should return origin value if value not defined', () => {
            expect( resolveLanguageTag('') ).toEqual('');
            expect( resolveLanguageTag(undefined) ).toEqual(undefined);
        });
    });
    describe('getBrowserCultureLang', () => {
        it('should return correct value', () => {
            let browserCultureLang: any = window.navigator.languages ? window.navigator.languages[0] : null;
            browserCultureLang = browserCultureLang ||
                window.navigator.language;
            expect( getBrowserCultureLang() ).toEqual(browserCultureLang);
        });
    });
});
