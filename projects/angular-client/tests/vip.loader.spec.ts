/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { Injector } from '@angular/core';
import { TestBed, getTestBed } from '@angular/core/testing';
import { VIPModule, I18nLoader, PatternCategories, VIPServiceConstants } from '../index';
import { of } from 'rxjs';

describe('I18nLoader', () => {
    let injector: Injector;
    let i18nLoader: I18nLoader;
    let httpClient: HttpClient;

    const translation = {
        productName: 'vipngxsample',
        messages: { 'application.title': '欢迎来到 VIP Angular 示例应用!' }
    }
    const categories = { dates: {} };
    const mockTranslationAndPattern = {
        components: [
            translation
        ],
        pattern: {
            isExistPattern: true,
            localeID: "zh-Hans",
            categories: categories
        }
    };
    const baseConfig = {
        productID: 'vipngxsample',
        component: 'default',
        version: '1.0.0',
        language: 'zh-Hans',
        host: 'http://test:8000/',
        isPseudo: false,
    };
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule,
                VIPModule.forRoot()
            ]
        });
        injector = getTestBed();
        httpClient = injector.get(HttpClient);
        i18nLoader = injector.get(I18nLoader);
    });

    it('should provide I18nLoader', () => {
        expect(i18nLoader).toBeDefined();
    });

    it('get translation and pattern', () => {
        const configWithI18n = Object.assign({ i18nScope: [PatternCategories.DATE] }, baseConfig);
        const requestParam = '?productName=' + configWithI18n.productID
                    .concat('&version=' + configWithI18n.version)
                    .concat('&components=' + configWithI18n.component)
                    .concat('&language=' + 'zh-Hans')
                    .concat('&scope=' + PatternCategories.DATE)
                    .concat('&pseudo=' + configWithI18n.isPseudo)
                    .concat('&combine=' + 2);
        // mock response
        spyOn(httpClient, 'get').and.returnValue(
            of({
                response: { code: 200 },
                data: mockTranslationAndPattern
            })
        );
        i18nLoader.getLocaleData(configWithI18n, 'zh-Hans').subscribe((res) => {
            expect(res).toEqual({
                categories: { dates: {} },
                messages: {
                    'vipngxsample-default-100': {
                        'application.title': '欢迎来到 VIP Angular 示例应用!'
                    }
                }
            });
        });
        const url = configWithI18n.host.concat(VIPServiceConstants.TRANSLATION_PATTERN);
        expect(httpClient.get).toHaveBeenCalledWith(url + requestParam);
    });

    it('get translation', () => {
        // mock response
        spyOn(httpClient, 'get').and.returnValue(
            of({
                response: { code: 200 },
                data: translation
            })
        );
        i18nLoader.getLocaleData(baseConfig, 'zh-Hans').subscribe((res) => {
            expect(res).toEqual(
                {
                    categories: undefined,
                    messages: {
                        'vipngxsample-default-100': {
                            'application.title': '欢迎来到 VIP Angular 示例应用!'
                        }
                    }
                }
            )
        });
        const url = 'http://test:8000/i18n/api/v2/translation/products/vipngxsample/versions/1.0.0/locales/zh-Hans/components/default?pseudo=false';
        expect(httpClient.get).toHaveBeenCalledWith(url);
    });

    it('get translation from assets folder', () => {
        spyOn(httpClient, 'get').and.returnValue(of({}));
        const conf = Object.assign({ i18nAssets: 'test/' }, baseConfig);
        i18nLoader.getLocaleData(conf, 'zh-Hans');
        const url = 'test/translation_zh-Hans.json';
        expect(httpClient.get).toHaveBeenCalledWith(url);
    });

    it('get translation and pattern from assets folder', () => {
        spyOn(httpClient, 'get').and.returnValue(of());
        const conf = Object.assign({ i18nAssets: 'test/', i18nScope: [PatternCategories.DATE] }, baseConfig);
        i18nLoader.getLocaleData(conf, 'zh-Hans');
        const url = 'test/zh-Hans.json';
        expect(httpClient.get).toHaveBeenCalledWith(url);
    });

    it('get supported languages', () => {
        spyOn(httpClient, 'get').and.returnValue(of({}));
        i18nLoader.getSupportedLanguages(baseConfig);
        const url = 'http://test:8000/i18n/api/v2/locale/supportedLanguageList?productName=vipngxsample&version=1.0.0';
        expect(httpClient.get).toHaveBeenCalledWith(url);
    });

    it('get supported regions', () => {
        spyOn(httpClient, 'get').and.returnValue(of({}));
        i18nLoader.getSupportedRegions('en-US', baseConfig);
        const url = 'http://test:8000/i18n/api/v2/locale/regionList?supportedLanguageList=en-US';
        expect(httpClient.get).toHaveBeenCalledWith(url);
    });

    it('get localized cities', () => {
        spyOn(httpClient, 'get').and.returnValue(of({}));
        i18nLoader.getLocalizedCities('en', 'US', baseConfig);
        const url = 'http://test:8000/i18n/api/v2/locale/regionList?supportedLanguageList=en&displayCity=true&regions=US';
        expect(httpClient.get).toHaveBeenCalledWith(url);
    });

    it('throw error if param missing', () => {
        expect(()=> {
            i18nLoader.getLocaleData({} as any, 'zh-Hans')
        }).toThrowError(`InvalidParamater: 'productID in VIPLoader'`);
        expect(()=> {
            i18nLoader.getLocaleData({productID: 'vipngxsample'} as any, 'zh-Hans')
        }).toThrowError(`InvalidParamater: 'version in VIPLoader'`);
        expect(()=> {
            i18nLoader.getLocaleData({productID: 'vipngxsample', version: '1.0.0'} as any, 'zh-Hans')
        }).toThrowError(`InvalidParamater: 'host in VIPLoader'`);
        expect(()=> {
            i18nLoader.getLocaleData(
                {productID: 'vipngxsample', version: '1.0.0', host: 'http://test:8000/'} as any, 'zh-Hans')
        }).toThrowError(`InvalidParamater: 'language in VIPLoader'`)
    });
});
