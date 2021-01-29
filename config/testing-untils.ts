/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
/// <reference path="../node_modules/@types/jasmine/index.d.ts" />

/*
 Temporary file for referencing the TypeScript defs for Jasmine + some potentially
 utils for testing. Will change/adjust this once I find a better way of doing
 */

declare module jasmine {
    interface Matchers<T> {
        toHaveText(text: string): boolean;
        toContainText(text: string): boolean;
    }
}

beforeEach(() => {
    jasmine.addMatchers({
        toHaveText: function() {
            return {
                compare: function(actual, expectedText) {
                    const actualText = actual.textContent;
                    return {
                        pass: actualText === expectedText,
                        get message() {
                            return 'Expected ' + actualText + ' to equal ' + expectedText;
                        }
                    };
                }
            };
        },

        toContainText: function() {
            return {
                compare: function(actual, expectedText) {
                    const actualText = actual.textContent;
                    return {
                        pass: actualText.indexOf(expectedText) > -1,
                        get message() {
                            return 'Expected ' + actualText + ' to contain ' + expectedText;
                        }
                    };
                }
            };
        }
    });
});
