import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { I18nComponent } from './i18n/i18n.component';
import { ROUTING } from './i18n.rounting';

import { NumberComponent } from './number/number.component';
import { VIPModule } from '@singleton-i18n/angular-client';
import { ClarityModule } from '@clr/angular';
import { CurrencyComponent } from './currency/currency.component';
import { DateComponent } from './date/date.component';
import { PercentComponent } from './percent/percent.component';
import { I18nServiceComponent } from './i18n-service/i18n-service.component';

@NgModule({
    declarations: [
        I18nComponent,
        NumberComponent,
        CurrencyComponent,
        DateComponent,
        PercentComponent,
        I18nServiceComponent
    ],
    imports: [
        CommonModule,
        BrowserAnimationsModule,
        ClarityModule,
        ROUTING,
        VIPModule
    ]
})
export class I18nModule { }
