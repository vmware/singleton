/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { ParamaterError } from '../src/exceptions';

describe('expections', () => {
    describe('paramaterError', () => {
        it('should throw error', () => {
            const errMsg = `Paramater: 'test string' required for 'paramaterError'`;
            expect(() => {
                throw ( ParamaterError('paramaterError', 'test string') );
            })
            .toThrowError(errMsg);
        });
    });
});
