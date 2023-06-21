/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import * as singletonCore from '@singleton-i18n/js-core-sdk';
import { ENGLISH } from './source.l10n';
import { config as defaultConfig } from './config';

/**
 * Should be executed in the html file that needs to be localized. 
 * 
 * @param {*} config object that provides the configuration with the following format
 *      {
 *          productID, version, component, host, localizeAttribute, langCookieName, localStoragePseudoKey 
 *      } 
 */
export function localize(config) {

    config = config ? config : {};
    // If some properties aren't provided by the given config 
    // we are getting them from the defaultConfig
    config = Object.assign(defaultConfig, config);
    const lang = detectLanguage(config);

    // Provide a conventient way to turn on pseudo translation.
    const shouldUsePseudoTranslations = () => {
        if (typeof window === 'undefined' || typeof window.localStorage === 'undefined') {
            return false;
        }
        return window.localStorage.getItem(config.localStoragePseudoKey) === 'true';
    };

    let i18nClient = singletonCore.i18nClient.init(
        {
            productID: config.productID,
            version: config.version,
            component: config.component,
            host: config.host,
            language: lang,
            sourceBundle: ENGLISH,
            // Uncommend if you don't want to load your translation from singleton but from your sources
            //i18nAssets: 'src/translations/', 
            isPseudo: shouldUsePseudoTranslations(),
        }
    );
    loadAndShowTranslations(i18nClient, config);
}

/**
 *  Collects all elements from the page that have the l10n attribute. 
 *  Gets the attribute's value which is the key for the message, localizes the message 
 *  and inserts it in the element.
 */
function loadAndShowTranslations(i18nClient, config) {
    if (i18nClient) {
        i18nClient.coreService.loadI18nData(() => {
            const localizableComponents = document.querySelectorAll(`[${config.localizeAttribute}]`);
            localizableComponents.forEach(comp => {
                const key = comp.getAttribute(config.localizeAttribute);
                const message = i18nClient.l10nService.getMessage(key);
                comp.innerHTML = message;
            });
        })
    }
}

/**
 *  Function that gets the current language. 
 *  If not set in the cookies it tries to detect the browser's. 
 *  If the browser's language isn't supported by singleton, falls back to english. 
 */
function detectLanguage(config) {
    // First checks for lang in the cookie 
    let language = getCookie(config.langCookieName);
    if (language) {
        return language;
    }

    // If there isn't cookie detects the browser lang
    language = singletonCore.getBrowserCultureLang();
    return language || undefined;
}

/** 
 * Gets a cookie by its name.  
 */
function getCookie(name) {
    function escape(s) { return s.replace(/([.*+?\^${}()|\[\]\/\\])/g, '\\$1'); };
    var match = document.cookie.match(RegExp('(?:^|;\\s*)' + escape(name) + '=([^;]*)'));
    return match ? match[1] : null;
}