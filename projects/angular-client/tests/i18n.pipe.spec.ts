/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injector } from '@angular/core';
import { TestBed, getTestBed } from '@angular/core/testing';
import { I18nService, VIPModule } from '../index';
import { DateFormatPipe, NumberFormatPipe, CurrencyFormatPipe, PercentFormatPipe } from "../src/i18n.pipe";
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('I18n Pipe', () => {
    let injector: Injector;
    let i18nService: I18nService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule,
                VIPModule.forRoot()
            ]
        });
        injector = getTestBed();
        i18nService = injector.get(I18nService);
    });

    it('Date format pipe', () => {
        const datePipe = new DateFormatPipe(i18nService);
        expect(() => datePipe.transform(new Date())).not.toThrow();
        expect(datePipe.transform('')).toBeNull();
        datePipe.ngOnDestroy();
    });

    it('Number format pipe', () => {
        const pipe = new NumberFormatPipe(i18nService);
        expect(() => pipe.transform(1)).not.toThrow();
        expect(pipe.transform('')).toBeNull();
        pipe.ngOnDestroy();
    });

    it('Currency format pipe', () => {
        const pipe = new CurrencyFormatPipe(i18nService);
        expect(() => pipe.transform(1, 'USD')).not.toThrow();
        expect(pipe.transform('', 'USD')).toBeNull();
        pipe.ngOnDestroy();
    });

    it('Percent format pipe', () => {
        const pipe = new PercentFormatPipe(i18nService);
        expect(() => pipe.transform(1)).not.toThrow();
        expect(pipe.transform('')).toBeNull();
        pipe.ngOnDestroy();
    });
});
