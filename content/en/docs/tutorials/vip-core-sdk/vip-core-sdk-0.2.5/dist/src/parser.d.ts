interface ResType {
    [key: string]: any;
}
export declare abstract class ResponseParser {
    abstract validateResponse(res: ResType): Object | null;
    abstract getPatterns(res: ResType): Object | null;
    abstract getTranslations(res: ResType): Object | null;
    abstract getSupportedRegions?(res: ResType): Object | null;
    abstract getSupportedLanguages?(res: ResType): {}[] | null;
}
declare class VIPResponseParser implements ResponseParser {
    private logger;
    constructor();
    validateResponse(res: ResType): any;
    getPatterns(res: ResType): Object | null;
    getTranslations(res: ResType): Object | null;
    getSupportedLanguages(res: ResType): {}[] | null;
    getSupportedRegions(res: ResType): Object | null;
}
export declare const defaultResponseParser: VIPResponseParser;
export {};
