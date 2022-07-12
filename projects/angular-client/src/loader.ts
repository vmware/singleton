/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { timeoutWith, catchError, map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { VIPTimeOutError, invalidParamater } from './exceptions';
import { Observable, defer, throwError } from 'rxjs';
import { VIPConfig } from './config';
import { VIPServiceConstants } from './constants';
import { ResponseParser } from './response.parser';
import { LocaleData } from './services/vip.service';
import { isDefined } from './util';

export interface VIPRequestBody {
    language: string;
    region: string;
    productName: string;
    version: string;
    components: Array<string>;
    scope: string;
    pseudo?: boolean;
    machineTranslation?: boolean;
    checkTranslationStatus?: string;
    combine: number;
}
export abstract class I18nLoader {
    abstract getLocaleData(config: VIPConfig, language: string, region?: string): Observable<any>;
    abstract getSupportedLanguages(conf: VIPConfig): Observable<any>;
    abstract getSupportedRegions(language: string, conf: VIPConfig): Observable<any>;
    abstract getLocalizedCities(language: string, region: string, conf: VIPConfig): Observable<any>;
}

@Injectable()
export class VIPLoader implements I18nLoader {
    constructor(
        public responseParser: ResponseParser,
        private http: HttpClient) { }

    /**
     * Get the i18n resource through VIP service.
     * i18nAssets will be removed in future release.
     * And for i18nAsset only support loading translation
     * from web server, can't support loading patterns.
     * @param url
     * @returns Promise<any>
     * @memberof VIPRestLoader
     */
    public getLocaleData(config: VIPConfig, language: string, region?: string): Observable<LocaleData> {
        this.validateConfig(config);
        const scope = config.i18nScope ? config.i18nScope.join(',') : '';
        // For now, just only support downloading translation to local assets folder
        // but it is also supposed to support formatting pattern data.
        // Firstly, identify which mode is using.
        if (isDefined(config.i18nAssets)) {
            return scope === ''
                ? this.getTranslationByComponent(language, config)
                : this.getCombineDataFromAssets(language, config);
        }
        // Secondly, which API should be used to communicate with VIP service.
        return scope === ''
            ? this.getTranslationByComponent(language, config)
            : this.getCombineLocaleData(config, language, region);
    }

    /**
     * Get the combine locale data(translations and pattern) through assets folder.
     * @param language language code in use
     * @param config
     */
    private getCombineDataFromAssets(language: string, config: VIPConfig) {
        const url = this.getI18nResourceUrl(config, language);
        return this.getRequest(url, config.timeout, (res: any) => {
            return this.responseParser.ParseLocaleData(res, config);
        });
    }
    /**
     * Only get translations through VIP service.
     * @param language language tag for translation.
     * @param config without i18nScope
     */
    private getTranslationByComponent(language: string, config: VIPConfig) {
        const url = this.getComponentTranslationUrl(language, config);
        return this.getRequest(url, config.timeout, (res: any) => {
            return this.responseParser.ParseLocaleData(res, config);
        });
    }

    /**
     * Get combine locale data(translations and pattern) through VIP service.
     * @param conf with i18nScope
     * @param language language tag for translation.
     * @param region region code for formatting pattern.
     */
    private getCombineLocaleData(conf: VIPConfig, language: string, region?: string) {
        const scope = conf.i18nScope.join(',');
        const url = this.getI18nResourceUrl(conf);
        const combine = region ? 1 : 2;
        const params = '?productName=' + conf.productID
                    .concat('&version=' + conf.version)
                    .concat('&components=' + conf.component)
                    .concat('&language=' + language)
                    .concat('&scope=' + scope)
                    .concat('&pseudo=' + conf.isPseudo)
                    .concat('&combine=' + combine)
                    .concat(region ? '&region=' +region : '');
        return this.getRequest(url + params, conf.timeout, (res: any) => {
            return this.responseParser.ParseLocaleData(res, conf);
        });
    }

    public getSupportedLanguages(conf: VIPConfig): Observable<Object> {
        const url = this.getSupportedLanguagesUrl(conf);
        return this.getRequest(url, conf.timeout, (res: Object) => this.responseParser.ParseSupportedLanguagesData(res));
    }

    public getSupportedRegions(language: string, conf: VIPConfig): Observable<Object> {
        const url = this.getSupportedRegionsUrl(language, conf.host);
        return this.getRequest(url, conf.timeout, (res: Object) => this.responseParser.ParseSupportedRegionsData(res));
    }

    public getLocalizedCities(language: string, region: string, conf: VIPConfig) {
        const url = this.getLocalizedCitiesUrl(language, region, conf.host);
        return this.getRequest(url, conf.timeout, (res: Object) => this.responseParser.ParseSupportedRegionsData(res));
    }
    /**
     * Fetch I18N resource from backend service through get request.
     * @param url request url.
     * @param timeout default value is 3000ms, timeoutWith default value is 0ms.
     * @param fn a callback function that is executed after the request is completed.
     */
    private getRequest(url: string, timeout: number, fn?: Function): Observable<any> {
        return this.http.get(url)
            .pipe(
                timeoutWith(timeout, defer(() => {
                    return throwError(new VIPTimeOutError('Timeout error'));
                })),
                map((res: Object) => fn && typeof fn === 'function' ? fn(res) : res),
                catchError((err: any) => {
                    return throwError(err);
                })
            );
    }

    private getI18nResourceUrl(conf: VIPConfig, language?: string): string {
        if (conf.i18nAssets) {
            const path = `${conf.i18nAssets}${language}${VIPServiceConstants.ASSETS_SUFFIX}`;
            return path;
        }
        return conf.host
            .concat(VIPServiceConstants.TRANSLATION_PATTERN);
    }

    private getComponentTranslationUrl(language: string, config: VIPConfig) {
        if (isDefined(config.i18nAssets)) {
            const path = `${config.i18nAssets}${VIPServiceConstants.L10N_ASSETS_PREFIX}${language}${VIPServiceConstants.ASSETS_SUFFIX}`;
            return path;
        }
        return config.host
            .concat(VIPServiceConstants.L10N_COMPONENT_API_ENDPOINT)
            .concat('/products/' + config.productID)
            .concat('/versions/' + config.version)
            .concat('/locales/' + language)
            .concat('/components/' + config.component)
            .concat('?pseudo=' + config.isPseudo);
    }

    private getSupportedLanguagesUrl(config: VIPConfig) {
        return config.host
            .concat(VIPServiceConstants.L10N_LOCAL_API_ENDPOINT)
            .concat('/supportedLanguageList?')
            .concat('productName=' + config.productID)
            .concat('&version=' + config.version);
    }

    private getSupportedRegionsUrl(language: string, host: string) {
        return host
            .concat(VIPServiceConstants.L10N_LOCAL_API_ENDPOINT)
            .concat('/regionList?')
            .concat('supportedLanguageList=' + language);
    }

    private getLocalizedCitiesUrl(language: string, region: string, host: string) {
        return host
            .concat(VIPServiceConstants.L10N_LOCAL_API_ENDPOINT)
            .concat('/regionList?')
            .concat('supportedLanguageList=' + language)
            .concat('&displayCity=true&regions=' + region);
    }

    private validateConfig(initConfig: VIPConfig): boolean {
        const message = ' in VIPLoader';
        if (!isDefined(initConfig.productID) || !initConfig.productID.length) {
            throw invalidParamater('productID' + message);
        }
        if (!isDefined(initConfig.version) || !initConfig.version.length) {
            throw invalidParamater('version' + message);
        }
        // in i18nAssets mode, the host is not required
        if (!isDefined(initConfig.host) && !isDefined(initConfig.i18nAssets)) {
            throw invalidParamater('host' + message);
        }
        if (!isDefined(initConfig.language)) {
            throw invalidParamater('language' + message);
        }
        return true;
    }
}
