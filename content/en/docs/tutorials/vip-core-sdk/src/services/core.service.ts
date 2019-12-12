/**
 * @copyright Copyright VMware, Inc. All rights reserved. VMware Confidential.
 * @license For licensing, see LICENSE.md.
 */

import { Loader } from '../loader';
import { Constants } from '../constants';
import { CacheManager } from '../cache';
import { isDefined, resolveLanguageTag } from '../utils';
import { Logger, basedLogger } from '../logger';
import { ParamaterError } from '../exceptions';
import { ResponseParser } from '../parser';
import sourcePatternData from '../data/locale_en';
import { Configuration, getDefaultConfig } from '../configuration';
import { GetPluralFn } from '../formatters/plural/plural.rules.parser';


export class CoreService {

    private config: Configuration;
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
        this.currentLanguage = vipConfig.language;
        this.currentRegion = vipConfig.region;
    }

    /**
     * Load resource prior to perform callback.
     * @param callback
     */
    async loadI18nData(callback?: () => void): Promise<any> {
        const pendingList = [];
        let i18nResources = [];

        // callback should be performed always
        try {
            // for translations
            if (!this.isSourceLanguage(this.currentLanguage)) {
                pendingList.push(this.loadTranslations(this.currentLanguage));
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
        return this.config.sourceBundle;
    }

    public getSourcePattern() {
        const patternData: any = sourcePatternData.categories;
        patternData.plurals.pluralFn = new GetPluralFn(patternData.plurals.pluralRules);
        return patternData;
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

    public getComponentTransUrl(language: string): string {
        if (isDefined(this.config.i18nAssets)) {
            const path = `${this.config.i18nAssets}${Constants.L10N_ASSETS_PREFIX}${language}${Constants.ASSETS_SUFFIX}`;
            return path;
        }
        return this.config.host
            .concat(Constants.L10N_COMPONENT_API_ENDPOINT)
            .concat('/products/' + this.config.productID)
            .concat('/versions/' + this.config.version)
            .concat('/locales/' + language)
            .concat('/components/' + this.config.component)
            .concat('?pseudo=' + this.config.isPseudo);
    }
    /**
     * return promise with processed result.
     * @param language
     */
    public loadTranslations(language: string): Promise<any> {
        const cache = this.cacheManager.lookforTranslationByComponent(this.getComponent(), this.currentLanguage);
        if (typeof cache !== "undefined") {
            return Promise.resolve(cache);
        }
        const componentTransUrl = this.getComponentTransUrl(language);
        const promise = this.coreLoader.getI18nResource(componentTransUrl, this.config.timeout);
        return promise.then(
            (result: any) => {
                const translations = this.resParser.getTranslations(result);
                if (translations) {
                    this.setTranslations(language, translations);
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
        const cache = this.cacheManager.lookforPattern(this.currentLanguage, this.currentRegion);
        if (typeof cache !== "undefined") {
            return Promise.resolve(cache);
        }
        if (!this.config.i18nScope || this.config.i18nScope.length < 1) { return; }
        const url = this.getPatternUrl(region, language);
        const getPatternPromise = this.coreLoader.getI18nResource(url, this.config.timeout);
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
        const promise = this.coreLoader.getI18nResource(requestUrl, this.config.timeout).then((result: any) => {
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
        const promise = this.coreLoader.getI18nResource(supportedRegionsUrl, this.config.timeout).then((result: any) => {
            const regions = this.resParser.getSupportedRegions(result);
            return regions;
        }).catch((err: any) => {
            this.logger.error(err.message);
        });
        return promise;
    }

    public setTranslations(language: string, translations: any) {
        if (!isDefined(language)) {
            throw ParamaterError('setTranslation', 'language');
        }
        if (!isDefined(translations)) {
            return;
        }
        this.cacheManager.addTranslationByComponent(this.getComponent(), this.currentLanguage, translations);
    }

    public setPatterns(patterns: any, language: string, region?: string, ) {
        if (!isDefined(language)) {
            throw ParamaterError('setPattern', 'language');
        }
        if (!isDefined(patterns)) {
            return;
        }
        if (patterns.plurals && isDefined(patterns.plurals.pluralRules)) {
            patterns.plurals.pluralFn = new GetPluralFn(patterns.plurals.pluralRules);
        }
        this.cacheManager.addPatternByLocale(patterns, language, region);
    }
}
