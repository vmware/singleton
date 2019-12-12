import { Constants } from '../constants';

import { pluralMessageParser } from '../formatters/plural/plural.message.parser';
import { I18nService } from '../services/i18n.service';


export class MessageFormat {
	private isFallback: boolean;
	constructor(private i18nService: I18nService) {
		if (this.interpretNumber && this.interpretPlural && this.interpretSelect) {
			this.isFallback = false;
		}
	}

	public format(isFallback: boolean, message: string, args: any) {
		this.isFallback = isFallback;
		let isPseudo: boolean;
		const pseudoTag = Constants.PSEUDO_TAG,
			reg = new RegExp(`^(${pseudoTag})(.*?)(${pseudoTag})$`, 'g');
		const regResult: string[] | null = reg.exec(message);
		if (regResult) {
			message = regResult[2];
			isPseudo = true;
		}
		const parsed = pluralMessageParser(message);
		let result = this.interpret(parsed, args);
		result = isPseudo ? pseudoTag + result + pseudoTag : result;
		return result;
	}
	public parseMessage(isFallback: boolean, message: string, args: any) {
		this.isFallback = isFallback;
		const pseudoTag = Constants.PSEUDO_TAG,
			reg = new RegExp(`^(${pseudoTag})(.*?)(${pseudoTag})$`, 'g');
		const regResult: string[] | null = reg.exec(message);
		if (regResult) {
			message = regResult[2];
		}
		const parsed = pluralMessageParser(message);
		return parsed;
	}

	public interpret(
		ast: any[]/*: AST */,
		args?: any[] | {}
	): string {
		return this.interpretAST(ast, null, true, args);
	}

	private interpretAST(
		elements: any[],
		parent: any,
		join: boolean,
		args?: any[] | {}
	): string {
		const parts = elements.map((element) => {
			const res = this.interpretElement(element, parent, join, args);
			return res;
		});
		let message = '';
		for (let e = 0; e < parts.length; ++e) {
			message += parts[e];
		}
		return message;
	}

	private interpretElement(element: any, parent: any[], join: boolean, args?: any[] | {}): string {
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

		// pre-process children
		const children: { [key: string]: any } = {};
		if (type === 'plural' || type === 'selectordinal') {
			Object.keys(element[3]).forEach((key: string) => {
				children[key] = this.interpretAST(element[3][key], element, join, args);
			});
			element = [element[0], element[1], element[2], children];
		} else if (element[2] && typeof element[2] === 'object') {
			Object.keys(element[2]).forEach((key) => {
				children[key] = this.interpretAST(element[2][key], element, join, args);
			});
			element = [element[0], element[1], children];
		}

		const functionName = type ? this.getFunctionName(type) : undefined;
		if (functionName) {
			const value = this.getArg(id, args);
			res = (this as any)[functionName](element, value);
		} else if (id && !type) {
			res = this.getArg(id, args);
		}
		return join
			? String(res)
			: res;
	}

	private interpretPlural(element: any[], value: number) {
		// fallback to sourceLocale
		const children = element[3];
		const language = this.isFallback ? Constants.SOURCE_LANGUAGE : null;
		const type = this.i18nService.getPluralCategoryType(value, language);
		const clause =
			children['=' + value] ||
			children[type] ||
			children.other;
		return clause;
	}

	private interpretNumber(value: number): string {
		const language = this.isFallback ? Constants.SOURCE_LANGUAGE : null;
		return this.i18nService.formatNumber(value, language);
	}

	private interpretSelect(element: any[], value: any): string {
		const children = element[2];
		return children[value] || children.other;
	}
	private getFunctionName(type: string) {
		const arr = type.split('');
		const first = arr.slice(0, 1)[0].toUpperCase();
		const h = arr.slice(1).join('');
		const str = first + h;
		const name = 'interpret' + str;
		return (this as any)[name] ? name : undefined;
	}

	private getArg(id: string, args?: string[] | {}) {
		if (args && (id in args)) { return (args as any)[id]; }
		const parts = id.split('.');
		let a = args;
		for (let i = 0, length = parts.length; a && i < length; ++i) {
			a = (args as any)[parts[i]];
		}
		return a || id;
	}

}

