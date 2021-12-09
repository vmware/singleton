/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { VIPModule, VIPService, I18nService } from '../index';
import { Injectable, Injector } from '@angular/core';
import { of } from 'rxjs';
import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { baseConfig } from './test.util';

describe('I18nService', () => {
    let injector: Injector;
    let i18nService: I18nService;
    let vipService: VIPService;
    let httpClient: HttpClient;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule,
                VIPModule.forRoot()
            ]
        });
        injector = getTestBed();
        httpClient = injector.get(HttpClient);
        i18nService = injector.get(I18nService);
        vipService = injector.get(VIPService);
    });

    it('should defined', () => {
        expect(i18nService).toBeDefined();
        expect(i18nService instanceof I18nService).toBeTruthy();
    });

    it('get languages list', async() => {
        vipService.initData(baseConfig);
        const languages =  [
            {
                languageTag: 'en',
                displayName: 'English',
                displayName_sentenceBeginning: 'English',
                displayName_standalone: 'English',
                displayName_uiListOrMenu: 'English'
            }
        ];
        spyOn(httpClient, 'get').and.returnValue(of({
            response: { code: 200 },
            data: {
                languages: languages
            }
        }));
        const res = await i18nService.getSupportedLanguages();
        expect(res).toEqual(languages);
    });

    it('get processed regions array', async () => {
        vipService.initData(baseConfig);
        const regionsData = {
            defaultRegionCode: 'US',
            language: 'en',
            territories: {
                AC: 'Ascension Island',
                AD: 'Andorra',
                AE: 'United Arab Emirates'
            }
        };
        spyOn(httpClient, 'get').and.returnValue(of({
            response: { code: 200 },
            data: [ regionsData ]
        }));
        const expectRes = [
            [ 'AC', 'Ascension Island'],
            [ 'AD', 'Andorra'],
            [ 'AE', 'United Arab Emirates']
        ];
        const regions  = await i18nService.getSupportedRegions('en-US');
        expect(regions).toEqual(expectRes);
    });

    it('get localized cities list', async() => {
        vipService.initData(baseConfig);
        const regionsData = {
            defaultRegionCode: 'US',
            language: 'en',
            territories: {AC: 'Ascension Island'},
            cities: {
                'US': [
                    {
                        geonameid: '4058553',
                        name: 'Decatur',
                        translatedname: 'Decatur - Alabama',
                        lat: '34.60593',
                        lng: '-86.98334'
                    }
                ]
            }
        };
        spyOn(httpClient, 'get').and.returnValue(of({
            response: { code: 200 },
            data: [ regionsData ]
        }));
        const cities = await i18nService.getCities('US', 'en');
        expect(cities).toEqual(regionsData.cities['US']);
    });

    it('get localized pattern', () => {
        const pattern = i18nService.getLocalizedPattern('long');
        expect(pattern).toEqual(`MMMM d, y 'at' h:mm:ss a z`)
    });

    it('get localized currency symbol', () => {
        const currencySymbol = i18nService.getLocalizedCurrencySymbol('CNY');
        expect(currencySymbol).toEqual('CN¥');
    });
});
