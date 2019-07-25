import { ModuleWithProviders } from '@angular/core/src/metadata/ng_module';
import { Routes, RouterModule } from '@angular/router';

import { HeaderComponent } from './header/header.component';

const ROUTES: Routes = [{
    path: '',
    component: HeaderComponent
}];

export const ROUTING: ModuleWithProviders = RouterModule.forChild(ROUTES);
