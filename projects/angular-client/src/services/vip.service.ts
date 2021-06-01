/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { Injectable } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { LocaleService } from './locale.service';
import { I18nLoader } from '../loader';
import { mergeObject, assign, deprecatedWarn } from '../util';
import { concat, Observable, forkJoin, Observer, of, Subscription } from 'rxjs';
import { share, switchMap, take, catchError } from 'rxjs/operators';
import { VIPConfig, getNameSpace, VIPConfigFactory } from '../config';
import { sourceBundleObject } from './l10n.service';
import { I18nContext } from './i18n.context';

export interface LocaleData {
    categories: Object;
    messages: { [key: string]: any };
}

export interface LoadingLocaleData {
    locale: string;
    data: Observable<any>;
}

@Injectable()
export class VIPService {
    public mainConfig: VIPConfig;
    private pending = false;
    private loadingLocaleData: LoadingLocaleData;
    private configs: VIPConfig[] = [];
    private availableLocales: Array<string> = [];
    private localeSubscription: Subscription;
    private _onLocaleChange: EventEmitter<string> = new EventEmitter<string>();
    private _i18nScope: Array<string> = [];
    private _localeData: any = {};

    constructor(
        protected localeService: LocaleService,
        protected i18nContext: I18nContext,
        public currentLoader: I18nLoader
    ) { }

    get onLocaleChange(): EventEmitter<string> {
        return this._onLocaleChange;
    }

    get localeData(): any {
        return this._localeData;
    }

    get i18nScope() {
        return this._i18nScope;
    }

    /**
     * Loading the i18n data at app module or root of lazy module.
     * @param config For root module or lazy load mdule.
     */
    public async initData(config: VIPConfig): Promise<any> {
        this.subscribeLocaleSubject();
        this.registerComponent(config, true);
        this.initLocale(config);
        await this.loadLocaleData();
    }

    /**
     * Init locale by config passed from root module for compatibility.
     * Will be removed in v9.
     * @param config
     */
    private initLocale(config: VIPConfig) {
        if ( config.locale || config.language || config.region ) {
            console.warn(
                'Set language and region by configuration is deprecated, will be removed in v9. ' +
                'Please use LocaleService.init instead.');
            if ( config.language && config.region ) {
                this.localeService.init(config.language, config.region);
            } else if (config.locale) {
                this.localeService.init(config.locale);
            }
        }
    }
    /**
     * Subscrible the locale changed event from view layer.
     * For live UI translation update.
     */
    public subscribeLocaleSubject() {
        if (!this.localeSubscription) {
            this.localeSubscription = this.localeService.userLocaleChanged
                .subscribe(() => { this.loadLocaleData(); });
        }
    }

    /**
     * Specifically, for the feature module which will be imported
     * to root module or lazy load module. This method should be invoked
     * in the constructor of feature module.
     * @param config
     * @param isMain
     */
    public registerComponent(config: VIPConfig, isMain: boolean = false) {
        this.subscribeLocaleSubject();
        config = VIPConfigFactory(config, this.i18nContext);
        this.mainConfig = isMain ? config : this.mainConfig;
        this.configs.push(config);
        this.updateI18nScope(config);
        if (config.sourceBundles || config.sourceBundle) {
            if (config.sourceBundle) {
                deprecatedWarn('The sourceBundle option in i18n config', 'v10', 'sourceBundles')
            }
            const bundle = config.sourceBundles ? this.resetBundle(config.sourceBundles) : config.sourceBundle;
            this.processBundle(bundle,
                this.localeService.defaultLocale.languageCode, config);
        }
    }

    /**
     * Tentative solution, if translation bundle is mounted.
     * no longer to consider i18n scope which defined in
     * feature module configuration.
     * @param config
     */
    private updateI18nScope(config: VIPConfig) {
        if (config.i18nScope && !config.translationBundles) {
            this._i18nScope = this._i18nScope.concat(config.i18nScope)
                .filter( (elem, index, self) => {
                    return index === self.indexOf(elem);
                });
        }
    }

    /**
     * If the locale is processed or source locale, skip.
     */
    public async loadLocaleData(): Promise<any> {
        const language = this.localeService.getCurrentLanguage();
        const region = this.localeService.getCurrentRegion();
        const locale = this.localeService.getCurrentLocale();
        if (!this.localeService.isSourceLocale &&
            this.availableLocales.indexOf(locale) === -1
            && this.i18nContext.i18nEnabled !== false
        ) {
            await this.getLocaleData(language, region, locale)
                .toPromise();
        }
        this.releaseLocale(locale);
    }

    private getLocaleData(language: string, region: string, locale: string): Observable<any> {
        const sequencesOfRequest: Array<Observable<any>> = [];
        let loadingLocaleData: Observable<any>;
        for (const config of this.configs) {
            // Bypass sending HTTP call to get translation.
            // Using local bundle instead.
            // Using language to map local bundle.
            // Using locale to store the translation.
            if (config.translationBundles) {
                const standardLanguageTag = this.localeService.normalizeLanguageCode(language);
                const bundle = config.translationBundles[language] || config.translationBundles[standardLanguageTag];
                if (bundle) {
                    this.processBundle(bundle, locale, config);
                }
            } else {
                sequencesOfRequest.push(this.currentLoader
                    .getLocaleData(config, language, region)
                    .pipe(share(), catchError((err) => {
                        console.error(`Can't fetch locale data.`, config, err);
                        return of(undefined);
                    })));
            }
        }

        // No HTTP call required.
        // Resource of current locale is available.
        if (sequencesOfRequest.length < 1) {
            this.availableLocales.push(locale);
            return of([]);
        }

        loadingLocaleData = forkJoin(sequencesOfRequest);
        this.loadingLocaleData = {
            data: loadingLocaleData.pipe(
                take(1),
                share()
            ), locale: locale
        };

        // FIFO
        this.pending = true;
        this.loadingLocaleData.data.subscribe((res: LocaleData) => {
            this.storeResource(res, this._localeData, locale);
            this.availableLocales.push(locale);
            this.pending = false;
        }, (err: any) => {
            this.pending = false;
        });
        return loadingLocaleData;
    }

    private resetBundle(sourceBundles: any) {
        if (Array.isArray(sourceBundles)) {
            return assign({}, sourceBundles);
        }
        return undefined;
    }

    private processBundle(bundle: any, locale: string, config: VIPConfig) {
        const formattedBundle = { 'messages': {} };
        const namespace = getNameSpace(config);
        (formattedBundle.messages as any)[namespace] = bundle;
        this.storeResource([formattedBundle],
            this._localeData, locale);
    }

    private storeResource(res: any, localedata: any, locale: string) {
        if (res) {
            const supplemental: Object = {};
            localedata[locale] = localedata[locale] || {};
            res.forEach((element: any) => {
                if (element) {
                    if (element.messages) {
                        localedata[locale].messages =
                            mergeObject(localedata[locale].messages, element.messages);
                    }
                    if (element.categories) {
                        localedata[locale].categories =
                            mergeObject(localedata[locale].categories, element.categories);
                        if (element.categories.supplemental) {
                            Object.assign(supplemental, element.categories.supplemental);
                        }
                    }
                }
            });
            if (localedata[locale].categories) {
                localedata[locale].categories.supplemental = supplemental;
            }
        }
    }

    /**
     * current represents current available locale to cover the initial status.
     * since in some situation, the 'onLocaleChange' haven't be register always,
     * or there is no event at time.
     */
    get current(): Observable<string | any> {
        if (this.pending) {
            const locale = this.loadingLocaleData.locale;
            return Observable.create((observer: Observer<string>) => {
                const onComplete = () => {
                    observer.next(locale);
                    observer.complete();
                };
                this.loadingLocaleData.data.subscribe((res: LocaleData) => {
                    onComplete();
                }, onComplete);
            });
        } else {
            return of(this.localeService.getCurrentLocale());
        }
    }

    /**
     * stream of 'available' locale.
     */
    get stream(): Observable<string | any> {
        return concat(
            this.current,
            this.onLocaleChange.pipe(
                switchMap((locale: string) => {
                    return of(locale);
                })
            ));
    }

    private releaseLocale(locale: string) {
        this.onLocaleChange.emit(locale);
    }

    public registerSourceBundles(sourceBundles: sourceBundleObject[], config: VIPConfig) {
        if (sourceBundles) {
            const bundle = this.resetBundle(sourceBundles);
            this.processBundle(bundle,
                this.localeService.defaultLocale.languageCode, config);
        }
    }
}
