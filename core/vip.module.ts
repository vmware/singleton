/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { NgModule, ModuleWithProviders, Provider } from '@angular/core';

import { VIPLoader, I18nLoader } from './src/loader';
import { VIPService } from './src/services/vip.service';
import { L10nDirective } from './src/l10n.directive';
import { L10nPipe, L10nPipePlus } from './src/l10n.pipe';
import { DateFormatter } from './src/formatters/date.formatter';
import { MessageFormat } from './src/formatters/message.formatter';
import {
    DateFormatPipe,
    CurrencyFormatPipe,
    NumberFormatPipe,
    PercentFormatPipe,
    I18nPipe
} from './src/i18n.pipe';
import { L10nService } from './src/services/l10n.service';
import { I18nService } from './src/services/i18n.service';
import { LocaleService } from './src/services/locale.service';
import { ResponseParser, VIPResponseParser } from './src/response.parser';
import { DefaultI18nContext, I18nContext } from './src/services/i18n.context';

export interface ModuleConfig {
    coreLoader?: Provider;
    i18nContext?: Provider;
}

export function provideRoot(config: ModuleConfig = {}): any[] {
    return [
        {
            provide: ResponseParser,
            useClass: VIPResponseParser
        },
        config.coreLoader || {
            provide: I18nLoader,
            useClass: VIPLoader
        },
        config.i18nContext || {
            provide: I18nContext,
            useClass: DefaultI18nContext
        },
        VIPService,
        LocaleService,
        L10nService,
        I18nService,
        DateFormatter,
        MessageFormat
    ];
}

export function provideChild(config: ModuleConfig = {}): any[] {
    return [
        config.coreLoader || {
            provide: I18nLoader,
            useClass: VIPLoader
        },
        VIPService,
        L10nService,
        I18nService,
        MessageFormat
    ];
}


@NgModule({
    declarations: [
        I18nPipe,
        L10nPipe,
        L10nPipePlus,
        L10nDirective,
        DateFormatPipe,
        CurrencyFormatPipe,
        NumberFormatPipe,
        PercentFormatPipe
    ],
    exports: [
        L10nPipe,
        L10nPipePlus,
        L10nDirective,
        DateFormatPipe,
        CurrencyFormatPipe,
        NumberFormatPipe,
        PercentFormatPipe
    ]
})
export class VIPModule {
    /**
     * Use this method in your root module to provide the VIP Service
     * @returns Module instance
     * @memberof VipModule
     */
    static forRoot(config: ModuleConfig = {}): ModuleWithProviders<VIPModule> {
        return {
            ngModule: VIPModule,
            providers: provideRoot(config)
        };
    }

    static forChild(config: ModuleConfig = {}): ModuleWithProviders<VIPModule> {
        return {
            ngModule: VIPModule,
            providers: provideChild(config)
        };
    }
}
