import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';


import { VIPModule } from '@singleton-i18n/angular-client';

import { L10nComponent } from './l10n/l10n.component';
import { L10nPipeComponent } from './l10n-pipe/l10n-pipe.component';
import { ROUTING } from './l10n.rounting';
import { L10nDirectiveComponent } from './l10n-directive/l10n-directive.component';
import { L10nServiceComponent } from './l10n-service/l10n-service.component';


@NgModule({
    declarations: [
        L10nComponent,
        L10nPipeComponent,
        L10nDirectiveComponent,
        L10nServiceComponent
    ],
    imports: [
        CommonModule,
        ClarityModule,
        ROUTING,
        VIPModule
    ]
})
export class L10nModule {}
