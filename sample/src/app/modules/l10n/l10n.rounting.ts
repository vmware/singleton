import { ModuleWithProviders } from '@angular/core/src/metadata/ng_module';
import { Routes, RouterModule } from '@angular/router';
import { L10nComponent } from './l10n/l10n.component';
import { L10nPipeComponent } from './l10n-pipe/l10n-pipe.component';
import { L10nDirectiveComponent } from './l10n-directive/l10n-directive.component';
import { L10nServiceComponent } from './l10n-service/l10n-service.component';


const ROUTES: Routes = [
    {
        path: 'l10n',
        component: L10nComponent,
        children: [
            {
                path: '',
                redirectTo: 'pipe',
                pathMatch: 'full',
            },
            {
                path: 'pipe',
                component: L10nPipeComponent,
            },
            {
                path: 'directive',
                component: L10nDirectiveComponent,
            },
            {
                path: 'service',
                component: L10nServiceComponent
            }
        ],
    }
];

export const ROUTING: ModuleWithProviders = RouterModule.forChild(ROUTES);
