"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const utils_1 = require("../utils");
const logger_1 = require("../logger");
const constants_1 = require("../constants");
const pseudoTag = '@@';
class L10nService {
    constructor(coreService, cacheManager, messageFormat) {
        this.coreService = coreService;
        this.cacheManager = cacheManager;
        this.messageFormat = messageFormat;
        this.isPseudo = coreService.getIsPseudo();
        this.sourceData = this.coreService.getSourceBundle();
        this.logger = logger_1.basedLogger.create('L10nService');
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
    getTranslation(key, source, args) {
        this.language = this.coreService.getLanguage();
        let translation;
        let isFallback = false;
        const isSourceLanguage = this.coreService.isSourceLanguage(this.language);
        // Return source string directly.
        if (isSourceLanguage) {
            translation = source;
        }
        else {
            translation = this.getTranslationInCache(key);
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
    formatMessage(isFallback, message, args) {
        const locale = isFallback ? constants_1.Constants.SOURCE_LOCALE : this.coreService.getLanguage();
        return this.messageFormat.format(locale, message, args);
    }
    /**
     * Get translation from cache
     */
    getTranslationInCache(key) {
        const translation = this.cacheManager.lookforTranslationByKey(key, this.coreService.getComponent(), this.coreService.getLanguage());
        return translation;
    }
    /**
     * Get source string in sourcebundle
     * @param key
     */
    getSourceString(key) {
        if (typeof key !== 'string') {
            return undefined;
        }
        if (this.sourceData && this.sourceData[key]) {
            return this.sourceData && this.sourceData[key];
        }
        this.logger.error('No English found for key: ' + key + ' in sourceBundle');
        return key;
    }
    /**
     * Get message only through the key.
     * @param key
     * @param args
     */
    getMessage(key, args) {
        if (typeof key !== 'string') {
            return undefined;
        }
        if (!utils_1.isDefined(key)) {
            return null;
        }
        const source = this.getSourceString(key);
        const translation = this.getTranslation(key, source, args);
        return translation;
    }
}
exports.L10nService = L10nService;
