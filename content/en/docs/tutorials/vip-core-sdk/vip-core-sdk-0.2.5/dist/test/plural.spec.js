"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const plural_message_parser_1 = require("../src/formatters/plural/plural.message.parser");
describe('plural message parser', () => {
    describe('parse string to AST', () => {
        it('string', () => {
            const format = plural_message_parser_1.parse('just some text');
            expect(format).toEqual(['just some text']);
        });
        it('placeholder', () => {
            const format = plural_message_parser_1.parse('There are some {placeholder}.');
            expect(format).toEqual(['There are some ', ['placeholder'], '.']);
        });
        it('plural', () => {
            const format = plural_message_parser_1.parse('{p, plural, one{one} other{other}}');
            expect(format).toEqual([
                ['p', 'plural', {
                        one: ['one'],
                        other: ['other']
                    }]
            ]);
        });
        it('should throw error if other undifined', () => {
            expect(() => { plural_message_parser_1.parse('{day, plural, one{Monday}}'); }).toThrowError();
        });
    });
});
