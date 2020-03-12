/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Loader, ResponseParser, HttpRequestOptions } from '../index';
export const mockResponse = {
    trans: {
        test: 'testing string'
    },
    pattern: {
        categories: {
            date: 'test'
        }
    },
    languages: [
        {
            languageCode: 'zh-CN',
            displayName: 'Simplified Chinese'
        }
    ],
    regions: {
        territories: [
            {
                AC: 'Ascension Island',
                DE: 'Germany'
            }
        ]
    }
};
export class CustomLoader extends Loader {
    getI18nResource(url: string, options: HttpRequestOptions): Promise<any> {
        if ( url.indexOf( '/components/' ) !== -1 ) {
            return new Promise((resolve ) => {
                resolve( mockResponse.trans);
            });
        } else if ( url.indexOf( '/patterns/locales' ) !== -1 ) {
            return new Promise((resolve ) => {
                resolve( mockResponse.pattern);
            });
        } else if (  url.indexOf( 'supportedLanguageList?' ) !== -1 ) {
            return new Promise((resolve ) => {
                resolve( mockResponse.languages);
            });
        } else if (  url.indexOf( '/regionList?' ) !== -1 ) {
            return new Promise((resolve ) => {
                resolve( mockResponse.regions);
            });
        }
    }
}

export class  CustomParser extends  ResponseParser {
    validateResponse( res: any ): any {
        return res;
    }
    getPatterns( res: any ): any {
        const data = this.validateResponse( res );
        const pattern = data && data.categories ? data.categories : null;
        return pattern;
    }
    getTranslations( res: any ): any {
        const data = this.validateResponse( res );
        const translations = data ? data : null;
        return translations;
    }
    getSupportedLanguages( res: any ): any {
        const data = this.validateResponse( res );
        const languages = data ? data : null;
        return languages;
    }
    getSupportedRegions( res: any ): any {
        const data = this.validateResponse( res );
        const regions = data ? data : null;
        return regions;
    }
    getTranslationBundles( res: any ): {}[]|null {
        const data = this.validateResponse( res );
        const bundles = data && data.bundles ? data.bundles : null;
        return bundles;
    }
}
