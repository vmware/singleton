import { HttpRequestOptions } from './loader';
export declare enum PatternCategories {
    DATE = "dates",
    NUMBER = "numbers",
    PLURAL = "plurals",
    CURRENCIES = "currencies"
}
export interface Configuration {
    productID: string;
    version: string;
    component?: string;
    host: string;
    isPseudo?: boolean;
    language?: string;
    region?: string;
    i18nScope?: PatternCategories[];
    sourceBundle?: {
        [key: string]: any;
    };
    sourceBundles?: Array<{
        [key: string]: any;
    }>;
    i18nAssets?: string;
    httpOptions?: HttpRequestOptions;
}
export declare function getDefaultConfig(): {};
