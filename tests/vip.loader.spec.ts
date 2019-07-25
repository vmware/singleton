/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injector } from '@angular/core';
import { TestBed, getTestBed } from '@angular/core/testing';
import { VIPModule, VIPService, I18nLoader } from '../index';
import { TestLoader } from './test.util';

describe('I18nLoader', () => {
    let injector: Injector;
    let service: VIPService;
    let transloader: I18nLoader;
    it('should provide I18nLoader', () => {
        TestBed.configureTestingModule({
            imports: [
                VIPModule.forRoot({
                    coreLoader: {
                        provide: I18nLoader,
                        useClass: TestLoader
                    }
                })
            ]
        });
        injector = getTestBed();
        service = injector.get(VIPService);
        transloader = injector.get(I18nLoader);
        expect(service).toBeDefined();
        expect(transloader).toBeDefined();
    });
});
