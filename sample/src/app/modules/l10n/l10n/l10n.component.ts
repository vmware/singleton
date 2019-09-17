import { Component, OnInit } from '@angular/core';
import { L10nService } from '@singleton-i18n/angular-client';


@Component({
    selector: 'l10n',
    templateUrl: './l10n.component.html',
    styleUrls: ['./l10n.component.css']
})
export class L10nComponent implements OnInit {

    constructor(private l10nService: L10nService) { }

    ngOnInit() {
    }

    t(key: string, args: string[]) {
        let translation: string;
        const onTranslation = (locale: string) => {
            if (locale === this.l10nService.currentLocale) {
                translation = this.l10nService.getMessage(key, args, locale);
            }
        };
        this.l10nService.current.subscribe(onTranslation);
        return translation;
    }

}
