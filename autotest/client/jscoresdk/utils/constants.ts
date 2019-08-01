import * as CoreSDK from '@vip/vip-core-sdk-dev';

export enum DateTimeFormat {
    SHORT = 'short',
    MEDIUM = 'medium',
    LONG = 'long',
    FULL = 'full',
    SHORTTIME = 'shortTime',
    MEDIUMTIME = 'mediumTime',
    LONGTIME = 'longTime',
    FULLTIME = 'fullTime',
    SHORTDATE = 'shortDate',
    MEDIUMDATE = 'mediumDate',
    LONGDATE = 'longDate',
    FULLDATE = 'fullDate',
    default = 'defaultDate'
}

export enum SupportedLanguage {
    en = 'en',
    zhcn = 'zh-Hans',
    de = 'de',
    es = 'es',
    // fr = 'fr',
    ja = 'ja',
    ko = 'ko',
    zhTW = 'zh-Hant',
    esMX = 'es-MX',
    frCA = 'fr-CA',
}



export enum UnsupportedLanguage {
    it = 'it-IT',
}



export enum Region {
    cn = 'CN',
    tw = 'TW',
    MX = 'MX',
}

export const config = {
    productID: 'JSCoreSDKTest',
    version: '1.0.0',
    component: 'sunglow',
    host: 'http://localhost:8091/',
    language: 'en-US',
    i18nScope: [CoreSDK.PatternCategories.DATE, CoreSDK.PatternCategories.CURRENCIES, CoreSDK.PatternCategories.NUMBER, CoreSDK.PatternCategories.PLURAL]
    // isPseudo?: boolean;
    // language?: string;
    // region?: string;
    // sourceBundle?: { [key: string]: any };
    // i18nAssets?: string;
    // timeout?: number;
};


export class TranslationResource {
    public static pseudo_fix = '@@';
    public static key_onlystring: string = 'com.vmware.um.journals.message';
    public static source_onlystring: string = 'Message';
    public static key_withargs: string = 'com.vmware.um.customer.rules.pagination';
    public static source_withargs: string = '{0}-{1} de {2} clientes';

    public static key_nonexistent: string = 'nonexistentKey';

    // public static key_bundle: string = "VIP.description.bundle";
    // public static trans_bundle_zhcn = "离线模式测试";

    public static key_bundle: string = "VIP.description";
    public static trans_bundle_zhcn = "{0} 是 VMware G11n 团队开发的通用 lib。";
    
    public static key_plural: string = "ngx.VIP.files";
    public static source_plural: string = "{files, plural, one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}";

    public static InvalidLanguage  = 'tttt';
}
