/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';
import { getNameSpace, VIPConfig } from '../config';
import { LocaleService } from './locale.service';
import { isDefined } from '../util';
import { filterArgs } from '../extensions/stringable';
import { MessageFormat } from '../formatters/message.formatter';
import { BaseService } from './base.service';
import { VIPService, LocaleData } from './vip.service';
import { VIPServiceConstants } from '../constants';
import { I18nContext } from './i18n.context';

const pseudoTag = '@@';

export interface L10NKey {
    rawKey: string;
    nameSpace: string;
}

export type sourceBundleObject = { [key: string]: any };

@Injectable()
export class L10nService extends BaseService {
    constructor(
        protected vipService: VIPService,
        protected localeService: LocaleService,
        private messageFormat: MessageFormat,
        private i18nContext: I18nContext
    ) {
        super(vipService, localeService);
    }

    /**
     * Generate long key with namespace.
     * @param config
     * @param key raw key
     */
    public getLongKey(config: VIPConfig, key: string) {
        const nameSpace = config ? getNameSpace(config) : undefined;
        return nameSpace ? nameSpace
            .concat(VIPServiceConstants.NAME_SPACE_SEPARATOR)
            .concat(key) : key;
    }

    /**
     * In the mutiple component situation, the namespace is necessary.
     * Default workspace the main component in root scope and each lazy scope.
     * @param key raw key with namespace
     */
    private parseKey(key: string): L10NKey {
        let nameSpace: string;
        let rawKey = key;
        if (key && key.indexOf(VIPServiceConstants.NAME_SPACE_SEPARATOR) > -1) {
            const parts = key.split(VIPServiceConstants.NAME_SPACE_SEPARATOR);
            nameSpace = parts[0];
            rawKey = parts[1];
        }
        nameSpace = nameSpace ? nameSpace : getNameSpace(this.vipService.mainConfig);
        return { rawKey: rawKey, nameSpace: nameSpace };
    }

    /**
     * If translation is certainly be loaded before application bootstrap, the locale can be
     * skipped, otherwise make sure getting available locale from the return of stream API.
     * @param nameSpace
     * @param locale
     */
    public resolveLocaleData(nameSpace: string, locale?: string) {
        let localeData: LocaleData;
        let translations: any;
        const currentLocale = locale ? locale : this.currentLocale;
        localeData = this.vipService.localeData[currentLocale];
        translations = localeData && localeData.messages &&
            localeData.messages[nameSpace] ? localeData.messages[nameSpace] : undefined;
        return translations;
    }

    /**
     * Register source bundles to the main configuration on demand.
     * For the isolated module (separated lib), the namespace is required.
     * Considering using the last item of the array for the configuration or new API instead.
     * @param args Source bundle objects from Angular component.
     */
    public registerSourceBundles(...args: sourceBundleObject[]) {
        if (args) {
            this.vipService.registerSourceBundles(args, this.vipService.mainConfig);
        }
    }

    /**
     * Get source string via key with namespace
     * @param key raw key with namespace
     */
    public getSourceString(key: string): string {
        if (typeof key !== 'string') { return undefined; }
        const l10nKey = this.parseKey(key);
        const sourceBundle = this.resolveLocaleData(l10nKey.nameSpace,
            this.localeService.defaultLocale.languageCode);
        if (sourceBundle && sourceBundle[l10nKey.rawKey]) {
            // If the corresponding value of key is an array containing source and comment
            if (Array.isArray(sourceBundle[l10nKey.rawKey])) {
                // prevent empty array
                if (sourceBundle[l10nKey.rawKey][0]) {
                    return sourceBundle[l10nKey.rawKey][0];
                }
            } else {
                return sourceBundle[l10nKey.rawKey];
            }
            console.error('No English found for key: %s in sourceBundle', key);
        }
        return key;
    }

    /**
     * Determine whether the key already exists in the sourceBundles or translation.
     * if the locale is source locale, check whether the key exists in sourceBundles.
     * if the locale is not source locale, check whether the key exists in translation.
     * @param key raw key with namespace
     * @param locale
     */
    public isExistKey(key: string, locale?: string): boolean {
        const l10nKey = this.parseKey(key);
        if (this.localeService.isSourceLanguage) {
            locale = this.localeService.defaultLocale.languageCode;
        } else {
            locale = locale ? locale : this.currentLocale;
        }
        // sourceBundle or translations
        const resourceBundle = this.resolveLocaleData(l10nKey.nameSpace, locale);
        const isExist = resourceBundle && resourceBundle[l10nKey.rawKey] ? true : false;
        return isExist;
    }

    private formatMessage(isFallback: boolean, locale: string, message: string, args?: string[] | {}) {
        if (isFallback) { locale = this.localeService.defaultLocale.languageCode; }
        return this.messageFormat.format(locale, message, args);
    }

    /**
     * @param key raw key with namespace
     * @param source source string for translation
     * @param args variables for placeholders
     * @param locale work with steam API
     */
    public translate(key: string, source: string, args?: any[] | {}, locale?: string): string {
        let translation: string;
        let isFallback: boolean;
        const l10nKey = this.parseKey(key);
        if (this.localeService.isSourceLanguage) {
            translation = source;
        } else {
            locale = locale ? locale : this.currentLocale;
            const translations = this.resolveLocaleData(l10nKey.nameSpace, locale);
            translation = translations ? translations[l10nKey.rawKey] : undefined;
            if (!translation || translation === '') {
                translation = source;
                const pseudoInConfig = this.vipService.mainConfig && this.vipService.mainConfig.isPseudo;
                const i18nEnabled = this.i18nContext.i18nEnabled !== false;
                if (pseudoInConfig && i18nEnabled) {
                    translation = `${pseudoTag}${source}${pseudoTag}`;
                }
                isFallback = true;
            }
        }
        if (translation && translation.trim() !== '') {
            translation = this.formatMessage(isFallback, locale, translation, args);
        }
        return translation;
    }

    /**
     * @param key raw key with namespace
     * @param args variables and comment
     * @param locale optinal parameter for live update through 'stream' API
     */
    public getMessage(key: string, args?: any[] | {}, locale?: string) {
        if (typeof key !== 'string') { return undefined; }
        if (!isDefined(key)) {
            return null;
        }

        // Detect whether existing Object in args Array, if exist, convert them into strings
        args = filterArgs(args);

        const source = this.getSourceString(key);
        const translation = this.translate(key, source, args, locale);
        return translation;
    }

    /**
     * This API is designed to generate scoped translate method for the isolated module.
     * The key with namespace which is generated by the configuration.
     * @param config The VIP configuration with product name, component name and version.
     */
    public getScopedTranslate(config: VIPConfig): Function {
        return (key: string, args?: any[] | {}, locale?: string) => {
            const longKey = this.getLongKey(config, key);
            return this.getMessage(longKey, args, locale);
        };
    }

    /**
     * getSplitedMessage
     * get message array splited by the seperator
     * @param key raw key with namespace
     * @param args variables
     * @param locale optinal parameter for live update through 'stream' API
     * @param seperator is a regular expressions, default value is /##\d+/
     */
    public getSplitedMessage(key: string, args?: any[], locale?: string, seperator?: RegExp) {
        const message = this.getMessage(key, args, locale);
        const defaultSeperator = /##\d+/;
        seperator = isDefined(seperator) ? seperator : defaultSeperator;
        return message.split(seperator);
    }
}
