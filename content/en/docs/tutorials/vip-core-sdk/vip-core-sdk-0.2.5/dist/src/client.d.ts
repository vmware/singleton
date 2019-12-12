import { CoreService } from './services/core.service';
import { I18nService } from './services/i18n.service';
import { L10nService } from './services/l10n.service';
import { Configuration } from './configuration';
declare class I18nClient {
    private loader;
    private resParser;
    private cacheManager;
    private dateFormatter;
    coreService: CoreService;
    i18nService: I18nService;
    l10nService: L10nService;
    constructor();
    /**
     * initialize services and load ENGLISH source data.
     * @param Config
     */
    init(Config: Configuration): this;
    plug(module: any): this;
    /**
     * For the project which needs mutiple instances.
     * @param Config
     */
    createInstance(Config: Configuration): I18nClient;
}
export declare const i18nClient: I18nClient;
export {};
