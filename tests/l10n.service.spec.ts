/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPModule, L10nService, VIPService, LocaleService, I18nLoader } from '../index';
import { Injector, APP_INITIALIZER } from '@angular/core';
import { getTestBed, TestBed } from '@angular/core/testing';
import { TestLoader, baseConfig } from './test.util';


describe('L10nService', () => {
    let injector: Injector;
    let l10nService: L10nService;

    const config = {
        language: 'zh-Hans',
        isPseudo: false,
        translationBundles: {
            'zh-Hans': {
                'demo.string.two': '{0, plural, one {VIP Angular 客户端有一个用户。} other {VIP Angular 客户端有 # 用户。}}',
                'application.title': '欢迎来到 VIP Angular 示例应用!',
                'demo.string.one': 'VIP Angular 客户端同时支持 {0} 和 {1}'
            }
        }
    };
    function initVIPConfig(service: VIPService) {
        return () => service.initData(Object.assign(baseConfig, config));
    }

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                VIPModule.forRoot({
                    coreLoader: {
                        provide: I18nLoader,
                        useClass: TestLoader
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
        const localeService = injector.get(LocaleService);
        localeService.setCurrentLocale('zh-Hans');
    });

    it('should defined', () => {
        expect(L10nService).toBeDefined();
        expect(l10nService).toBeDefined();
        expect(l10nService instanceof L10nService).toBeTruthy();
    });

    it('should get translation by key', () => {
        expect(l10nService.getMessage('application.title')).toEqual(config.translationBundles['zh-Hans']['application.title']);
    });

    it('should fallback to source locale', () => {
        expect(l10nService.translate('test.no.translation', 'source string')).toEqual('source string');
        expect(l10nService.translate('test.with.param', '{0} source string', ['Second'])).toEqual('Second source string');
    });
});
