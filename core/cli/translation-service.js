/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const superagent = require('superagent');
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
                return res.body.data;
            }).catch((e) => {
                console.error('Got an error when send with token: ', e && e.message || e);
                return Promise.reject(e && e.message || e);
            });
    }

    send(token, key, locale, source) {
        var sendPromise = superagent
            .post(`${this.host}/i18n/api/v2/translation/products/${this.product}/versions/${this.version}/locales/${locale}/components/${this.component}/keys/${key}`)
            .query({
                collectSource: true,
                pseudo: false,
            })
            .send(source)
            .set('Content-Type', 'application/json');

        if (token) {
            sendPromise.set('csp-auth-token', token)
        }

        return sendPromise;
    }
}

module.exports = TranslationService;
