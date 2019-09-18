import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VIPModule, VIPService } from '@singleton-i18n/angular-client';

import { LazyModuleRoutingModule } from './lazy-module-routing.module';
import { CotactComponent } from './contact/contact.component';
import { ENGLISH } from './contact.l10n';

@NgModule({
    declarations: [CotactComponent],
    imports: [
        CommonModule,
        LazyModuleRoutingModule,
        VIPModule.forChild(),
    ]
})
export class LazyModule {
    constructor(private vipService: VIPService) {
        this.vipService.initData({
            productID: 'SingletonContact',
            component: 'default',
            version: '1.0.0',
            host: 'http://localhost:8091/',
            isPseudo: false,
            collectSource: false,
            sourceBundle: ENGLISH
        });
    }
}
