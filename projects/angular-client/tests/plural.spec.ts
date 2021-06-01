/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { MessageFormat } from '../src/formatters/message.formatter';
import { parse } from '../src/formatters/plural/plural.message.parser';

describe('Plural Format', () => {
    const messageFormat = new MessageFormat( null );
    describe('parse string to AST', () => {
        it('string', () => {
            const format = parse('just some text');
            expect(format).toEqual(['just some text']);
        });
        it('placeholder', () => {
            const format = parse('There are some {placeholder}.');
            expect(format).toEqual(['There are some ', ['placeholder'], '.']);
        });
        it('plural', () => {
            const ast = [['p', 'plural', {
                one: ['one'],
                other: ['other']
            }]];
            const format = parse('{p, plural, one{one} other{other}}');
            expect(format).toEqual(ast);
        });
        it('plural with variable', () => {
            const ast = [['str', 'plural', {
                one: ['one string'],
                other: [['#'], ' strings']
            }]];
            const format = parse('{str, plural, one{one string} other{# strings}}');
            expect(format).toEqual(ast);
        });
    });

    describe('message format interpret', () => {
        it('string', () => {
            const format = messageFormat.interpret(['just some text']);
            expect(format).toEqual('just some text');
        });
        it('placeholders', () => {
            const format = messageFormat.interpret(['There are some ', ['placeholder'], '.'], { placeholder: 'strings' });
            expect(format).toEqual('There are some strings.');
        });
        it('unknown placeholder', () => {
            const format = messageFormat.interpret([['a'], ['b'], ['c']]);
            expect(format).toEqual('abc');
        });
        it('handles missing args', () => {
            const ast = [['placeholder']];
            const format = messageFormat.interpret(ast);
            expect(format).toEqual('placeholder');
            expect(messageFormat.interpret(ast, { a: null })).toEqual('placeholder');
        });
    });
});
