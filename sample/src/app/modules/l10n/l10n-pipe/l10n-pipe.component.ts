import { Component, OnInit } from '@angular/core';
import { L10nService } from '@singleton-i18n/angular-client';

import { DATA } from '../l10n.data';

@Component({
    selector: 'l10n-pipe',
    templateUrl: './l10n-pipe.component.html',
    styleUrls: ['./l10n-pipe.component.css']
})
export class L10nPipeComponent implements OnInit {
    datas: Object = [];
    routes: Object = {};
    constructor(private l10n: L10nService) { }

    ngOnInit() {
        this.datas = DATA;
    }

}

