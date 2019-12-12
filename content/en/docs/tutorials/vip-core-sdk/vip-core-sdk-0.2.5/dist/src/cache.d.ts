/**
 * Used to maintain the cache for translations and patterns.
 */
export declare abstract class Store {
    abstract lookforTranslationByComponent(component: string, language: string): any;
    abstract lookforTranslationByKey(key: string, component: string, language: string): string;
    abstract lookforPattern(language: string, region?: string): any;
    abstract addTranslationByComponent(component: string, language: string, msgObj: any): boolean;
    abstract addPatternByLocale(patternsObj: any, language: string, region?: string): boolean;
    abstract release(): boolean;
}
export declare class CacheManager implements Store {
    private static cacheManager;
    private cachedTranslationMap;
    private cachedPatternsMap;
    static createTranslationCacheManager(): CacheManager;
    /**
     * generate unique cache key for translation object per component.
     * @param component
     * @param language
     */
    private getTranslationCacheKey;
    /**
     * generate unique cache key for pattern object per locale.
     * @param language
     * @param region
     */
    private getPatternCacheKey;
    /**
     * Get Component translation from cache.
     * @param cacheKey The key of cache.
     * @returns The translation map.
     */
    lookforTranslationByComponent(component: string, language: string): string;
    /**
     * Get translation for specific key.
     * @param key
     * @param component
     * @param language
     */
    lookforTranslationByKey(key: string, component: string, language: string): string;
    /**
     * Add translations for the component.
     * @param component
     * @param language
     * @param msgObj
     */
    addTranslationByComponent(component: string, language: string, msgObj: any): boolean;
    /**
     * Add parttern to cache by cacheKey
     * @param cacheKey The key of cache.
     * @param patternsObj
     */
    addPatternByLocale(patternsObj: any, language: string, region?: string): boolean;
    lookforPattern(language: string, region?: string): any;
    /**
     * clean the cache.
     * @returns true or false
     */
    release(): boolean;
}
