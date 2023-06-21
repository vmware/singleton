/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { localize } from "../src/i18n.utils";
import * as singletonCore from '@singleton-i18n/js-core-sdk';
import { config as defaultConfig } from "../src/config";
import { ENGLISH } from "../src/source.l10n";

describe("Localize static files solution tests", () => {

    describe("Pseudo extraction tests", () => {

        it("should call the localStorage with the default config key", () => {
            const spy = jest.spyOn(Storage.prototype, 'getItem');

            localize();

            expect(spy).toHaveBeenCalledWith(defaultConfig.localStoragePseudoKey);
        });

        it("should call the localStorage with the provided key", () => {
            const spy = jest.spyOn(Storage.prototype, 'getItem');
            const localStoragePseudoKey = 'local-storage-key';

            localize({ localStoragePseudoKey });

            expect(spy).toHaveBeenCalledWith(localStoragePseudoKey);
        });
    });


    describe("Language detection tests", () => {
        it('should get the language from the cookie when it is present', () => {
            jest.spyOn(document, 'cookie', 'get').mockReturnValue(`${defaultConfig.langCookieName}=en`);
            const spyGetBrowserLang = jest.spyOn(singletonCore, "getBrowserCultureLang");

            localize();

            expect(spyGetBrowserLang).toHaveBeenCalledTimes(0);
        });

        it('should get the language from the browser when cookie with the given name is not provided', () => {
            jest.spyOn(document, 'cookie', 'get').mockReturnValue(`not-correct-key=en`);
            const spyGetBrowserLang = jest.spyOn(singletonCore, "getBrowserCultureLang");

            localize();

            expect(spyGetBrowserLang).toHaveBeenCalledTimes(1);
        });
    });

    describe('I18n client instantiation tests', () => {
        it('should successfully instantiate the i18n client with the default config', () => {
            const spyInit = jest.spyOn(singletonCore.i18nClient, "init");
            const currentLang = "en_US";
            jest.spyOn(document, 'cookie', 'get').mockReturnValue(`${defaultConfig.langCookieName}=${currentLang}`);

            localize();

            let {
                productID,
                version,
                component,
                host
            } = defaultConfig;
            expect(spyInit).toHaveBeenCalledWith({ productID, version, component, host, language: currentLang, sourceBundle: ENGLISH, isPseudo: false });
        });

        it('should instantiate the i18nclient by combining the provided config and the default one', () => {
            const spyInit = jest.spyOn(singletonCore.i18nClient, "init");
            const currentLang = "en_US";
            const component = "testComponent";
            const version = "1.5.6";
            jest.spyOn(document, 'cookie', 'get').mockReturnValue(`${defaultConfig.langCookieName}=${currentLang}`);

            localize({ component, version });

            let {
                productID,
                host
            } = defaultConfig;
            expect(spyInit).toHaveBeenCalledWith({ productID, version, component, host, language: currentLang, sourceBundle: ENGLISH, isPseudo: false });
        });
    });

});