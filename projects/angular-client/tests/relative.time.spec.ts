/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPModule, VIPService, I18nService, I18nLoader } from '../index';
import { Injectable, Injector } from '@angular/core';
// tslint:disable-next-line: import-blacklist
import { getTestBed, TestBed } from '@angular/core/testing';

import { RelativeTimeFormatter } from '../src/formatters/relative.time';
import { TestLoader } from './test.util';

@Injectable()
class FakeLoader extends TestLoader {
   
}
describe('Relative time formatter', () => {
    const formatter = new RelativeTimeFormatter();
    const unit = {
        SECOND: 'second',
        MINUTE: 'minute',
        HOUR: 'hour',
        DAY: 'day',
        MONTH: 'month',
        YEAR: 'year'
    };
    const t = ( second: number ) => {
        return new Date(2019, 9, 1, 8, 0, second);
    };
    describe('get offset', () => {
        it('seconds', () => {
            // 0s
            const offset = formatter.getOffset(t(0), t(44));
            expect(offset.offset).toEqual(44);
            expect(offset.unit).toEqual(unit.SECOND);
        });
        it('minutes', () => {
            // 45s - 1min
            const offset = formatter.getOffset(t(0), t(45));
            expect(offset.offset).toEqual(1);
            expect(offset.unit).toEqual(unit.MINUTE);

            // 44 min
            const offset2 = formatter.getOffset(t(0), t(2640));
            expect(offset2.offset).toEqual(44);
            expect(offset2.unit).toEqual(unit.MINUTE);
        });

        it('hour', () => {
            // 45min - 1hr
            const offset = formatter.getOffset(t(0), t(2700));
            expect(offset.offset).toEqual(1);
            expect(offset.unit).toEqual(unit.HOUR);

            // 90min - 2hr
            const offset2 = formatter.getOffset(t(0), t(5400));
            expect(offset2.offset).toEqual(2);
            expect(offset2.unit).toEqual(unit.HOUR);

            // 21hr

        });
        it('days', () => {
            // 22hr - 1day
            const offset = formatter.getOffset(t(0), t(79200));
            expect(offset.offset).toEqual(1);
            expect(offset.unit).toEqual(unit.DAY);

            // 35hr - 1day
            const offset2 = formatter.getOffset(new Date(2019, 9, 1, 0), new Date(2019, 9, 2, 11));
            expect(offset2.offset).toEqual(1);
            expect(offset2.unit).toEqual(unit.DAY);

            // 36hr - 2days
            const offset3 = formatter.getOffset(new Date(2019, 9, 1, 0), new Date(2019, 9, 2, 12));
            expect(offset3.offset).toEqual(2);
            expect(offset3.unit).toEqual(unit.DAY);

            // 25d - 25days
            const offset4 = formatter.getOffset(new Date(2019, 9, 1), new Date(2019, 9, 26));
            expect(offset4.offset).toEqual(25);
            expect(offset4.unit).toEqual(unit.DAY);
        });

        it('months', () => {
            // 26d -1month
            const offset = formatter.getOffset(new Date(2019, 9, 1), new Date(2019, 9, 27));
            expect(offset.offset).toEqual(1);
            expect(offset.unit).toEqual(unit.MONTH);
            // 45d -1month
            const offset1 = formatter.getOffset(new Date(2019, 9, 1), new Date(2019, 10, 15));
            expect(offset1.offset).toEqual(1);
            expect(offset1.unit).toEqual(unit.MONTH);
            // 46d - 2months
            const offset2 = formatter.getOffset(new Date(2019, 9, 1), new Date(2019, 10, 16));
            expect(offset2.offset).toEqual(2);
            expect(offset2.unit).toEqual(unit.MONTH);
            // 319d - 10month
            const offset3 = formatter.getOffset(new Date(2019, 0, 1), new Date(2019, 10, 16));
            expect(offset3.offset).toEqual(10);
            expect(offset3.unit).toEqual(unit.MONTH);
        });
        it('years', () => {
            // 320d - 1y
            const offset3 = formatter.getOffset(new Date(2019, 0, 1), new Date(2019, 10, 17));
            expect(offset3.offset).toEqual(1);
            expect(offset3.unit).toEqual(unit.YEAR);

            // 547d - 1y
            const offset4 = formatter.getOffset(new Date(2019, 0, 1), new Date(2020, 6, 1));
            expect(offset4.offset).toEqual(1);
            expect(offset4.unit).toEqual(unit.YEAR);

            // 548d - 2ys
            const offset5 = formatter.getOffset(new Date(2019, 0, 1), new Date(2020, 6, 2));
            expect(offset5.offset).toEqual(2);
            expect(offset5.unit).toEqual(unit.YEAR);
        });
    });
    describe('formatRelativeTime', () => {
        let injector: Injector;
        let i18nService: I18nService;
        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [
                    VIPModule.forRoot({
                        coreLoader: {
                            provide: I18nLoader,
                            useClass: FakeLoader
                        }
                    })
                ]
            });
            injector = getTestBed();
            i18nService = injector.get(I18nService);
        });
        it('years', () => {
            const from = new Date(2018, 1),
                    to = new Date(2019, 1);
            expect(i18nService.formatRelativeTime(from, to, 'en')).toEqual('in 1 year');
            expect(i18nService.formatRelativeTime(from, to, 'en', { numeric: 'auto'})).toEqual('next year');
            expect(i18nService.formatRelativeTime(to, from, 'en')).toEqual('1 year ago');
            expect(i18nService.formatRelativeTime(to, from, 'en', { numeric: 'auto'})).toEqual('last year');
        });
        it('seconds', () => {
            const t = (second: number) => {
                return new Date(2019, 9, 22, 12, 30, second);
            }
            const alwaysF = ( from: Date, to: Date ) => { return i18nService.formatRelativeTime(from, to, 'en'); };
            expect( alwaysF(t(1), t(45)) ).toEqual('in 44 seconds');
            expect( alwaysF(t(1), t(46)) ).toEqual('in 1 minute');
        });

        it('days', () => {
            const t = (hour: number) => {
                return new Date(2019, 9, 22, hour, 30);
            }
            const alwaysF = ( from: Date, to: Date ) => { return i18nService.formatRelativeTime(from, to, 'en'); };
            const autoF = ( from: Date, to: Date ) => { return i18nService.formatRelativeTime(from, to, 'en', { numeric: 'auto'}); };
            expect( alwaysF(t(1), t(23)) ).toEqual('in 1 day');
            expect( autoF(t(1), t(23)) ).toEqual('tomorrow');
            expect( autoF(t(23), t(1)) ).toEqual('yesterday');
            expect( alwaysF(t(46), t(1)) ).toEqual('2 days ago');
        });
    });
});