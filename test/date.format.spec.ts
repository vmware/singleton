/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { DateFormatter } from '../index';
import dePatternData from './locale.de';
import sourcePatternData from '../src/data/locale_en';

describe('date formtter' , () => {
    const formatter = new DateFormatter();
    const date = new Date(2019, 2, 22, 9, 3, 1, 550);
    const dataForDate = sourcePatternData.categories.dates;

    describe('support input type', () => {
        it('should support date', () => {
            expect( formatter.getStandardTime(date) ).toEqual(date) ;
        });

        it('should support time step', () => {
            expect( formatter.getStandardTime(date.getTime()) )
                    .toEqual( date );
        });

        it('should support numeric strings', () => {
            expect( formatter.getStandardTime(date.getTime().toString()) )
                    .toEqual( date );
        });

        it('should support ISO string', () => {
            const date1 = new Date(2019, 2, 22, 17, 3, 1, 550);
            expect( formatter.getStandardTime(date1.toISOString()) )
                    .toEqual( date1 );
            // expect( formatter.getStandardTime('2019-03-22T09:03:01.000233Z').toString() )
                    // .toEqual( date1.toString() );
        });

        it('should return origin string if string invalid', () => {
            expect( formatter.getStandardTime('')).toEqual('');
            expect( formatter.getStandardTime(null)).toEqual(null);
        });
    });

    describe('format date according pattern', () => {
        it('should return correct formatted date in english', () => {
            const dateFixtures: { [key: string]: any} = {
                short: /3\/22\/19, 9:03 AM/,
                long: /March 22, 2019 at 9:03:01 AM GMT(\+|-)\d/,
                full: /Friday, March 22, 2019 at 9:03:01 AM GMT(\+|-)\d{2}:\d{2}/,
                medium: /Mar 22, 2019, 9:03:01 AM/,
                shortTime: /9:03 AM/,
                mediumTime:	/9:03:01 AM/,
                longTime: /9:03:01 AM GMT(\+|-)\d/,
                fullTime: /9:03:01 AM GMT(\+|-)\d{2}:\d{2}/,
                shortDate: /3\/22\/19/,
                longDate: /March 22, 2019/,
                mediumDate: /Mar 22, 2019/,
                fullDate: /Friday, March 22, 2019/
            };
            Object.keys(dateFixtures).forEach((pattern: string) => {
                const forttedDate = formatter.getformattedString(date, pattern, dataForDate);
                expect( forttedDate ).toMatch(dateFixtures[pattern]);
            });
        });
        it('should return correct formatted date in various language', () => {
            const dateDateOfDe = dePatternData.categories.dates;
            const deReg = /Freitag, 22. März 2019 um 09:03:01 GMT(\+|-)\d{2}:\d{2}/;
            expect(formatter.getformattedString(date, 'full', dateDateOfDe)).toMatch( deReg );
        });
        it('should format with timezones', () => {
            const dateFixtures: { [key: string]: any} = {
                z: /GMT(\+|-)\d/,
                zzzz: /GMT(\+|-)\d{2}\:30/,
                Z: /(\+|-)\d{2}30/,
            };
            Object.keys(dateFixtures).forEach((pattern: string) => {
                expect(formatter.getformattedString(date, pattern, dataForDate, '-', '+0430')).toMatch(dateFixtures[pattern]);
            });
        });
        it('should format invalid in Safari ISO date', () => {
            const time = formatter.getStandardTime('2019-02-22T09:03:01+0000');
            expect( formatter.getformattedString(time, 'mediumDate', dataForDate) ).toEqual('Feb 22, 2019');
        });

        it('should format invalid in IE ISO date', () => {
            const time = formatter.getStandardTime('2019-02-22T09:03:01.014-0500');
             expect( formatter.getformattedString(time, 'mediumDate', dataForDate) ).toEqual('Feb 22, 2019');
        });

        it('should format correctly with iso strings that contain time', () => {
            const time = formatter.getStandardTime('2019-02-22T09:03:01');
            expect( formatter.getformattedString(time, 'dd-MM-yyyy HH:mm', dataForDate) ).toMatch(/22-02-2019 \d{2}:\d{2}/);
        });
    });
});
