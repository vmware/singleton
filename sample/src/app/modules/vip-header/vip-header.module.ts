import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { VIPService } from '@singleton-i18n/angular-client';
import { L10nPipe, libConfig } from './i18n.util';
import { ClarityModule } from '@clr/angular';
import '@clr/icons';
import { HeaderComponent } from './header/header.component';

@NgModule({
    declarations: [
        HeaderComponent,
        L10nPipe],
    imports: [
        CommonModule,
        ClarityModule,
        RouterModule
    ],
    exports: [
        HeaderComponent
    ]
})
export class VIPHeaderModule {
    constructor(private service: VIPService) {
        this.service.registerComponent(libConfig);
   }
}
