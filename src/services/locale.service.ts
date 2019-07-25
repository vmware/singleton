/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { isDefined } from '../util';

@Injectable({
    providedIn: 'root',
})
export class LocaleService {

    /**
     * Fired when user changed current locale.
     * Return the combination of language and region.
     */
    public UserLocaleChanged: Subject<string> = new Subject<string>();
    public defaultLanguage = { languageTag: 'en', displayName: 'English' };
    public defaultRegion = { regionCode: 'US', regionName: 'United States' };
    public defaultLocale = 'en-US';
    private _currentLanguage: string;
    private _currentRegion: string;
    private _currentLocale: string;

    constructor() {
        this._currentLanguage = this.defaultLanguage.languageTag;
        this._currentRegion = this.defaultRegion.regionCode;
        this._currentLocale = this.defaultLocale;
    }

    getCurrentLanguage(): string {
        return this._currentLanguage;
    }

    setCurrentLanguage(currentLang: string) {
        if (currentLang !== this._currentLanguage) {
            this._currentLanguage = currentLang;
            this.sendUserLocaleEvent();
        }
    }

    getCurrentRegion(): string {
        return this._currentRegion;
    }

    setCurrentRegion(currentRegion: string) {
        if (currentRegion !== this._currentRegion) {
            this._currentRegion = currentRegion;
            this.sendUserLocaleEvent();
        }
    }

    getCurrentLocale(): string {
        this._currentLocale = this._currentRegion ?
            this.composeLocale(this._currentLanguage, this._currentRegion)
            : this._currentLanguage;
        return this._currentLocale;
    }


    /**
     * set current language and region combination at application bootstrap time
     * or at runtime trigger by user selection.
     * @param languageTag for transaltion and plural rule
     * @param regionCode for the l2 formatting purpose
     * @param silentMode if only need to change the value without notification
     */
    setCurrentLocale(languageTag: string, regionCode?: string, silentMode = false): string {
        if (languageTag !== this._currentLanguage || regionCode !== this._currentRegion) {
            this._currentLanguage = languageTag;
            this._currentRegion = regionCode;
            this._currentLocale = this._currentRegion ?
                this.composeLocale(this._currentLanguage, this._currentRegion)
                : this._currentLanguage;
            if (!silentMode) { this.sendUserLocaleEvent(); }
        }
        return this._currentLocale;
    }

    public composeLocale(language: string, region: string) {
        return language && region ?
            `${language}-${region}` : language;
    }

    private sendUserLocaleEvent(): void {
        this.UserLocaleChanged.next(this.getCurrentLocale());
    }


    get isSourceLocale(): boolean {
        return this.shouldSourceLocale(this._currentLanguage, this._currentRegion);
    }

    public shouldSourceLocale(language: string, region?: string): boolean {
        return this.shouldSourceLanguage(language)
            && isDefined(region)
            && region.toUpperCase() === this.defaultRegion.regionCode ? true
            : this.shouldSourceLanguage(language) && !isDefined(region) ? true
                : false;
    }

    private resolveLanguageTag(languageTag: string) {
        if (!isDefined(languageTag)) {
            return languageTag;
        }
        return languageTag.split('_').join('-').toLocaleLowerCase();
    }

    get isSourceLanguage(): boolean {
        return this.shouldSourceLanguage(this._currentLanguage);
    }

    public shouldSourceLanguage(language: string): boolean {
        return language.toLowerCase() === this.defaultLanguage.languageTag ? true
            : this.defaultLocale.toLowerCase() === this.resolveLanguageTag(language) ? true
                : false;
    }

    /**
     * Reserve interface for language tag normalization in bundle mode.
     * @param languageTag
     */
    public normalizeLanguageTag(languageTag: string): string {
        return languageTag;
    }
}
