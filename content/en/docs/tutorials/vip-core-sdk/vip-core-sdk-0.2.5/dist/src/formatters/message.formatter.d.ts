import { I18nService } from '../services/i18n.service';
export declare class MessageFormat {
    private i18nService;
    private locale;
    constructor(i18nService: I18nService);
    format(locale: string, message: string, args: any): string;
    interpret(ast: any[], args?: any[] | {}): string;
    private interpretAST;
    private interpretElement;
    private interpretPlural;
    private interpretNumber;
    private getFunctionName;
    private getArg;
}
