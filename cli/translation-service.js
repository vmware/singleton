/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const axios = require('axios');
const debug = require('debug')('translate-service');

class TranslationService {
    constructor(host, token, product, version, component) {
        this.product = product;
        this.version = version;
        this.component = component;
        this.host = host;
        this.token = token;
    }

    collectSource(key, source, pid = 'main thread') {
        var locale = 'en_US';

        debug(`[pid: ${pid}] calling translate service for the locale: ${locale} and the key ${key}`);

        return this.send(this.token, key, locale, source)
            .then((res) => {
                debug(`[pid: ${pid} locale: ${locale}, key: ${key}] Translate API successfully returned a translation.`);
                return res.data;
            }).catch((e) => {
                console.error('Got an error when send with token: ', e && e.message || e);
                return Promise.reject(e && e.message || e);
            });
    }

    send(token, key, locale, source) {
        let headers = {
            'Content-Type': 'application/json',
        }

        if (token) {
            headers['csp-auth-token'] = token;
        }

        return axios.post(`${this.host}/i18n/api/v2/translation/products/${this.product}/versions/${this.version}/locales/${locale}/components/${this.component}/keys/${key}`,
            source,
            {
                headers,
                params: {
                    collectSource: true,
                    pseudo: false,
                }
            }
        );
    }
}

module.exports = TranslationService;
