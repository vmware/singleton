import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
    {
        path: '',
        redirectTo: 'l10n',
        pathMatch: 'full'
    },
    {
        path: 'contact',
        loadChildren: 'src/app/modules/lazy-module/lazy.module#LazyModule'
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
