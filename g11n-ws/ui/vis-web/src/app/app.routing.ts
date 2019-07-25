/*
* Copyright 2019 VMware, Inc.
* SPDX-License-Identifier: EPL-2.0
*/
import { ModuleWithProviders } from '@angular/core/src/metadata/ng_module';
import { Routes, RouterModule } from '@angular/router';

import { AboutComponent } from './about/about.component';
import { HomeComponent } from './home/home.component';


export const ROUTES: Routes = [
    {path: '', redirectTo: 'home', pathMatch: 'full'},
    {path: 'home', component: HomeComponent},
    {path: 'about', component: AboutComponent}
];

export const ROUTING: ModuleWithProviders = RouterModule.forRoot(ROUTES);
