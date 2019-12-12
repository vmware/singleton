import { Loader, ResponseParser, HttpRequestOptions } from '../index';
export declare const mockResponse: {
    trans: {
        test: string;
    };
    pattern: {
        categories: {
            date: string;
        };
    };
    languages: {
        languageCode: string;
        displayName: string;
    }[];
    regions: {
        territories: {
            AC: string;
            DE: string;
        }[];
    };
};
export declare class CustomLoader extends Loader {
    getI18nResource(url: string, options: HttpRequestOptions): Promise<any>;
}
export declare class CustomParser extends ResponseParser {
    validateResponse(res: any): any;
    getPatterns(res: any): any;
    getTranslations(res: any): any;
    getSupportedLanguages(res: any): any;
    getSupportedRegions(res: any): any;
}
