import { Component, OnInit, OnDestroy } from '@angular/core';
import { L10nService } from '@singleton-i18n/angular-client';

import { DATA } from '../l10n.data';

@Component({
    selector: 'l10n-service',
    templateUrl: './l10n-service.component.html',
    styleUrls: ['./l10n-service.component.css']
})
export class L10nServiceComponent implements OnInit, OnDestroy {
    data: any;
    source: string;
    subscription: any;
    message: string;
    translation: string;

    constructor(private l10nService: L10nService) { }

    ngOnInit() {
        this.data = DATA;
        this.source = this.l10nService.getSourceString(this.data[0].key);
        this.subscription = this.l10nService.stream.subscribe((locale: string) => {
            this.message = this.l10nService.getMessage(this.data[1].key, this.data[1].variables, locale);
            this.translation = this.l10nService.translate(this.data[3].key, this.data[3].source, this.data[3].variables, locale);
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.subscription = undefined;
    }
}

