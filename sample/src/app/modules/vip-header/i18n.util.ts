import { L10nPipePlus } from '@vip/ngx-vip';
import { HEADERSOURCE } from './header.l10n';

export const libConfig = {
    productID: 'SingletonHeader',
    component: 'default',
    version: '1.0.0',
    host: 'http://localhost:8091/',
    sourceBundle: HEADERSOURCE
};

export class L10nPipe extends L10nPipePlus {
    config = libConfig;
}
