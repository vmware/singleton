import { ModuleWithProviders } from '@angular/core/src/metadata/ng_module';
import { Routes, RouterModule } from '@angular/router';
import { I18nComponent } from './i18n/i18n.component';
import { NumberComponent } from './number/number.component';
import { CurrencyComponent } from './currency/currency.component';
import { DateComponent } from './date/date.component';
import { PercentComponent } from './percent/percent.component';
import { I18nServiceComponent } from './i18n-service/i18n-service.component';




const ROUTES: Routes = [{
    path: 'i18n',
    component: I18nComponent,
    children: [{
        path: '',
        redirectTo: 'date',
        pathMatch: 'full',
    }, {
        path: 'number',
        component: NumberComponent,
    },
    {
        path: 'currencies',
        component: CurrencyComponent,
    },
    {
        path: 'date',
        component: DateComponent,
    },
    {
        path: 'percent',
        component: PercentComponent,
    },
    {
        path: 'service',
        component: I18nServiceComponent
    }]
}];

export const ROUTING: ModuleWithProviders = RouterModule.forChild(ROUTES);
