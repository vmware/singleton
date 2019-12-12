"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const loader_1 = require("./loader");
const cache_1 = require("./cache");
const core_service_1 = require("./services/core.service");
const i18n_service_1 = require("./services/i18n.service");
const l10n_service_1 = require("./services/l10n.service");
const date_formatter_1 = require("./formatters/date.formatter");
const message_formatter_1 = require("./formatters/message.formatter");
const parser_1 = require("./parser");
class I18nClient {
    constructor() {
        this.loader = loader_1.defaultLoader;
        this.resParser = parser_1.defaultResponseParser;
        this.cacheManager = cache_1.CacheManager.createTranslationCacheManager();
        this.dateFormatter = date_formatter_1.defaultDateFormatter;
    }
    /**
     * initialize services and load ENGLISH source data.
     * @param Config
     */
    init(Config) {
        this.coreService = new core_service_1.CoreService(this.loader, this.resParser);
        this.coreService.init(Config);
        this.i18nService = new i18n_service_1.I18nService(this.coreService, this.dateFormatter, this.cacheManager);
        const messageFormatter = new message_formatter_1.MessageFormat(this.i18nService);
        this.l10nService = new l10n_service_1.L10nService(this.coreService, this.cacheManager, messageFormatter);
        return this;
    }
    plug(module) {
        if (module instanceof loader_1.Loader) {
            this.loader = module;
        }
        if (module instanceof parser_1.ResponseParser) {
            this.resParser = module;
        }
        if (module instanceof cache_1.Store) {
            this.cacheManager = module;
        }
        return this;
    }
    /**
     * For the project which needs mutiple instances.
     * @param Config
     */
    createInstance(Config) {
        return new I18nClient().init(Config);
    }
}
exports.i18nClient = new I18nClient();
