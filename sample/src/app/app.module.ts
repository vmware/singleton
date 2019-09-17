import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ClarityModule } from '@clr/angular';
import {
    VIPModule,
    VIPService,
    PatternCategories,
    LocaleService,
    VIPLoader,
    I18nLoader
} from '@singleton-i18n/angular-client';
import { ENGLISH } from './source/app.l10n';
import { L10nModule } from './modules/l10n/l10n.module';
import { I18nModule } from './modules/i18n/i18n.module';
import { VIPHeaderModule } from './modules/vip-header/vip-header.module';


export function initVIPConfig(service: VIPService, localeService: LocaleService) {
    localeService.init('zh-Hans');
    return () => service.initData({
        productID: 'SingletonSample',
        component: 'default',
        version: '1.0.0',
        i18nScope: [
            PatternCategories.DATE,
            PatternCategories.NUMBER,
            PatternCategories.CURRENCIES
        ],
        host: 'http://localhost:8091/',
        isPseudo: false,
        collectSource: false,
        sourceBundle: ENGLISH,
        timeout: 5000
    });
}

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        ClarityModule,
        L10nModule,
        VIPModule.forRoot({
            coreLoader: {
                provide: I18nLoader,
                useClass: VIPLoader
            }
        }),
        I18nModule,
        VIPHeaderModule,
    ],
    providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: initVIPConfig,
            deps: [
                VIPService,
                LocaleService
            ],
            multi: true
        }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
    // constructor(localeService: LocaleService, service: VIPService) {
    //     localeService.init('zh-Hans');
    //     service.initData({
    //         productID: 'SingletonSample',
    //         component: 'default',
    //         version: '1.0.0',
    //         i18nScope: [
    //             PatternCategories.DATE,
    //             PatternCategories.NUMBER,
    //             PatternCategories.CURRENCIES
    //         ],
    //         host: 'http://localhost:8091/',
    //         isPseudo: false,
    //         collectSource: false,
    //         sourceBundle: ENGLISH,
    //         timeout: 5000
    //     });
    // }
}
