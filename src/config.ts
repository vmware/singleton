/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { InjectionToken } from '@angular/core';

export const VIP_CONFIG: InjectionToken<VIPConfigRef> = new InjectionToken<VIPConfigRef>('VIP_CONFIG');
export type VIPConfigRef = Required<VIPConfig>;
export enum PatternCategories {
    DATE = 'dates',
    NUMBER = 'numbers',
    PLURAL = 'plurals',
    CURRENCIES = 'currencies'
}

export interface VIPConfig {
    productID: string;
    component: string;
    version: string;
    host: string;
    isPseudo?: boolean;
    // deprecated
    language?: string;
    // deprecated
    region?: string;
    // deprecated
    locale?: string;
    i18nScope?: PatternCategories[];
    sourceBundle?: { [key: string]: any };
    translationBundles?: { [key: string]: any };
    // deprecated
    i18nAssets?: string;
    // deprecated
    collectSource?: boolean;
    timeout?: number;
}

export function getNameSpace(config: VIPConfig) {
    if (!config) { return undefined; }
    return [config.productID,
    config.component,
    config.version]
        .join('-').toString().toLowerCase()
        .replace(/\s+/g, '-')
        .replace(/[^\w\-]+/g, '')
        .replace(/\-\-+/g, '-')
        .replace(/^-+/, '')
        .replace(/-+$/, '');
}

/**
 * check and set default value.
 * @param vipConfig
 */
export function VIPConfigFactory(vipConfig: VIPConfig): VIPConfigRef {
    return {
        productID: vipConfig.productID || undefined,
        component: vipConfig.component || undefined,
        version: vipConfig.version || undefined,
        host: vipConfig.host || undefined,
        isPseudo: vipConfig.isPseudo || false,
        locale: vipConfig.locale || undefined,
        region: vipConfig.region || undefined,
        language: vipConfig.language || 'en',
        i18nScope: vipConfig.i18nScope || [],
        sourceBundle: vipConfig.sourceBundle || undefined,
        translationBundles: vipConfig.translationBundles || undefined,
        i18nAssets: vipConfig.i18nAssets || undefined,
        collectSource: vipConfig.collectSource || false,
        timeout: vipConfig.timeout || 3000
    };
}
