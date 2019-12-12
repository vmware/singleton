import { Store } from '../cache';
import { CoreService } from './core.service';
import { MessageFormat } from '../formatters/message.formatter';
export declare class L10nService {
    coreService: CoreService;
    private cacheManager;
    private messageFormat;
    private language;
    private isPseudo;
    private sourceData;
    private logger;
    constructor(coreService: CoreService, cacheManager: Store, messageFormat: MessageFormat);
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
    getTranslation(key: string, source: string, args?: string[] | {}): string;
    /**
     * Get formatted plural message
     */
    private formatMessage;
    /**
     * Get translation from cache
     */
    private getTranslationInCache;
    /**
     * Get source string in sourcebundle
     * @param key
     */
    getSourceString(key: string): string;
    /**
     * Get message only through the key.
     * @param key
     * @param args
     */
    getMessage(key: string, args?: any[] | {}): string;
}
