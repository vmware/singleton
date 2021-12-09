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
        isPseudo: false,
        translationBundles: {
            'zh-Hans': {
                'demo.string.two': '{0, plural, one {VIP Angular 客户端有一个用户。} other {VIP Angular 客户端有 # 用户。}}',
                'application': '欢迎来到 VIP Angular 示例应用!',
                'demo.string.one': 'VIP Angular 客户端同时支持 {0} 和 {1}',
                'test.split.message-default': '点击链接 ##1 获取最新 ##2 文件。',
                'test.split.message-custom': `点击链接 '1 获取最新 '2 文件。`,
                'test.param-object': '懒加载模块来自于{0}'
            }
        }
    };

    function initVIPConfig(service: VIPService, localeService: LocaleService) {
        localeService.init('zh-Hans');
        const i18nConfig = Object.assign({}, baseConfig, config);
        return () => service.initData(i18nConfig);
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
                    deps: [VIPService, LocaleService],
                    multi: true
                }
            ]
        });
        injector = getTestBed();
        l10nService = injector.get(L10nService);
    });

    it('should defined', () => {
        expect(L10nService).toBeDefined();
        expect(l10nService).toBeDefined();
        expect(l10nService instanceof L10nService).toBeTruthy();
    });

    it('should get translation by key', () => {
        expect(l10nService.getMessage('application')).toEqual(config.translationBundles['zh-Hans']['application']);
    });

    it('should fallback to source locale', () => {
        expect(l10nService.translate('test.no.translation', 'source string')).toEqual('source string');
        expect(l10nService.translate('test.with.param', '{0} source string', ['Second'])).toEqual('Second source string');
    });

    it('should call registerSourceBundles method along with souce bundle objects as parameters', () => {
        l10nService.registerSourceBundles({ ENGLISH: 'source bundles' });
        expect(l10nService.getMessage('ENGLISH')).toEqual('source bundles');
    });

    it('whether key exists in resource', () => {
        // current locale
        expect(l10nService.isExistKey('application')).toEqual(true);
        expect(l10nService.isExistKey('application.title')).toEqual(false);

        // source locale
        expect(l10nService.isExistKey('application.title', 'en')).toEqual(true);
        expect(l10nService.isExistKey('application', 'en')).toEqual(false);
    });

    it('should getSplitedMessage', () => {
        const expectRes = ['点击链接 ', ' 获取最新 ', ' 文件。'];
        expect(l10nService.getSplitedMessage('test.split.message-default')).toEqual(expectRes);

        const customReg = /\'\d+/;
        expect(l10nService.getSplitedMessage(
            'test.split.message-custom', undefined, undefined, customReg
        )).toEqual(expectRes);
    })

    it('should turn object param into string', () => {
        const translatedStr = l10nService.getMessage(
            'test.param-object',
            [{
                listItems: ['Dascom', 'Vmware', 'Google'],
                separatorType: '+',
                type: 1
            }]
        );
        expect(translatedStr).toEqual('懒加载模块来自于Dascom+Vmware+Google');

        const consoleErrorSpy = spyOn(console, 'error');
        l10nService.getMessage(
            'test.param-object',
            [{
                type: 2, listItems: ['Dascom', 'Vmware', 'Google'],
                separatorType: '+',
            }]);
        expect(consoleErrorSpy).toHaveBeenCalledTimes(1);
    })
});
