/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { isDefined } from './utils';


/**
 * Used to maintain the cache for translations and patterns.
 */

export abstract class Store {
    abstract lookforTranslationByComponent(component: string, language: string): any;
    abstract lookforTranslationByKey(key: string, component: string, language: string): string;
    abstract lookforPattern(language: string, region?: string): any;
    abstract addTranslationByComponent(component: string, language: string, msgObj: any): boolean;
    abstract addPatternByLocale(patternsObj: any, language: string, region?: string): boolean;
    abstract release(): boolean;
}

enum CacheNamespace {
    L10N,
    I18N
}

export class CacheManager implements Store {
    private static cacheManager: CacheManager = null;
    private cachedTranslationMap = new Map();
    private cachedPatternsMap = new Map();
    static createTranslationCacheManager() {
        if (!isDefined(this.cacheManager)) {
            this.cacheManager = new CacheManager();
        }
        return this.cacheManager;
    }

    /**
     * generate unique cache key for translation object per component.
     * @param component
     * @param language
     */
    private getTranslationCacheKey(component: string, language: string): string {
        return [
            component,
            language,
            CacheNamespace.L10N
        ].join('_');
    }

    /**
     * generate unique cache key for pattern object per locale.
     * @param language
     * @param region
     */
    private getPatternCacheKey(language: string, region?: string): string {
        return [
            isDefined(region) ? [language, region].join('_') : language,
            CacheNamespace.I18N
        ].join('_');
    }

    /**
     * Get Component translation from cache.
     * @param cacheKey The key of cache.
     * @returns The translation map.
     */
    lookforTranslationByComponent(component: string, language: string): string {
        const cacheKey = this.getTranslationCacheKey(component, language);
        if (!this.cachedTranslationMap.get(cacheKey)) {
            return undefined;
        } else {
            return this.cachedTranslationMap.get(cacheKey);
        }
    }

    /**
     * Get translation for specific key.
     * @param key
     * @param component
     * @param language
     */
    lookforTranslationByKey(key: string, component: string, language: string): string {
        const cacheKey = this.getTranslationCacheKey(component, language);
        if (this.cachedTranslationMap.get(cacheKey) && this.cachedTranslationMap.get(cacheKey)[key]) {
            return this.cachedTranslationMap.get(cacheKey)[key];
        }
        return undefined;
    }

    /**
     * Add translations for the component.
     * @param component
     * @param language
     * @param msgObj
     */
    addTranslationByComponent(component: string, language: string, msgObj: any): boolean {
        const cacheKey = this.getTranslationCacheKey(component, language);
        this.cachedTranslationMap.set(cacheKey, msgObj);
        return true;
    }

    /**
     * Add parttern to cache by cacheKey
     * @param cacheKey The key of cache.
     * @param patternsObj
     */
    addPatternByLocale(patternsObj: any, language: string, region?: string): boolean {
        const cacheKey = this.getPatternCacheKey(language, region);
        this.cachedPatternsMap.set(cacheKey, patternsObj);
        return true;
    }

    lookforPattern(language: string, region?: string): any {
        const cacheKey = this.getPatternCacheKey(language, region);
        if (this.cachedPatternsMap.get(cacheKey)) {
            return this.cachedPatternsMap.get(cacheKey);
        }
        return undefined;
    }

    /**
     * clean the cache.
     * @returns true or false
     */
    release(): boolean {
        this.cachedTranslationMap.clear();
        this.cachedPatternsMap.clear();
        return true;
    }
}
