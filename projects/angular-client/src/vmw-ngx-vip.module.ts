/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { NgModule, ModuleWithProviders, Provider } from '@angular/core';

import { VIPLoader, I18nLoader } from './loader';
import { VIPService } from './services/vip.service';
import { L10nDirective } from './l10n.directive';
import { L10nPipe, L10nPipePlus } from './l10n.pipe';
import { DateFormatter } from './formatters/date.formatter';
import { MessageFormat } from './formatters/message.formatter';
import {
    DateFormatPipe,
    CurrencyFormatPipe,
    NumberFormatPipe,
    PercentFormatPipe
} from './i18n.pipe';
import { L10nService } from './services/l10n.service';
import { I18nService } from './services/i18n.service';
import { LocaleService } from './services/locale.service';
import { ResponseParser, VIPResponseParser } from './response.parser';

export interface ModuleConfig {
    coreLoader?: Provider;
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
