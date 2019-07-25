import { Component, OnInit } from '@angular/core';
import { DATA } from '../l10n.data';


@Component({
    selector: 'l10n-directive',
    templateUrl: './l10n-directive.component.html',
    styleUrls: ['./l10n-directive.component.css']
})
export class L10nDirectiveComponent implements OnInit {
    datas: any[];
    constructor() { }

    ngOnInit() {
        this.datas = DATA;
    }

}
