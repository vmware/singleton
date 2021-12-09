/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import localeDe from './locale_de';
import { DateFormatter } from '../src/formatters/date.formatter';
import { SOURCE_LOCALE_DATA } from '../src/data/locale_en';

describe('Date Formatter', () => {

    const date = new Date(2018, 1, 22, 9, 3, 1, 550);
    const formatter = new DateFormatter();
    const dataForDate = SOURCE_LOCALE_DATA.categories.dates;

    describe('supports', () => {

        it('should support date', () => {
            expect(() => { formatter.getStandardTime(date); }).not.toThrow();
        });

        it('should support number', () => {
            expect(() => { formatter.getStandardTime(1521680581000); }).not.toThrow();
        });

        it('should support numeric strings', () => {
            expect(() => { formatter.getStandardTime('1521680581000'); }).not.toThrow();
            // expect(formatter.getStandardTime('123')).toBe(new Date(1));
        });

        it('should support decimal strings', () => {
            expect(() => { formatter.getStandardTime(1521680581000.22); }).not.toThrow();
        });

        it('should support ISO string', () => {
            expect(() => { formatter.getStandardTime('2018-02-22T09:03:01Z'); }).not.toThrow();
            expect(() => { formatter.getStandardTime('2018-07-19T18:30:54.000233Z'); }).not.toThrow();
        });

        it('should return origin string for empty string', () => {
            expect(formatter.getStandardTime(null)).toEqual(null);
            expect(formatter.getStandardTime('')).toEqual('');
            expect(formatter.getStandardTime('abc')).toEqual('abc');
        });

    });

    describe('transform', () => {
        it('should format with pattern aliases', () => {
            const dateFixtures: { [key: string]: any } = {
                short: '2/22/18, 9:03 AM',
                long: /February 22, 2018 at 9:03:01 AM GMT(\+|-)\d/,
                full: /Thursday, February 22, 2018 at 9:03:01 AM GMT(\+|-)\d{2}:\d{2}/,
                medium: 'Feb 22, 2018, 9:03:01 AM',
                shortTime: '9:03 AM',
                mediumTime: '9:03:01 AM',
                longTime: /9:03:01 AM GMT(\+|-)\d/,
                fullTime: /9:03:01 AM GMT(\+|-)\d{2}:\d{2}/,
                shortDate: '2/22/18',
                longDate: 'February 22, 2018',
                mediumDate: 'Feb 22, 2018',
                fullDate: 'Thursday, February 22, 2018'
            };
            Object.keys(dateFixtures).forEach((pattern: string) => {
                expect(formatter.getformattedString(date, pattern, dataForDate)).toMatch(dateFixtures[pattern]);
            });
        });

        it('should format with timezones', () => {
            const dateFixtures: { [key: string]: any } = {
                z: /GMT(\+|-)\d/,
                zzzz: /GMT(\+|-)\d{2}\:30/,
                Z: /(\+|-)\d{2}30/,
            };
            Object.keys(dateFixtures).forEach((pattern: string) => {
                expect(formatter.getformattedString(date, pattern, dataForDate, '-', '+0430')).toMatch(dateFixtures[pattern]);
            });
        });


        it('should format invalid in Safari ISO date', () => {
            const dateObj = formatter.getStandardTime('2018-02-22T09:03:01+0000');
            expect(formatter.getformattedString(dateObj, 'mediumDate', dataForDate)).toEqual('Feb 22, 2018');
        });

        it('should format invalid in IE ISO date', () => {
            const dateObj = formatter.getStandardTime('2018-02-22T09:03:01.014-0500');
            expect(formatter.getformattedString(dateObj, 'mediumDate', dataForDate)).toEqual('Feb 22, 2018');
        });

        it('should format correctly with iso strings that contain time', () => {
            const dateObj = formatter.getStandardTime('2018-02-22T09:03:01');
            expect(formatter.getformattedString(dateObj, 'dd-MM-yyyy HH:mm', dataForDate)).toMatch(/22-02-2018 \d{2}:\d{2}/);
        });
});

describe('transform in various locale', () => {
    it('should format the date correctly in various locales', () => {
        const dateFormatter = new DateFormatter();
        const deReg = /Donnerstag, 22. Februar 2018 um 09:03:01 GMT(\+|-)\d{2}:\d{2}/;
        expect(dateFormatter.getformattedString(date, 'full', localeDe.categories.dates)).toMatch(deReg);
    });
});
});
