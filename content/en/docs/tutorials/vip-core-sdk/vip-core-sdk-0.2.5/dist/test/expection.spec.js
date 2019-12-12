"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const exceptions_1 = require("../src/exceptions");
describe('expections', () => {
    describe('paramaterError', () => {
        it('should throw error', () => {
            const errMsg = `Paramater: 'test string' required for 'paramaterError'`;
            expect(() => {
                throw (exceptions_1.ParamaterError('paramaterError', 'test string'));
            })
                .toThrowError(errMsg);
        });
    });
});
