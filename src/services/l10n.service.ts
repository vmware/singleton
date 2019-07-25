/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';
import { getNameSpace, VIPConfig } from '../config';
import { LocaleService } from './locale.service';
import { isDefined } from '../util';
import { MessageFormat } from '../formatters/message.formatter';
import { BaseService } from './base.service';
import { VIPService, LocaleData } from './vip.service';
import { VIPServiceConstants } from '../constants';

const pseudoTag = '@@';

export interface L10NKey {
    rawKey: string;
    nameSpace: string;
}

@Injectable()
export class L10nService extends BaseService {
    constructor(
        protected vipService: VIPService,
        protected localeService: LocaleService,
        private messageFormat: MessageFormat,
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
     * Get source string via key with namespace
     * @param key raw key with namespace
     */
    public getSourceString(key: string): string {
        if (typeof key !== 'string') { return undefined; }
        const l10nKey = this.parseKey(key);
        const sourceBundle = this.resolveLocaleData(l10nKey.nameSpace, VIPServiceConstants.SOURCE_LANGUAGE);
        if (sourceBundle && sourceBundle[l10nKey.rawKey]) {
            return sourceBundle && sourceBundle[l10nKey.rawKey];
        }
        console.error('No English found for key: %s in sourceBundle', key);
        return key;
    }

    private formatMessage(isFallback: boolean, locale: string,  message: string, args?: string[] | {}) {
        if ( isFallback ) { locale = this.localeService.defaultLanguage.languageTag; }
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
                if (this.vipService.mainConfig && this.vipService.mainConfig.isPseudo) {
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
        const source = this.getSourceString(key);
        const translation = this.translate(key, source, args, locale);
        return translation;
    }
}
