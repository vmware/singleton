"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const constants_1 = require("../constants");
const plural_message_parser_1 = require("./plural/plural.message.parser");
class MessageFormat {
    constructor(i18nService) {
        this.i18nService = i18nService;
    }
    format(locale, message, args) {
        this.locale = locale;
        let isPseudo;
        const pseudoTag = constants_1.Constants.PSEUDO_TAG, reg = new RegExp(`^(${pseudoTag})(.*?)(${pseudoTag})$`, 'g');
        const regResult = reg.exec(message);
        if (regResult) {
            message = regResult[2];
            isPseudo = true;
        }
        const messageAST = plural_message_parser_1.parse(message);
        let result = this.interpret(messageAST, args);
        result = isPseudo ? pseudoTag + result + pseudoTag : result;
        return result;
    }
    interpret(ast /*: AST */, args) {
        return this.interpretAST(ast, null, args);
    }
    interpretAST(elements, parent, args) {
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
    interpretElement(element, parent, args) {
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
            res = this[functionNameForNumber](value);
            return res;
        }
        const children = {};
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
        }
        else if (id && !type) {
            res = this.getArg(id, args);
        }
        return String(res);
    }
    interpretPlural(element, value) {
        const children = element[2];
        const type = this.i18nService.getPluralCategoryType(value, this.locale);
        const clause = children['=' + +value] ||
            children[type] ||
            children.other;
        return clause;
    }
    interpretNumber(value) {
        return this.i18nService.formatNumber(value, this.locale);
    }
    getFunctionName(type) {
        const arr = type.split('');
        const first = arr.slice(0, 1)[0].toUpperCase();
        const h = arr.slice(1).join('');
        const str = first + h;
        const name = 'interpret' + str;
        return this[name] ? name : undefined;
    }
    getArg(id, args) {
        if (args && (id in args)) {
            return args[id];
        }
        const parts = id.split('.');
        let a = args;
        for (let i = 0, length = parts.length; a && i < length; ++i) {
            a = a[parts[i]];
        }
        return a || id;
    }
}
exports.MessageFormat = MessageFormat;
