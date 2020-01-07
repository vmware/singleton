import { HttpRequestOptions } from './loader';

/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
export enum PatternCategories {
    DATE = 'dates',
    NUMBER = 'numbers',
    PLURAL = 'plurals',
    CURRENCIES = 'currencies'
}
export interface Configuration {
    productID: string;
    version: string;
    component?: string;
    components?: string[];
    combineRequest?: boolean;
    host: string;
    isPseudo?: boolean;
    language?: string;
    region?: string;
    i18nScope?: PatternCategories[];
    sourceBundle?: { [key: string]: any };
    sourceBundles?: { [key: string]: any };
    i18nAssets?: string;
    httpOptions?: HttpRequestOptions;
}

export function getDefaultConfig(): {} {
    return {
        component: 'default',
        isPseudo: false,
        language: 'en',
        region: '',
        i18nScope: [],
        sourceBundle: {},
        i18nAssets: '',
        combineRequest: true,
        httpOptions: { timeout: 3000 }
    };
}
