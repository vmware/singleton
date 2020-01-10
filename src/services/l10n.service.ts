/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Store } from '../cache';
import { CoreService } from './core.service';
import { isDefined } from '../utils';
import { MessageFormat } from '../formatters/message.formatter';
import { Logger, basedLogger } from '../logger';
import { Constants } from '../constants';

const pseudoTag = '@@';

export class L10nService {
    private language: string;
    private isPseudo: boolean;
    private sourceData: any;
    private logger: Logger;

    constructor(
        public coreService: CoreService,
        private cacheManager: Store,
        private messageFormat: MessageFormat,
        private component?: string
    ) {
        this.isPseudo = coreService.getIsPseudo();
        this.sourceData = this.coreService.getSourceBundle();
        this.logger = basedLogger.create('L10nService');
        this.component = isDefined(component) ? component : this.coreService.getComponent();
    }

    /**
     * Get translation from cache if language isn't source.
     * And interpolate params to translation or source.
     * If translation not found return source string.
     * @param key
     * @param source
     * @param params[args]
     * @returns string
     * @memberof L10nService
     */
    public getTranslation(key: string, source: string, args?: string[] | {}): string {
        this.language = this.coreService.getLanguage();
        let translation: string;
        let isFallback = false;
        const isSourceLanguage: boolean = this.coreService.isSourceLanguage(this.language);
        // Return source string directly.
        if (isSourceLanguage) {
            translation = source;
        } else {
            const component = this.component || this.coreService.getComponent();
            translation = this.getTranslationInCache(component, key);
            if (!translation || translation === '') {
                translation = this.isPseudo ? `${pseudoTag}${source}${pseudoTag}` : source;
                // source fallback to sourceLocale, plural & number format should fallback to sourceLocale too.
                isFallback = true;
            }
        }
        if (translation && translation.trim() !== '') {
            translation = this.formatMessage(isFallback, translation, args);
        }
        return translation;
    }


    /**
     * Get formatted plural message
     */
    private formatMessage(isFallback: boolean, message: string, args?: string[] | {}) {
        const locale = isFallback ? Constants.SOURCE_LOCALE : this.coreService.getLanguage();
        return this.messageFormat.format(locale, message, args);
    }

    /**
     * Get translation from cache
     */
    private getTranslationInCache(component: string, key: string) {
        const translation = this.cacheManager.lookforTranslationByKey(key, component, this.coreService.getLanguage());
        return translation;
    }

    /**
     * Get source string in sourcebundle
     * @param key
     */
    public getSourceString(key: string): string {
        if (typeof key !== 'string') { return undefined; }
        if (this.sourceData && this.sourceData[key]) {
            return this.sourceData && this.sourceData[key];
        }
        // get source string from multiple component sourceBundle
        const sourceString = this.sourceData && this.sourceData[this.component] && this.sourceData[this.component][key];
        if (sourceString) { return sourceString; }
        this.logger.error('No English found for key: ' + key + ' in sourceBundle');
        return key;
    }
    /**
     * Get message only through the key.
     * @param key
     * @param args
     */
    public getMessage(key: string, args?: any[] | {}): string {
        if (typeof key !== 'string') { return undefined; }
        if (!isDefined(key)) {
            return null;
        }
        const source = this.getSourceString(key);
        const translation = this.getTranslation(key, source, args);
        return translation;
    }
}
