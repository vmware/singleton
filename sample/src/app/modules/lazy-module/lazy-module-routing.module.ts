import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CotactComponent } from './contact/contact.component';

const routes: Routes = [
    {
        path: '',
        component: CotactComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class LazyModuleRoutingModule { }
