/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injector, APP_INITIALIZER, Injectable } from '@angular/core';
import { TestBed, getTestBed } from '@angular/core/testing';
import { L10nService, VIPModule, VIPService, LocaleService, I18nLoader, getNameSpace } from '../index';
import { L10nPipe, L10nPipePlus } from '../src/l10n.pipe';
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
    @Injectable()
    class VIPRestLoader extends TestLoader {
        getTranslationByComponent(languae: string): Observable < any > {
            const namespace = getNameSpace(baseConfig);
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
        return () => service.initData( baseConfig );
    }

    let injector: Injector;
    let l10nService: L10nService;
    let l10nPipe: L10nPipe;
    let plusPipe: L10nPipePlus;

    let localeService: LocaleService;
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
        plusPipe = new L10nPipePlus(l10nService);
        localeService = injector.get(LocaleService);
        localeService.setCurrentLocale(locale);
    });

    it('should defined', () => {
        expect(L10nPipe).toBeDefined();
        expect(plusPipe).toBeDefined();
        expect(l10nPipe instanceof L10nPipe).toBeTruthy();
        expect(plusPipe instanceof L10nPipePlus).toBeTruthy();
    });

    it('should transform with translation', () => {
        const res1 = l10nPipe.transform('test.string', 'test string for en');
        const res2 = plusPipe.transform('test.string');
        const expectRes = 'test string for other locale';
        expect(res1).toEqual(expectRes);
        expect(res2).toEqual(expectRes);
        locale = localeArr[1];
    });

    it('should transform with string in source', () => {
        localeService.setCurrentLocale(localeArr[1]);
        const sourceStr = 'test string for en';
        const res1 = l10nPipe.transform('test.string', sourceStr);
        expect(res1).toEqual(sourceStr);
        const res2 = plusPipe.transform('application.title');
        expect(res2).toEqual('Welcome to VIP Angular sample application!');
        locale = localeArr[2];
    });

    it('should translate with no key throw error', () => {
        expect(() => {
            l10nPipe.transform('', 'test string for en');
        }).toThrowError(`InvalidParamater: 'key in L10nPipe'`);
        expect(() => {
            plusPipe.transform('');
        }).toThrowError(`InvalidParamater: 'key in L10nPipePlus'`);
        l10nPipe.ngOnDestroy();
        plusPipe.ngOnDestroy();
    });
});
