/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { HttpRequestOptions } from './loader';
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
    host: string;
    isPseudo?: boolean;
    language?: string;
    region?: string;
    i18nScope?: PatternCategories[];
    sourceBundle?: { [key: string]: any };
    sourceBundles?: Array<{ [key: string]: any }>;
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
        httpOptions: { timeout: 3000 }
    };
}
