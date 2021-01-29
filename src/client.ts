/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Loader, defaultLoader } from './loader';
import { Store, CacheManager } from './cache';
import { CoreService } from './services/core.service';
import { I18nService } from './services/i18n.service';
import { L10nService } from './services/l10n.service';
import { DateFormatter, defaultDateFormatter } from './formatters/date.formatter';
import { MessageFormat } from './formatters/message.formatter';
import { ResponseParser, defaultResponseParser } from './parser';
import { Configuration } from './configuration';

class I18nClient {
    private loader: Loader;
    private resParser: ResponseParser;
    private cacheManager: Store;
    private dateFormatter: DateFormatter;
    public coreService: CoreService;
    public i18nService: I18nService;
    public l10nService: L10nService;

    constructor() {
        this.loader = defaultLoader;
        this.resParser = defaultResponseParser;
        this.cacheManager = CacheManager.createTranslationCacheManager();
        this.dateFormatter = defaultDateFormatter;
    }

    /**
     * initialize services and load ENGLISH source data.
     * @param Config
     */
    init(Config: Configuration) {
        this.coreService = new CoreService(this.loader, this.resParser);
        this.coreService.init(Config);
        this.i18nService = new I18nService(this.coreService, this.dateFormatter, this.cacheManager);
        const messageFormatter = new MessageFormat(this.i18nService);
        this.l10nService = new L10nService(this.coreService, this.cacheManager, messageFormatter);
        return this;
    }

    plug(module: any) {
        if (module instanceof Loader) {
            this.loader = module;
        }

        if (module instanceof ResponseParser) {
            this.resParser = module;
        }

        if (module instanceof Store) {
            this.cacheManager = module;
        }
        return this;
    }

    /**
     * For the project which needs mutiple instances.
     * @param Config
     */
    createInstance(Config: Configuration) {
        return new I18nClient().init(Config);
    }

}

export const i18nClient = new I18nClient();
