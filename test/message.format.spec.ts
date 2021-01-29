/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { MessageFormat } from '../src/formatters/message.formatter';

describe('interpret AST', () => {
    const messageFormat = new MessageFormat( null );
    it( 'string', () => {
        const format = messageFormat.interpret([ 'just some text' ] );
        expect(format).toEqual('just some text');
    });
    it( 'placeholders', () => {
        const format = messageFormat.interpret([ 'There are some ', ['placeholder'], '.'], { placeholder: 'strings'});
        expect(format).toEqual('There are some strings.');
    });
    it( 'unknown placeholder', () => {
        const format = messageFormat.interpret([ ['a'], ['b'], ['c']]);
        expect(format).toEqual('abc');
    });
    it( 'handles missing args', () => {
        const ast = [ ['placeholder'] ];
        const format = messageFormat.interpret(ast);
        expect(format).toEqual('placeholder');
        expect(messageFormat.interpret(ast, { a: null })).toEqual('placeholder');
    });
});
