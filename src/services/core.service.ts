/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Loader, HttpRequestOptions } from '../loader';
import { Constants } from '../constants';
import { CacheManager } from '../cache';
import { isDefined, resolveLanguageTag, assign } from '../utils';
import { Logger, basedLogger } from '../logger';
import { ParamaterError } from '../exceptions';
import { ResponseParser } from '../parser';
import { Configuration, getDefaultConfig } from '../configuration';


export class CoreService {

    private config: Configuration;
    private httpOptions: HttpRequestOptions;
    private currentRegion: string;
    private currentLanguage: string;
    private cacheManager: CacheManager;
    private logger: Logger;

    constructor(private coreLoader: Loader, private resParser: ResponseParser) {
        this.cacheManager = CacheManager.createTranslationCacheManager();
        this.logger = basedLogger.create('CoreService');
    }

    init(config: Configuration) {
        const vipConfig = { ...getDefaultConfig(), ...config };
        this.validateConfig(vipConfig);
        this.config = vipConfig;
        this.config.sourceBundle = this.resetSourceBundle();
        this.currentLanguage = vipConfig.language;
        this.currentRegion = vipConfig.region;
        this.httpOptions = vipConfig.httpOptions;
    }

    /**
     * Load resource prior to perform callback.
     * @param callback
     */
    async loadI18nData(callback?: () => void): Promise<any> {
        let pendingList: Promise<any>[] = [];
        let i18nResources;

        // callback should be performed always
        try {
            // for translations
            if (!this.isSourceLanguage(this.currentLanguage)) {
                const request = this.loadTranslations(this.currentLanguage);
                if (Array.isArray(request)) {
                    pendingList = request;
                } else {
                    pendingList.push(request);
                }
            }
            // for formatting patterns
            if (!this.isSourceLocale(this.currentLanguage, this.currentRegion)) {
                pendingList.push(this.loadPatterns(this.currentRegion, this.currentLanguage));
            }
            i18nResources = await (pendingList.length > 0 ? Promise.all(pendingList) : Promise.resolve([]));
        } catch (error) {
            this.logger.error('Load i18n resources failed.', error.message);
        } finally {
            if (callback && typeof callback === 'function') { callback(); }
        }
        return i18nResources;
    }

    private validateConfig(config: Configuration): any {
        if (!isDefined(config.productID) || !config.productID.length) {
            throw ParamaterError('CoreService', 'ProductID');
        }
        if (!isDefined(config.version) || !config.version.length) {
            throw ParamaterError('CoreService', 'Version');
        }
        if (!isDefined(config.host) || !config.host.length) {
            throw ParamaterError('CoreService', 'Host');
        }
        if (!isDefined(config.language) || !config.language.length) {
            config.language = Constants.SOURCE_LANGUAGE;
        }
    }

    public getHost(): string {
        return this.config.host;
    }

    public getProductID(): string {
        return this.config.productID;
    }

    public getVersion(): string {
        return this.config.version;
    }

    public getComponent(): string {
        return this.config.component;
    }

    public getLanguage(): string {
        return this.currentLanguage;
    }

    public getRegion(): string {
        return this.currentRegion;
    }

    public getIsPseudo(): boolean {
        return this.config.isPseudo;
    }

    public getI18nScope() {
        return this.config.i18nScope;
    }

    public getSourceBundle() {
        return this.config.sourceBundle || undefined;
    }

    private resetSourceBundle() {
        if (this.config.sourceBundle && Object.keys(this.config.sourceBundle).length > 0) {
            return this.config.sourceBundle;
        }
        if (this.config.sourceBundles) {
            Object.keys(this.config.sourceBundles).forEach((key: string) => {
                const translations = this.config.sourceBundles[key];
                if (translations && Array.isArray(translations)) {
                    this.config.sourceBundles[key] = assign({}, translations);
                }
            });
            return this.config.sourceBundles;
        }
        return undefined;
    }

    /**
     * Identify the locale from specified language and region is source locale or not.
     * @param language
     * @param region
     */
    public isSourceLocale(language: string, region?: string): boolean {
        return this.isSourceLanguage(language)
            && isDefined(region)
            && region.toUpperCase() === Constants.SOURCE_REGION ? true
            : this.isSourceLanguage(language) && !isDefined(region) ? true
                : false;
    }

    /**
     * Identify specified language is source language or not.
     * @param language
     */
    public isSourceLanguage(language: string): boolean {
        return language.toLowerCase() === Constants.SOURCE_LANGUAGE ? true
            : Constants.SOURCE_LOCALE.toLowerCase() === resolveLanguageTag(language) ? true
                : false;
    }

    public getComponentTransUrl(language: string, component: string, multiple = false): string {
        if (isDefined(this.config.i18nAssets)) {
            const path = `${this.config.i18nAssets}${Constants.L10N_ASSETS_PREFIX}${language}${Constants.ASSETS_SUFFIX}`;
            return path;
        }
        if (multiple) {
            // url for multiple components
            return this.config.host
                .concat(Constants.L10N_COMPONENT_API_ENDPOINT)
                .concat('/products/' + this.config.productID)
                .concat('/versions/' + this.config.version)
                .concat('?components=' + component)
                .concat('&locales=' + language)
                .concat('&pseudo=' + this.config.isPseudo);
        }
        // url for component
        return this.config.host
            .concat(Constants.L10N_COMPONENT_API_ENDPOINT)
            .concat('/products/' + this.config.productID)
            .concat('/versions/' + this.config.version)
            .concat('/locales/' + language)
            .concat('/components/' + component)
            .concat('?pseudo=' + this.config.isPseudo);
    }

    /**
     * return promise with processed result.
     * @param language
     */
    public loadTranslations(language: string): Promise<any> | Promise<any>[] {
        const isMultipleComponents = this.config.components && this.config.components.length > 0;
        const isCombine = isMultipleComponents && this.config.combineRequest;

        // single component
        if (!isMultipleComponents) {
            return this.getTranslationByComponent(language, this.config.component);
        }
        // multiple components & not combine request

        if (isMultipleComponents && !isCombine) {
            const requestArray: Promise<any>[] = [];
            this.config.components.forEach((component: string) => {
                const request = this.getTranslationByComponent(language, component);
                requestArray.push(request);
            });
            return requestArray;
        }
        // multiple components & combine request
        const components = this.config.components.join(',');
        const componentTransUrl = this.getComponentTransUrl(language, components, true);
        const promise = this.coreLoader.getI18nResource(componentTransUrl, this.httpOptions);
        return promise.then(
            (result: any) => {
                // bundle is translations array
                const bundles = this.resParser.getTranslationBundles(result);
                bundles.forEach((item: any) => {
                    if (item.component && item.messages) {
                        this.setTranslations(language, item.component, item.messages);
                    }
                });
                return bundles;
            }).catch((err: any) => { this.logger.error('Load translations failed.', err.message); });

    }

    private async getTranslationByComponent(language: string, component: string) {
        const cache = this.cacheManager.lookforTranslationByComponent(component, language);
        if (typeof cache !== 'undefined') {
            return Promise.resolve(cache);
        }
        const url = this.getComponentTransUrl(language, component);
        const promise = this.coreLoader.getI18nResource(url, this.httpOptions);
        return promise.then(
            (result: any) => {
                const translations = this.resParser.getTranslations(result);
                if (translations) {
                    this.setTranslations(language, component, translations);
                    return translations;
                }
            }).catch((err: any) => { this.logger.error('Load translations failed.', err.message); });
    }

    public getPatternUrl(region: string, language: string): string {
        if (isDefined(this.config.i18nAssets)) {
            const path = `${this.config.i18nAssets}${Constants.I18N_ASSETS_PREFIX}${region}${Constants.ASSETS_SUFFIX}`;
            return path;
        }
        const categories = this.config.i18nScope.join(',');
        // When only set language, the language will be treated as a locale with default region.
        const endpoint = isDefined(region)
            ? `?language=${language}&region=${region}`
            : `/locales/${language}`;
        let url = this.config.host
            .concat(Constants.I18N_API_ENDPOINT)
            .concat(endpoint);
        if (categories) {
            const character = isDefined(region) ? '&' : '?';
            url = url.concat(character + 'scope=' + categories);
        }
        return url;
    }

    public loadPatterns(region: string, language: string) {
        const cache = this.cacheManager.lookforPattern(language, region);
        if (typeof cache !== 'undefined') {
            return Promise.resolve(cache);
        }
        if (!this.config.i18nScope || this.config.i18nScope.length < 1) { return; }
        const url = this.getPatternUrl(region, language);
        const getPatternPromise = this.coreLoader.getI18nResource(url, this.httpOptions);
        return getPatternPromise.then(
            (result: any) => {
                const patterns = this.resParser.getPatterns(result);
                if (patterns) {
                    this.setPatterns(patterns, language, region);
                    return patterns;
                }
            }).catch((err: any) => { this.logger.error('Load formatting patterns failed.', err.message); });
    }

    private getSupportedLanguagesUrl(displayLang?: string) {
        let url = this.config.host
            .concat(Constants.L10N_LOCAL_API_ENDPOINT)
            .concat('/supportedLanguageList?')
            .concat('productName=' + this.config.productID)
            .concat('&version=' + this.config.version);
        if (displayLang) { url = `${url}&displayLanguage=${displayLang}`; }
        return url;
    }

    public getSupportedLanguages(displayLang?: string): Promise<any> {
        const requestUrl = this.getSupportedLanguagesUrl(displayLang);
        const promise = this.coreLoader.getI18nResource(requestUrl, this.httpOptions).then((result: any) => {
            const languages = this.resParser.getSupportedLanguages(result);
            return languages;
        }).catch((err: any) => {
            this.logger.error(err.message);
        });
        return promise;
    }

    private getSupportedRegionsUrl(language: string) {
        const url = this.config.host
            .concat(Constants.L10N_LOCAL_API_ENDPOINT)
            .concat('/regionList?')
            .concat('supportedLanguageList=' + language);
        return url;
    }

    public getSupportedRegions(language: string): Promise<any> {
        if (!isDefined(language)) {
            throw ParamaterError('CoreService', 'language');
        }
        const supportedRegionsUrl = this.getSupportedRegionsUrl(language);
        const promise = this.coreLoader.getI18nResource(supportedRegionsUrl, this.httpOptions).then((result: any) => {
            const regions = this.resParser.getSupportedRegions(result);
            return regions;
        }).catch((err: any) => {
            this.logger.error(err.message);
        });
        return promise;
    }

    public setTranslations(language: string, component: string, translations: any) {
        if (!isDefined(language)) {
            throw ParamaterError('setTranslation', 'language');
        }
        if (!isDefined(translations)) {
            return;
        }
        this.cacheManager.addTranslationByComponent(component, language, translations);
    }

    public setPatterns(patterns: any, language: string, region?: string, ) {
        if (!isDefined(language)) {
            throw ParamaterError('setPattern', 'language');
        }
        if (!isDefined(patterns)) {
            return;
        }
        this.cacheManager.addPatternByLocale(patterns, language, region);
    }
}
