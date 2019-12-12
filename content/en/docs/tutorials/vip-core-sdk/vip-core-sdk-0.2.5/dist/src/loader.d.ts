export interface HttpRequestOptions {
    timeout?: number;
    withCredentials?: boolean;
}
export declare abstract class Loader {
    abstract getI18nResource(url: string, options: HttpRequestOptions): Promise<{
        [key: string]: any;
    }>;
}
declare class RestLoader implements Loader {
    /**
    * Get the i18n resource from VIP server in async mode.
    * By default, timeout time 3 sec.
    *
    * @param {string} url
    * @returns {*}
    * @memberof VIPRestLoader
    */
    private logger;
    constructor();
    getI18nResource(url: string, options: HttpRequestOptions): Promise<{
        [key: string]: any;
    }>;
}
export declare const defaultLoader: RestLoader;
export {};
