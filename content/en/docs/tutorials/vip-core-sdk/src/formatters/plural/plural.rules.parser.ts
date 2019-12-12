export class Parser {
    private v0: number;
    private t0: number;
    private i: number;
    private f: number;
    private t: number;
    private v: number;
    private n10: number;
    private n100: number;
    public parse(cond: string) {
        if (cond === 'i = 0 or n = 1') { return 'n >= 0 && n <= 1'; }
        if (cond === 'i = 0,1') { return 'n >= 0 && n < 2'; }
        if (cond === 'i = 1 and v = 0') {
            this.v0 = 1;
            return 'n == 1 && v0';
        }
        return cond
            .replace(/([tv]) (!?)= 0/g, (m, sym, noteq) => {
                const sn = sym + '0';
                (this as any)[sn] = 1;
                return noteq ? '!' + sn : sn;
            })
            .replace(/\b[fintv]\b/g, m => {
                (this as any)[m] = 1;
                return m;
            })
            .replace(/([fin]) % (10+)/g, (m, sym, num) => {
                const sn: string = sym + num;
                (this as any)[sn] = 1;
                return sn;
            })
            .replace(/n10+ = 0/g, 't0 && $&')
            .replace(/(\w+ (!?)= )([0-9.]+,[0-9.,]+)/g, (m, se, noteq, x) => {
                if (m === 'n = 0,1') { return '(n == 0 || n == 1)'; }
                if (noteq) { return se + x.split(',').join(' && ' + se); }
                { return '(' + se + x.split(',').join(' || ' + se) + ')'; }
            })
            .replace(/(\w+) (!?)= ([0-9]+)\.\.([0-9]+)/g, (m, sym, noteq, x0, x1) => {
                if (Number(x0) + 1 === Number(x1)) {
                    if (noteq) { return `${sym} != ${x0} && ${sym} != ${x1}`; }
                    return `(${sym} == ${x0} || ${sym} == ${x1})`;
                }
                if (noteq) { return `(${sym} < ${x0} || ${sym} > ${x1})`; }
                if (sym === 'n') { this.t0 = 1; return `(t0 && n >= ${x0} && n <= ${x1})`; }
                return `(${sym} >= ${x0} && ${sym} <= ${x1})`;
            })
            .replace(/ and /g, ' && ')
            .replace(/ or /g, ' || ')
            .replace(/ = /g, ' == ');
    }

    vars() {
        const vars = [];
        if (this.i) { vars.push('i = s[0]'); }
        if (this.f || this.v) { vars.push(`f = s[1] || ''`); }
        if (this.t) { vars.push(`t = (s[1] || '').replace(/0+$/, '')`); }
        if (this.v) { vars.push('v = f.length'); }
        if (this.v0) { vars.push('v0 = !s[1]'); }
        if (this.t0 || this.n10 || this.n100) { vars.push('t0 = Number(s[0]) == n'); }
        for (const k in this) {
            if (/^.10+$/.test(k)) {
                const k0 = (k[0] === 'n') ? 't0 && s[0]' : k[0];
                vars.push(`${k} = ${k0}.slice(-${k.substr(2).length})`);
            }
        }
        if (!vars.length) { return ''; }
        return 'var ' + [`s = String(n).split('.')`, ...vars].join(', ');
    }
}

interface CategoriesData { [key: string]: any; }

export class GetPluralFn {
    private data: CategoriesData;
    private parser: Parser;
    private fn: any;
    private categories: CategoriesData;
    constructor(data: Object, cardinals: boolean = true, ordinals: boolean = false) {
        this.data = data;
        this.categories = { 'cardinal': [], 'ordinal': [] };
        this.parser = new Parser();
        this.fn = this.buildFunction(cardinals, ordinals);
        this.fn._obj = this;
        this.fn.categories = this.categories;
        this.fn.toString = this.fnToString.bind(this);
        return this.fn;
    }
    compile(type: string, ...req: Array<any>) {
        const rules = this.data;
        if (!rules) {
            if (req) { throw new Error(`rules not found`); }
            this.categories[type] = ['other'];
            return 'other';
        }
        const cases = [];
        for (const r in rules) {
            if (r) {
                const r1 = r.replace(/pluralRule-count-/g, '');
                const [cond] = rules[r].trim().split(/\s*@\w*/);
                const cat = r1;
                if (cond) { cases.push([this.parser.parse(cond), cat]); }
            }
        }
        this.categories[type] = cases.map(c => c[1]).concat('other');
        if (cases.length === 1) {
            return `(${cases[0][0]}) ? '${cases[0][1]}' : 'other'`;
        } else {
            return [...cases.map(c => `(${c[0]}) ? '${c[1]}'`), `'other'`].join('\n      : ');
        }
    }

    buildFunction(cardinals: boolean, ordinals: boolean) {
        const compile = (c: any) => c ? ((c[1] ? 'return ' : 'if (ord) return ') + this.compile(c)) : '';
        const fold = {
            vars: (str: any) => `  ${str};`.replace(/(.{1,78})(,|$) ?/g, '$1$2\n      '),
            cond: (str: any) => `  ${str};`.replace(/(.{1,78}) (\|\| |$) ?/gm, '$1\n          $2')
        };
        const cond = [
            ordinals && ['ordinal', !cardinals],
            cardinals && ['cardinal', true]
        ]
            .map(compile)
            .map(fold.cond);
        const body = [
            fold.vars(this.parser.vars()),
            ...cond
        ]
            .filter(line => !/^[\s;]*$/.test(line))
            .map(line => line.replace(/\s+$/gm, ''))
            .join('\n');
        const args = ordinals && cardinals ? 'n, ord' : 'n';
        return new Function(args, body);
    }

    fnToString(name: string) {
        return Function.prototype.toString.call(this.fn)
            .replace(/^function( \w+)?/, name ? 'function ' + name : 'function')
            .replace(/\n\/\*(``)?\*\//, '');
    }
}
