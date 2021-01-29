/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injector, APP_INITIALIZER } from '@angular/core';
import { TestBed, getTestBed } from '@angular/core/testing';
import { L10nService, VIPModule, VIPService, LocaleService, I18nLoader, getNameSpace } from '../index';
import { L10nPipe } from '../src/l10n.pipe';
import { Observable, of } from 'rxjs';
import { TestLoader, baseConfig } from './test.util';


/**
 * Mock loader for testing purpose.
 * @class VIPRestLoader
 * @implements {I18nLoader}
 */


// expect test it with component
describe('L10nPipe', () => {
    const localeArr: ArrayLike < string > = ['zh-Hans', 'en', 'Ja'];
    let locale = localeArr[0];
    const config = Object.assign( baseConfig, { language: locale});
    class VIPRestLoader extends TestLoader {
        getTranslationByComponent(languae: string): Observable < any > {
            const namespace = getNameSpace(config);
            const en = {'test.string': 'test string for en'};
            const other = {'test.string': 'test string for other locale'};
            const data = languae !== 'en' ? other  : en;
            const translations: {[key: string]: any} = {};
                translations[namespace] = data;
            const response = of({ messages: translations});
            return response;
        }
        getLocaleData(languae?: string) {
            return this.getTranslationByComponent(languae);
        }
    }
    function initVIPConfig(service: VIPService) {
        return () => service.initData( config );
    }

    let injector: Injector;
    let l10nService: L10nService;
    let l10nPipe: L10nPipe;

    beforeEach(() => {

        TestBed.configureTestingModule({
            imports: [
                VIPModule.forRoot({
                    coreLoader: {
                        provide: I18nLoader,
                        useClass: VIPRestLoader
                    }
                })
            ],
            providers: [
                {
                    provide: APP_INITIALIZER,
                    useFactory: initVIPConfig,
                    deps: [VIPService],
                    multi: true
                }
            ]
        });

        injector = getTestBed();
        l10nService = injector.get(L10nService);
        l10nPipe = new L10nPipe(l10nService);
        const localeService = injector.get(LocaleService);
        localeService.setCurrentLocale(locale);
    });

    it('should defined', () => {
        expect(L10nPipe).toBeDefined();
        expect(l10nPipe).toBeDefined();
        expect(l10nPipe instanceof L10nPipe).toBeTruthy();

    });

    it('should transform with translation', () => {
        const res = l10nPipe.transform('test.string', 'test string for en');
        expect(res).toEqual('test string for other locale');
        locale = localeArr[1];
    });

    it('should transform with string in source', () => {
        const res = l10nPipe.transform('test.string', 'test string for en');
        expect(res).toEqual('test string for en');
        locale = localeArr[2];
    });

    it('should translate with no key throw error', () => {
        expect(() => {
            l10nPipe.transform('', 'test string for en');
        }).toThrowError(`InvalidParamater: 'key in L10nPipe'`);
    });
});
