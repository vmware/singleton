/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

import { L10nService } from './services/l10n.service';

export class LocalizedComponent {
    l10nService: L10nService;

    translate(key: string, ...args: any[]): string {
        const self: any = this as any;
        const mixinData = self.__proto__.mixinData as any;
        const english = mixinData.L10nSourceMap[key];
        const i18nConfig = mixinData && mixinData.Namespace;
        if (!english) {
            return `!!NO ENGLISH for ${key}!!`;
        }
        let l10nKey = mixinData.L10nKey + '.' + key;
        // Support LocalizedComponent usage in isolated mode.
        // Get the namespace based on the i18n configuration to form the final key.
        if (i18nConfig) {
            l10nKey = this.l10nService.getLongKey(i18nConfig, l10nKey);
        }
        let translation: string;
        const onTranslation = (locale: string) => {
            if (locale === this.l10nService.currentLocale) {
                translation = this.l10nService.translate(l10nKey, english, args);
            }
        };
        this.l10nService.current.subscribe(onTranslation);
        return translation;
    }
}
