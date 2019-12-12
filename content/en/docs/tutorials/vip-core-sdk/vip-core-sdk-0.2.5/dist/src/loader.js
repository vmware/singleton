"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const axios_1 = require("axios");
const logger_1 = require("./logger");
class Loader {
}
exports.Loader = Loader;
class RestLoader {
    constructor() {
        this.logger = logger_1.basedLogger.create('RestLoader');
    }
    getI18nResource(url, options) {
        return axios_1.default.get(url, options || {}).then((response) => {
            const res = response.data;
            return res;
        }).catch((reason) => {
            this.logger.error(reason.message);
        });
    }
}
exports.defaultLoader = new RestLoader();
