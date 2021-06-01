/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPModule, VIPService, I18nService, I18nLoader } from '../index';
import { Injectable, Injector } from '@angular/core';
// tslint:disable-next-line: import-blacklist
import { Observable, of } from 'rxjs';
import { getTestBed, TestBed } from '@angular/core/testing';
import { TestLoader, baseConfig } from './test.util';

/**
 * Mock loader for testing purpose.
 * @class VIPRestLoader
 * @implements {I18nLoader}
 */
const dataForSource =  [
    {languageTag: 'zh-Hant', displayName: 'Traditional Chinese'},
    {languageTag: 'en', displayName: 'English'},
    {languageTag: 'de', displayName: 'Deutsch'}
];

@Injectable()
class FakeLoader extends TestLoader {
    getSupportedLanguages(): Observable<any> {
        return of( dataForSource );
    }
}

describe('I18nService', () => {
    let injector: Injector;
    let i18nService: I18nService;
    let vipService: VIPService;

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
        vipService = injector.get(VIPService);
    });

    it('should defined', () => {
        expect(i18nService).toBeDefined();
        expect(i18nService instanceof I18nService).toBeTruthy();
    });

    it('should get languages for source locale', () => {
        vipService.initData(baseConfig);
        i18nService.getSupportedLanguages().then(
            (languages: any) => {
                expect(languages).toEqual( dataForSource );
            }
        );
    });
});
