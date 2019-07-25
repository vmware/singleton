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
    host: string;
    isPseudo?: boolean;
    language?: string;
    region?: string;
    i18nScope?: PatternCategories[];
    sourceBundle?: { [key: string]: any };
    sourceBundles?: Array<{ [key: string]: any }>;
    i18nAssets?: string;
    timeout?: number;
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
        timeout: 3000
    };
}
