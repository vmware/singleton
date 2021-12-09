/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Plural } from '../src/formatters/plural/plural.formatter';

describe('Plural Formatter', () => {
    const plural = new Plural();
    describe('get plural function by locale', () => {
        it('locale can be lower case', () => {
            const EnFn = plural.getFunc('EN');
            const type = EnFn(1);
            expect(type).toEqual('one');
        });

        it('locale can be resolved', () => {
            const Fn = plural.getFunc('zh-Hans-cn');
            const type = Fn(0);
            expect(type).toEqual('other');
        });

        it('unavailable locale', () => {
            const Fn = plural.getFunc('zz');
            expect(Fn).toBeUndefined();
        });
    });
});
