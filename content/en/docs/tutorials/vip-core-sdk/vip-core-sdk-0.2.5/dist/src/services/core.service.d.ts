import { Loader } from '../loader';
import { ResponseParser } from '../parser';
import { Configuration } from '../configuration';
export declare class CoreService {
    private coreLoader;
    private resParser;
    private config;
    private httpOptions;
    private currentRegion;
    private currentLanguage;
    private cacheManager;
    private logger;
    constructor(coreLoader: Loader, resParser: ResponseParser);
    init(config: Configuration): void;
    /**
     * Load resource prior to perform callback.
     * @param callback
     */
    loadI18nData(callback?: () => void): Promise<any>;
    private validateConfig;
    getHost(): string;
    getProductID(): string;
    getVersion(): string;
    getComponent(): string;
    getLanguage(): string;
    getRegion(): string;
    getIsPseudo(): boolean;
    getI18nScope(): import("../configuration").PatternCategories[];
    getSourceBundle(): {
        [key: string]: any;
    };
    private resetSourceBundle;
    /**
     * Identify the locale from specified language and region is source locale or not.
     * @param language
     * @param region
     */
    isSourceLocale(language: string, region?: string): boolean;
    /**
     * Identify specified language is source language or not.
     * @param language
     */
    isSourceLanguage(language: string): boolean;
    getComponentTransUrl(language: string): string;
    /**
     * return promise with processed result.
     * @param language
     */
    loadTranslations(language: string): Promise<any>;
    getPatternUrl(region: string, language: string): string;
    loadPatterns(region: string, language: string): Promise<any>;
    private getSupportedLanguagesUrl;
    getSupportedLanguages(displayLang?: string): Promise<any>;
    private getSupportedRegionsUrl;
    getSupportedRegions(language: string): Promise<any>;
    setTranslations(language: string, translations: any): void;
    setPatterns(patterns: any, language: string, region?: string): void;
}
