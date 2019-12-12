"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const utils_1 = require("../src/utils");
describe('utils', () => {
    describe('isDefined', () => {
        it('should return false when value is null undifined or empty string', () => {
            expect(utils_1.isDefined(null)).toBeFalsy();
            expect(utils_1.isDefined(undefined)).toBeFalsy();
            expect(utils_1.isDefined('')).toBeFalsy();
        });
        it('should return true when value is defined and not empty', () => {
            expect(utils_1.isDefined(1)).toBeTruthy();
            expect(utils_1.isDefined('a')).toBeTruthy();
            expect(utils_1.isDefined(' ')).toBeTruthy();
        });
    });
    describe('resolveLanguageTag', () => {
        it('should transform _ to - in laguage tag and to lower case', () => {
            expect(utils_1.resolveLanguageTag('en-US')).toEqual('en-us');
            expect(utils_1.resolveLanguageTag('en_US')).toEqual('en-us');
        });
        it('should return origin value if value not defined', () => {
            expect(utils_1.resolveLanguageTag('')).toEqual('');
            expect(utils_1.resolveLanguageTag(undefined)).toEqual(undefined);
        });
    });
    describe('getBrowserCultureLang', () => {
        it('should return correct value', () => {
            let browserCultureLang = window.navigator.languages ? window.navigator.languages[0] : null;
            browserCultureLang = browserCultureLang ||
                window.navigator.language;
            expect(utils_1.getBrowserCultureLang()).toEqual(browserCultureLang);
        });
    });
});
