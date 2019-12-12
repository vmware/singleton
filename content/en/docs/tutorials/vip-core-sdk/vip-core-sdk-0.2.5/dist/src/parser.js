"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const logger_1 = require("./logger");
class ResponseParser {
}
exports.ResponseParser = ResponseParser;
class VIPResponseParser {
    constructor() {
        this.logger = logger_1.basedLogger.create('VIPResponseParser');
    }
    validateResponse(res) {
        if (!res || !res.response) {
            return null;
        }
        const response = res.response;
        // response code is bussiness code from VIP backend.
        if (response.code !== 200) {
            this.logger.error(response.message);
        }
        return res.data;
    }
    getPatterns(res) {
        const data = this.validateResponse(res);
        const pattern = data && data.categories ? data.categories : null;
        return pattern;
    }
    getTranslations(res) {
        const data = this.validateResponse(res);
        const translations = data && data.messages ? data.messages : null;
        return translations;
    }
    getSupportedLanguages(res) {
        const data = this.validateResponse(res);
        const languages = data && data.languages ? data.languages : null;
        return languages;
    }
    getSupportedRegions(res) {
        const data = this.validateResponse(res);
        const regions = data && data[0] && data[0].territories ? data[0].territories : null;
        return regions;
    }
}
exports.defaultResponseParser = new VIPResponseParser();
