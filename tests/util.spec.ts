/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { format, isDefined, equals, getBrowserCultureLang } from '../src/util';

describe('parseValue and isDefined in util', () => {

    it('format message with param', () => {
        const message = 'name:{ 1 }, jod:{ 0 }',
              param = ['test', 'Jack'];
        const massageString = format(message, param);
        expect(massageString).toEqual('name:Jack, jod:test');
        const message1 = 'name:{ 11 }, jod:{ 0 }',
        param1 = ['test', 'Jack'];
        const massageString1 = format(message1, param1);
        expect(massageString1).toEqual('name:{ 11 }, jod:test');
        const message2 = 'name:{ 0 }, jod:{ 1 }',
        param2 = ['Jack', 'test'];
        const massageString2 = format(message2, param2);
        expect(massageString2).toEqual('name:Jack, jod:test');
    });

    it('should defined', () => {

        const str: any = undefined;
        const str2: any = null;
        const str3 = '';
        const str4 = 'str';
        const map = new Map();
        const obj = new Object();

        expect(isDefined(str)).toBeFalsy();
        expect(isDefined(str2)).toBeFalsy();
        expect(isDefined(str3)).toBeFalsy();
        expect(isDefined(str4)).toBeTruthy();
        expect(isDefined(map)).toBeTruthy();
        expect(isDefined(obj)).toBeTruthy();
        expect( getBrowserCultureLang() ).toBeDefined();

    });

    describe('equal should return right value', () => {
        it('should equal', () => {
            const obj1 = { name: 'Jack', age: '20' };
            const obj2 = { name: 'Jack', age: '20' };
            expect( equals( obj1, obj2) ).toBeTruthy();
        });
        it('should not equal', () => {
            const obj1 = { name: 'Jack', age: '20' };
            const obj2 = { name: 'Jack', age: 20 };
            expect( equals( obj1, obj2) ).toBeFalsy();
        });
    });
});
