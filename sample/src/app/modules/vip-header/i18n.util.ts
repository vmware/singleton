import { L10nPipePlus } from '@singleton-i18n/angular-client';
import { ENGLISH } from './header.l10n';

export const libConfig = {
    productID: 'SingletonHeader',
    component: 'default',
    version: '1.0.0',
    host: 'http://localhost:8091/',
    sourceBundle: ENGLISH
};

export class L10nPipe extends L10nPipePlus {
    config = libConfig;
}
