/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injector } from '@angular/core';
import { TestBed, getTestBed } from '@angular/core/testing';
import { VIPModule, LocaleService } from '../index';

describe('I18n Pipe', () => {
    let injector: Injector;
    let localeService: LocaleService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                VIPModule.forRoot()
            ]
        });
        injector = getTestBed();
        localeService = injector.get(LocaleService);
    });

    it('get language and region from current lcoale', () => {
        localeService.setDefaultLocale({
            languageCode: 'zh-Hans',
            languageName: '简体中文',
            regionCode: 'CN',
            regionName: '中国大陆'
        })
        expect(localeService.getCurrentLanguage()).toEqual('zh-Hans');
        expect(localeService.getCurrentRegion()).toEqual('CN');

        localeService.setCurrentLocale('zh-Hans-CN');
        expect(localeService.getCurrentLanguage()).toEqual('zh-Hans-CN');
        expect(localeService.getCurrentRegion()).toEqual(undefined);
    });

    it('get locale from language and region', () => {
        localeService.setCurrentLanguage('zh-Hans');
        localeService.setCurrentRegion('CN');
        expect(localeService.getCurrentLocale()).toEqual('zh-Hans-CN');
    });
});
