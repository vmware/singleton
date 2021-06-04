/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { isDefined } from '../util';
import { VIPServiceConstants } from '../constants';
import { LanguageMap } from '../data/language.map';
import { I18nContext } from './i18n.context';
export interface VLocale {
    readonly languageCode: string;
    readonly languageName: string;
    readonly regionCode: string;
    readonly regionName: string;
}

@Injectable({
    providedIn: 'root',
})
export class LocaleService {

    /**
     * Fired when user changed current locale.
     * Return the combination of language and region.
     */
    public userLocaleChanged: Subject<string> = new Subject<string>();
    public defaultLocale: VLocale = VIPServiceConstants.ENGLISH;
    private currentLanguage: string;
    private currentRegion: string;

    /**
     * by default, the initial locale is en-US.
     */
    constructor(
        protected i18nContext: I18nContext,
    ) {
        this.setLocale(this.defaultLocale.languageCode,
            this.defaultLocale.regionCode);
    }

    /**
     * initialize language and region combination at app bootstrap.
     * @param language language code for translation and plural rule.
     * @param region region code for the l2 formatting patterns.
     */
    public init(language: string, region?: string) {
        if (isDefined(language)) {
            this.setLocale(language, region);
            this.i18nContext.preferredLanguage = language;
        }
        if (isDefined(region)) {
            this.i18nContext.preferredRegion = region;
        }
    }

    /**
     * set default locale and initialize current language and current region.
     * this should be executed when the application start.
     * @param defaultLocale will be used as fallback locale.
     */
    public setDefaultLocale(defaultLocale: VLocale) {
        this.defaultLocale = defaultLocale || this.defaultLocale;
        this.setLocale(this.defaultLocale.languageCode,
            this.defaultLocale.regionCode);
    }

    private setLocale(language: string, region?: string) {
        if (language !== this.currentLanguage ||
            region !== this.currentRegion) {
            this.currentLanguage = language;
            this.currentRegion = region;
        }
    }

    public getCurrentLanguage(): string {
        return this.currentLanguage;
    }

    public setCurrentLanguage(currentLang: string) {
        if (currentLang !== this.currentLanguage) {
            this.currentLanguage = currentLang;
            this.sendUserLocaleEvent();
        }
    }

    public getCurrentRegion(): string {
        return this.currentRegion;
    }

    public setCurrentRegion(currentRegion: string) {
        if (currentRegion !== this.currentRegion) {
            this.currentRegion = currentRegion;
            this.sendUserLocaleEvent();
        }
    }

    public getCurrentLocale(): string {
        return this.composeLocale(this.currentLanguage, this.currentRegion);
    }

    /**
     * set current language and region combination at runtime.
     * and notify VIP service to load corresponding i18n resource.
     * @param language language code for translation and plural rule.
     * @param region region code for the l2 formatting patterns.
     */
    public setCurrentLocale(language: string, region?: string) {
        this.setLocale(language, region);
        this.sendUserLocaleEvent();
    }

    /**
     * compose the sample locale structure based on the current usage scenarios.
     * @param language language code for translation and plural rule.
     * @param region region code for the l2 formatting patterns.
     */
    public composeLocale(language: string, region: string) {
        return language && region ?
            `${language}-${region}` : language;
    }

    private sendUserLocaleEvent(): void {
        this.userLocaleChanged.next(this.getCurrentLocale());
    }

    public get isSourceLocale(): boolean {
        return this.shouldSourceLocale(this.currentLanguage, this.currentRegion);
    }

    public shouldSourceLocale(language: string, region?: string): boolean {
        return this.shouldSourceLanguage(language)
            && isDefined(region)
            && region.toUpperCase() === this.defaultLocale.regionCode ? true
            : this.shouldSourceLanguage(language) && !isDefined(region) ? true
                : false;
    }

    private resolveLanguageTag(language: string) {
        if (!isDefined(language)) {
            return language;
        }
        return language.split('_').join('-').toLocaleLowerCase();
    }

    public get isSourceLanguage(): boolean {
        return this.shouldSourceLanguage(this.currentLanguage);
    }

    public shouldSourceLanguage(language: string): boolean {
        return language.toLowerCase() === this.defaultLocale.languageName ? true
            : this.defaultLocale.languageCode.toLowerCase() === this.resolveLanguageTag(language) ? true
                : `${this.defaultLocale.languageCode}-${this.defaultLocale.regionCode}`.toLowerCase()
                    === this.resolveLanguageTag(language) ? true
                    : false;
    }

    /**
     * Reserve interface for language tag normalization in bundle mode.
     * @param language language code for translation and plural rule.
     */
    public normalizeLanguageCode(language: string): string {
        const lang = language.replace('_', '-').toLowerCase();
        if (LanguageMap[lang]) {
            return LanguageMap[lang][0];
        }
        return language;
    }
}
