/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { parse } from '../src/formatters/plural/plural.message.parser';

describe('plural message parser', () => {
    describe('parse string to AST', () => {
        it( 'string', () => {
            const format = parse('just some text');
            expect(format).toEqual(['just some text']);
        });
        it( 'placeholder', () => {
            const format = parse('There are some {placeholder}.');
            expect(format).toEqual([ 'There are some ', ['placeholder'], '.']);
        });
        it('plural', () => {
            const format = parse('{p, plural, one{one} other{other}}');
            expect(format).toEqual([
                [ 'p', 'plural', {
                    one: [ 'one' ],
                    other: [ 'other' ]
                } ]
            ]);
        });
        it('should throw error if other undifined', () => {
            expect( () => { parse('{day, plural, one{Monday}}'); } ).toThrowError();
        });
    });
});
