/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPModule, L10nService, VIPService, LocaleService, I18nLoader, I18nContext } from '../index';
import { Injector, APP_INITIALIZER } from '@angular/core';
import { getTestBed, TestBed } from '@angular/core/testing';
import { TestLoader, baseConfig } from './test.util';

describe('I18nContext', () => {
    let injector: Injector;
    let i18nContext: I18nContext;
    let l10nService: L10nService;

    const config = {
        isPseudo: localStorage.getItem('vip.pseudoEnabled'),
        translationBundles: {
            'zh-Hans': {
                'application.title': '欢迎来到 VIP Angular 示例应用!',
                'demo.string.one': 'VIP Angular 客户端同时支持 {0} 和 {1}'
            }
        }
    };

    function initVIPConfig(service: VIPService, localeService: LocaleService, i18nContext: I18nContext) {
        const i18nEnable = false;
        const language = i18nContext.preferredLanguage || 'zh-Hans';
        i18nContext.i18nEnabled = i18nContext.i18nEnabled || i18nEnable;
        localeService.init(language);
        return () => service.initData(Object.assign({}, baseConfig, config));
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
                    deps: [VIPService, LocaleService, I18nContext],
                    multi: true
                }
            ]
        });
        injector = getTestBed();
        i18nContext = injector.get(I18nContext);
        l10nService = injector.get(L10nService);
    });

    it('should defined', () => {
        expect(i18nContext).toBeDefined();
        expect(i18nContext).toBeDefined();
        expect(i18nContext instanceof I18nContext).toBeTruthy();
    });

    it('all undefined', () => {
        // pseudo = false, i18nEnabled = false, language = zh-Hans
        expect(l10nService.getMessage('application.title')).toEqual('Welcome to VIP Angular sample application!');
        i18nContext.i18nEnabled = true;
    });

    it('enable i18n', () => {
        // pseudo = false, i18nEnabled = true, language = zh-Hans : show translation
        expect(l10nService.getMessage('application.title')).toEqual(config.translationBundles['zh-Hans']['application.title']);
        localStorage.setItem('vip.pseudoEnabled', 'true');
    });

    it('enable pseudo', () => {
        // pseudoEnabled = true, i18nEnabled = true, language = zh-Hans : show pseudo translation
        expect(l10nService.getMessage('demo.string.one', ['i18n', 'l10n'])).toEqual('VIP Angular 客户端同时支持 i18n 和 l10n');
        expect(l10nService.getMessage('test.no.translation')).toEqual('@@source string@@');
        localStorage.removeItem('vip.i18nEnabled');
    });

    it('enable pseudo abd disabled i18n', () => {
        // pseudoEnabled = true, i18nEnabled = false, language = zh-Hans : show source string
        expect(l10nService.getMessage('application.title')).toEqual('Welcome to VIP Angular sample application!');
    });

    it('should get value from localStorage', () => {
        expect(i18nContext.preferredRegion).toEqual(undefined);
        i18nContext.preferredRegion = 'US';
        expect(i18nContext.preferredRegion).toEqual('US');
        localStorage.clear();
    })
});
