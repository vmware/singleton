/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { I18nService } from '../services/i18n.service';
import { Constants } from '../constants';

import { parse } from './plural/plural.message.parser';

export class MessageFormat {
    private locale: string;
    constructor(private i18nService: I18nService) { }

    public format(locale: string, message: string, args: any) {
        this.locale = locale;
        let isPseudo: boolean;
        const pseudoTag = Constants.PSEUDO_TAG,
            reg = new RegExp(`^(${pseudoTag})(.*?)(${pseudoTag})$`, 'g');
        const regResult: string[]|null = reg.exec(message);
        if (regResult) {
            message = regResult[2];
            isPseudo = true;
        }
        const messageAST = parse(message);
        let result = this.interpret(messageAST, args);
        result = isPseudo ? pseudoTag + result + pseudoTag : result;
        return result;
    }

    public interpret(
        ast: any[]/*: AST */,
        args?: any[]|{}
    ): string {
        return this.interpretAST(ast, null, args);
    }

    private interpretAST(
        elements: any[],
        parent: any,
        args?: any[]|{}
    ): string {
        const parts = elements.map((element) => {
            const res = this.interpretElement(element, parent, args);
            return res;
        });
        let message = '';
        for (let e = 0; e < parts.length; ++e) {
            message += parts[e];
        }
        return message;
    }

    private interpretElement(element: any, parent: any[], args?: any[]|{}): string {
        if (typeof element === 'string') {
            return element;
        }
        let id = element[0];
        const type = element[1];
        let res;

        if (parent && element[0] === '#') {
            id = parent[0];
            const functionNameForNumber = this.getFunctionName('number');
            const value = this.getArg(id, args);
            res = (this as any)[functionNameForNumber](value);
            return res;
        }

        const children: { [key: string]: any } = {};
        if (type === 'plural') {
            Object.keys(element[2]).forEach((key) => {
                children[key] = this.interpretAST(element[2][key], element, args);
            });
            element = [element[0], element[1], children];
        }

        const functionName = type ? this.getFunctionName(type) : undefined;
        if (functionName) {
            const value = this.getArg(id, args);
            switch (type) {
                case 'plural':
                    res = this.interpretPlural(element, value);
                    break;
                case 'number':
                    res = this.interpretNumber(value);
                    break;
            }
        } else if (id && !type) {
            res = this.getArg(id, args);
        }
        return String(res);
    }

    private interpretPlural(element: any[], value: number) {
        const children = element[2];
        const type = this.i18nService.getPluralCategoryType(value, this.locale);
        const clause =
            children['=' + +value] ||
            children[type] ||
            children.other;
        return clause;
    }

    private interpretNumber(value: number): string {
        return this.i18nService.formatNumber(value);
    }

    private getFunctionName(type: string) {
        const arr = type.split('');
        const first = arr.slice(0, 1)[0].toUpperCase();
        const h = arr.slice(1).join('');
        const str = first + h;
        const name = 'interpret' + str;
        return (this as any)[name] ? name : undefined;
    }

    private getArg(id: string, args?: any) {
        if (args && (id in args)) { return args[id]; }
        const parts = id.split('.');
        let a = args;
        for (let i = 0, length = parts.length; a && i < length; ++i) {
            a = a[parts[i]];
        }
        return a || id;
    }

}

