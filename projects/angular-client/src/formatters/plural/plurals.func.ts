/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const C = [
    function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return 'other';
    },
    function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one' : 'other';
    },
    function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return ((n === 0
            || n === 1)) ? 'one' : 'other';
    },
    function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1];
        if (ord) { return 'other'; }
        return (n === 1 && v0) ? 'one' : 'other';
    }
];

export const PLURALFUNCS: { [key: string]: Function} = {
    af: C[1],

    ak: C[2],

    am: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    ar: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return 'other'; }
        return (n === 0) ? 'zero'
            : (n === 1) ? 'one'
                : (n === 2) ? 'two'
                    : ((n100 >= 3 && n100 <= 10)) ? 'few'
                        : ((n100 >= 11 && n100 <= 99)) ? 'many'
                            : 'other';
    },

    ars: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n100 = t0 && +s[0].slice(-2);
        if (ord) { return 'other'; }
        return (n === 0) ? 'zero'
            : (n === 1) ? 'one'
                : (n === 2) ? 'two'
                    : ((n100 >= 3 && n100 <= 10)) ? 'few'
                        : ((n100 >= 11 && n100 <= 99)) ? 'many'
                            : 'other';
    },

    as: function (n: number, ord: boolean) {
        if (ord) { return ((n === 1 || n === 5 || n === 7 || n === 8 || n === 9
            || n === 10)) ? 'one'
            : ((n === 2
                || n === 3)) ? 'two'
                : (n === 4) ? 'few'
                    : (n === 6) ? 'many'
                        : 'other';
        }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    asa: C[1],

    ast: C[3],

    az: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2)), i1000 = Number(i.slice(-3));
        if (ord) { return ((i10 === 1 || i10 === 2 || i10 === 5 || i10 === 7 || i10 === 8) || (i100 === 20 || i100 === 50
            || i100 === 70
            || i100 === 80)) ? 'one'
            : ((i10 === 3 || i10 === 4) || (i1000 === 100 || i1000 === 200 || i1000 === 300 || i1000 === 400 || i1000 === 500
                || i1000 === 600 || i1000 === 700 || i1000 === 800
                || i1000 === 900)) ? 'few'
                : (+Number(i) === 0 || i10 === 6 || (i100 === 40 || i100 === 60
                    || i100 === 90)) ? 'many'
                    : 'other';
        }
        return (n === 1) ? 'one' : 'other';
    },

    be: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)), n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return ((n10 === 2
            || n10 === 3) && n100 !== 12 && n100 !== 13) ? 'few' : 'other';
        }
        return (n10 === 1 && n100 !== 11) ? 'one'
            : ((n10 >= 2 && n10 <= 4) && (n100 < 12
                || n100 > 14)) ? 'few'
                : (t0 && n10 === 0 || (n10 >= 5 && n10 <= 9)
                    || (n100 >= 11 && n100 <= 14)) ? 'many'
                    : 'other';
    },

    bem: C[1],

    bez: C[1],

    bg: C[1],

    bh: C[2],

    bm: C[0],

    bn: function (n: number, ord: boolean) {
        if (ord) { return ((n === 1 || n === 5 || n === 7 || n === 8 || n === 9
            || n === 10)) ? 'one'
            : ((n === 2
                || n === 3)) ? 'two'
                : (n === 4) ? 'few'
                    : (n === 6) ? 'many'
                        : 'other';
        }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    bo: C[0],

    br: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)), n100 = t0 && Number(s[0].slice(-2)),
            n1000000 = t0 && Number(s[0].slice(-6));
        if (ord) { return 'other'; }
        return (n10 === 1 && n100 !== 11 && n100 !== 71 && n100 !== 91) ? 'one'
            : (n10 === 2 && n100 !== 12 && n100 !== 72 && n100 !== 92) ? 'two'
                : (((n10 === 3 || n10 === 4) || n10 === 9) && (n100 < 10 || n100 > 19) && (n100 < 70 || n100 > 79) && (n100 < 90
                    || n100 > 99)) ? 'few'
                    : (n !== 0 && t0 && n1000000 === 0) ? 'many'
                        : 'other';
    },

    brx: C[1],

    bs: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2)),
            f10 = Number(f.slice(-1)), f100 = Number(f.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i10 === 1 && i100 !== 11
            || f10 === 1 && f100 !== 11) ? 'one'
            : (v0 && (i10 >= 2 && i10 <= 4) && (i100 < 12 || i100 > 14) || (f10 >= 2 && f10 <= 4) && (f100 < 12
                || f100 > 14)) ? 'few'
                : 'other';
    },

    ca: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1];
        if (ord) { return ((n === 1
            || n === 3)) ? 'one'
            : (n === 2) ? 'two'
                : (n === 4) ? 'few'
                    : 'other';
        }
        return (n === 1 && v0) ? 'one' : 'other';
    },

    ce: C[1],

    cgg: C[1],

    chr: C[1],

    ckb: C[1],

    cs: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1];
        if (ord) { return 'other'; }
        return (n === 1 && v0) ? 'one'
            : ((Number(i) >= 2 && Number(i) <= 4) && v0) ? 'few'
                : (!v0) ? 'many'
                    : 'other';
    },

    cy: function (n: number, ord: boolean) {
        if (ord) { return ((n === 0 || n === 7 || n === 8
            || n === 9)) ? 'zero'
            : (n === 1) ? 'one'
                : (n === 2) ? 'two'
                    : ((n === 3
                        || n === 4)) ? 'few'
                        : ((n === 5
                            || n === 6)) ? 'many'
                            : 'other';
        }
        return (n === 0) ? 'zero'
            : (n === 1) ? 'one'
                : (n === 2) ? 'two'
                    : (n === 3) ? 'few'
                        : (n === 6) ? 'many'
                            : 'other';
    },

    da: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], t0 = Number(s[0]) === n;
        if (ord) { return 'other'; }
        return (n === 1 || !t0 && (Number(i) === 0
            || Number(i) === 1)) ? 'one' : 'other';
    },

    de: C[3],

    dsb: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i100 = Number(i.slice(-2)), f100 = Number(f.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i100 === 1
            || f100 === 1) ? 'one'
            : (v0 && i100 === 2
                || f100 === 2) ? 'two'
                : (v0 && (i100 === 3 || i100 === 4) || (f100 === 3
                    || f100 === 4)) ? 'few'
                    : 'other';
    },

    dv: C[1],

    dz: C[0],

    ee: C[1],

    el: C[1],

    en: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1], t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)),
            n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return (n10 === 1 && n100 !== 11) ? 'one'
            : (n10 === 2 && n100 !== 12) ? 'two'
                : (n10 === 3 && n100 !== 13) ? 'few'
                    : 'other';
        }
        return (n === 1 && v0) ? 'one' : 'other';
    },

    eo: C[1],

    es: C[1],

    et: C[3],

    eu: C[1],

    fa: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    ff: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n >= 0 && n < 2) ? 'one' : 'other';
    },

    fi: C[3],

    fil: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i10 = Number( i.slice(-1) ), f10 = Number(f.slice(-1));
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return (v0 && (Number(i) === 1 || Number(i) === 2 || Number(i) === 3) || v0 && i10 !== 4 && i10 !== 6 && i10 !== 9
            || !v0 && f10 !== 4 && f10 !== 6 && f10 !== 9) ? 'one' : 'other';
    },

    fo: C[1],

    fr: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return (n >= 0 && n < 2) ? 'one' : 'other';
    },

    fur: C[1],

    fy: C[3],

    ga: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n;
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : ((t0 && n >= 3 && n <= 6)) ? 'few'
                    : ((t0 && n >= 7 && n <= 10)) ? 'many'
                        : 'other';
    },

    gd: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n;
        if (ord) { return ((n === 1
            || n === 11)) ? 'one'
            : ((n === 2
                || n === 12)) ? 'two'
                : ((n === 3
                    || n === 13)) ? 'few'
                    : 'other';
        }
        return ((n === 1
            || n === 11)) ? 'one'
            : ((n === 2
                || n === 12)) ? 'two'
                : (((t0 && n >= 3 && n <= 10)
                    || (t0 && n >= 13 && n <= 19))) ? 'few'
                    : 'other';
    },

    gl: C[3],

    gsw: C[1],

    gu: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one'
            : ((n === 2
                || n === 3)) ? 'two'
                : (n === 4) ? 'few'
                    : (n === 6) ? 'many'
                        : 'other';
        }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    guw: C[2],

    gv: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i10 === 1) ? 'one'
            : (v0 && i10 === 2) ? 'two'
                : (v0 && (i100 === 0 || i100 === 20 || i100 === 40 || i100 === 60
                    || i100 === 80)) ? 'few'
                    : (!v0) ? 'many'
                        : 'other';
    },

    ha: C[1],

    haw: C[1],

    he: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1], t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1));
        if (ord) { return 'other'; }
        return (n === 1 && v0) ? 'one'
            : (Number(i) === 2 && v0) ? 'two'
                : (v0 && (n < 0
                    || n > 10) && t0 && n10 === 0) ? 'many'
                    : 'other';
    },

    hi: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one'
            : ((n === 2
                || n === 3)) ? 'two'
                : (n === 4) ? 'few'
                    : (n === 6) ? 'many'
                        : 'other';
        }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    hr: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2)),
            f10 = Number(f.slice(-1)), f100 = Number(f.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i10 === 1 && i100 !== 11
            || f10 === 1 && f100 !== 11) ? 'one'
            : (v0 && (i10 >= 2 && i10 <= 4) && (i100 < 12 || i100 > 14) || (f10 >= 2 && f10 <= 4) && (f100 < 12
                || f100 > 14)) ? 'few'
                : 'other';
    },

    hsb: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i100 = Number(i.slice(-2)), f100 = Number(f.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i100 === 1
            || f100 === 1) ? 'one'
            : (v0 && i100 === 2
                || f100 === 2) ? 'two'
                : (v0 && (i100 === 3 || i100 === 4) || (f100 === 3
                    || f100 === 4)) ? 'few'
                    : 'other';
    },

    hu: function (n: number, ord: boolean) {
        if (ord) { return ((n === 1
            || n === 5)) ? 'one' : 'other';
        }
        return (n === 1) ? 'one' : 'other';
    },

    hy: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return (n >= 0 && n < 2) ? 'one' : 'other';
    },

    ia: C[3],

    id: C[0],

    ig: C[0],

    ii: C[0],

    'in': C[0],

    io: C[3],

    is: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], t0 = Number(s[0]) === n, i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2));
        if (ord) { return 'other'; }
        return (t0 && i10 === 1 && i100 !== 11
            || !t0) ? 'one' : 'other';
    },

    it: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1];
        if (ord) { return ((n === 11 || n === 8 || n === 80
            || n === 800)) ? 'many' : 'other';
        }
        return (n === 1 && v0) ? 'one' : 'other';
    },

    iu: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    iw: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1], t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1));
        if (ord) { return 'other'; }
        return (n === 1 && v0) ? 'one'
            : (Number(i) === 2 && v0) ? 'two'
                : (v0 && (n < 0
                    || n > 10) && t0 && n10 === 0) ? 'many'
                    : 'other';
    },

    ja: C[0],

    jbo: C[0],

    jgo: C[1],

    ji: C[3],

    jmc: C[1],

    jv: C[0],

    jw: C[0],

    ka: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], i100 = Number(i.slice(-2));
        if (ord) { return (Number(i) === 1) ? 'one'
            : (Number(i) === 0 || ((i100 >= 2 && i100 <= 20) || i100 === 40 || i100 === 60
                || i100 === 80)) ? 'many'
                : 'other';
        }
        return (n === 1) ? 'one' : 'other';
    },

    kab: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n >= 0 && n < 2) ? 'one' : 'other';
    },

    kaj: C[1],

    kcg: C[1],

    kde: C[0],

    kea: C[0],

    kk: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1));
        if (ord) { return (n10 === 6 || n10 === 9
            || t0 && n10 === 0 && n !== 0) ? 'many' : 'other';
        }
        return (n === 1) ? 'one' : 'other';
    },

    kkj: C[1],

    kl: C[1],

    km: C[0],

    kn: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    ko: C[0],

    ks: C[1],

    ksb: C[1],

    ksh: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 0) ? 'zero'
            : (n === 1) ? 'one'
                : 'other';
    },

    ku: C[1],

    kw: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    ky: C[1],

    lag: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0];
        if (ord) { return 'other'; }
        return (n === 0) ? 'zero'
            : ((Number(i) === 0
                || Number(i) === 1) && n !== 0) ? 'one'
                : 'other';
    },

    lb: C[1],

    lg: C[1],

    lkt: C[0],

    ln: C[2],

    lo: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return 'other';
    },

    lt: function (n: number, ord: boolean) {
        const s = String(n).split('.'), f = s[1] || '', t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)),
            n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return 'other'; }
        return (n10 === 1 && (n100 < 11
            || n100 > 19)) ? 'one'
            : ((n10 >= 2 && n10 <= 9) && (n100 < 11
                || n100 > 19)) ? 'few'
                : (Number(f) !== 0) ? 'many'
                    : 'other';
    },

    lv: function (n: number, ord: boolean) {
        const s = String(n).split('.'), f = s[1] || '', v = f.length, t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)),
            n100 = t0 && Number(s[0].slice(-2)), f100 = Number(f.slice(-2)), f10 = Number(f.slice(-1));
        if (ord) { return 'other'; }
        return (t0 && n10 === 0 || (n100 >= 11 && n100 <= 19)
            || v === 2 && (f100 >= 11 && f100 <= 19)) ? 'zero'
            : (n10 === 1 && n100 !== 11 || v === 2 && f10 === 1 && f100 !== 11
                || v !== 2 && f10 === 1) ? 'one'
                : 'other';
    },

    mas: C[1],

    mg: C[2],

    mgo: C[1],

    mk: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2)),
            f10 = Number(f.slice(-1)), f100 = Number(f.slice(-2));
        if (ord) { return (i10 === 1 && i100 !== 11) ? 'one'
            : (i10 === 2 && i100 !== 12) ? 'two'
                : ((i10 === 7
                    || i10 === 8) && i100 !== 17 && i100 !== 18) ? 'many'
                    : 'other';
        }
        return (v0 && i10 === 1 && i100 !== 11
            || f10 === 1 && f100 !== 11) ? 'one' : 'other';
    },

    ml: C[1],

    mn: C[1],

    mo: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1], t0 = Number(s[0]) === n, n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return (n === 1 && v0) ? 'one'
            : (!v0 || n === 0
                || n !== 1 && (n100 >= 1 && n100 <= 19)) ? 'few'
                : 'other';
    },

    mr: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one'
            : ((n === 2
                || n === 3)) ? 'two'
                : (n === 4) ? 'few'
                    : 'other';
        }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    },

    ms: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return 'other';
    },

    mt: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 0
                || (n100 >= 2 && n100 <= 10)) ? 'few'
                : ((n100 >= 11 && n100 <= 19)) ? 'many'
                    : 'other';
    },

    my: C[0],

    nah: C[1],

    naq: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    nb: C[1],

    nd: C[1],

    ne: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n;
        if (ord) { return ((t0 && n >= 1 && n <= 4)) ? 'one' : 'other'; }
        return (n === 1) ? 'one' : 'other';
    },

    nl: C[3],

    nn: C[1],

    nnh: C[1],

    no: C[1],

    nqo: C[0],

    nr: C[1],

    nso: C[2],

    ny: C[1],

    nyn: C[1],

    om: C[1],

    or: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n;
        if (ord) { return ((n === 1 || n === 5
            || (t0 && n >= 7 && n <= 9))) ? 'one'
            : ((n === 2
                || n === 3)) ? 'two'
                : (n === 4) ? 'few'
                    : (n === 6) ? 'many'
                        : 'other';
        }
        return (n === 1) ? 'one' : 'other';
    },

    os: C[1],

    pa: C[2],

    pap: C[1],

    pl: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2));
        if (ord) { return 'other'; }
        return (n === 1 && v0) ? 'one'
            : (v0 && (i10 >= 2 && i10 <= 4) && (i100 < 12
                || i100 > 14)) ? 'few'
                : (v0 && Number(i) !== 1 && (i10 === 0 || i10 === 1) || v0 && (i10 >= 5 && i10 <= 9)
                    || v0 && (i100 >= 12 && i100 <= 14)) ? 'many'
                    : 'other';
    },

    prg: function (n: number, ord: boolean) {
        const s = String(n).split('.'), f = s[1] || '', v = f.length, t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)),
            n100 = t0 && Number(s[0].slice(-2)), f100 = Number(f.slice(-2)), f10 = Number(f.slice(-1));
        if (ord) { return 'other'; }
        return (t0 && n10 === 0 || (n100 >= 11 && n100 <= 19)
            || v === 2 && (f100 >= 11 && f100 <= 19)) ? 'zero'
            : (n10 === 1 && n100 !== 11 || v === 2 && f10 === 1 && f100 !== 11
                || v !== 2 && f10 === 1) ? 'one'
                : 'other';
    },

    ps: C[1],

    pt: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0];
        if (ord) { return 'other'; }
        return ((Number(i) === 0
            || Number(i) === 1)) ? 'one' : 'other';
    },

    'pt-PT': C[3],

    rm: C[1],

    ro: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1], t0 = Number(s[0]) === n, n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return (n === 1 && v0) ? 'one'
            : (!v0 || n === 0
                || n !== 1 && (n100 >= 1 && n100 <= 19)) ? 'few'
                : 'other';
    },

    rof: C[1],

    root: C[0],

    ru: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i10 === 1 && i100 !== 11) ? 'one'
            : (v0 && (i10 >= 2 && i10 <= 4) && (i100 < 12
                || i100 > 14)) ? 'few'
                : (v0 && i10 === 0 || v0 && (i10 >= 5 && i10 <= 9)
                    || v0 && (i100 >= 11 && i100 <= 14)) ? 'many'
                    : 'other';
    },

    rwk: C[1],

    sah: C[0],

    saq: C[1],

    sc: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1];
        if (ord) { return ((n === 11 || n === 8 || n === 80
            || n === 800)) ? 'many' : 'other';
        }
        return (n === 1 && v0) ? 'one' : 'other';
    },

    scn: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1];
        if (ord) { return ((n === 11 || n === 8 || n === 80
            || n === 800)) ? 'many' : 'other';
        }
        return (n === 1 && v0) ? 'one' : 'other';
    },

    sd: C[1],

    sdh: C[1],

    se: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    seh: C[1],

    ses: C[0],

    sg: C[0],

    sh: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2)),
            f10 = Number(f.slice(-1)), f100 = Number(f.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i10 === 1 && i100 !== 11
            || f10 === 1 && f100 !== 11) ? 'one'
            : (v0 && (i10 >= 2 && i10 <= 4) && (i100 < 12 || i100 > 14) || (f10 >= 2 && f10 <= 4) && (f100 < 12
                || f100 > 14)) ? 'few'
                : 'other';
    },

    shi: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n;
        if (ord) { return 'other'; }
        return (n >= 0 && n <= 1) ? 'one'
            : ((t0 && n >= 2 && n <= 10)) ? 'few'
                : 'other';
    },

    si: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '';
        if (ord) { return 'other'; }
        return ((n === 0 || n === 1)
            || Number(i) === 0 && Number(f) === 1) ? 'one' : 'other';
    },

    sk: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1];
        if (ord) { return 'other'; }
        return (n === 1 && v0) ? 'one'
            : ((Number(i) >= 2 && Number(i) <= 4) && v0) ? 'few'
                : (!v0) ? 'many'
                    : 'other';
    },

    sl: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1], i100 = Number(i.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i100 === 1) ? 'one'
            : (v0 && i100 === 2) ? 'two'
                : (v0 && (i100 === 3 || i100 === 4)
                    || !v0) ? 'few'
                    : 'other';
    },

    sma: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    smi: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    smj: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    smn: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    sms: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n === 1) ? 'one'
            : (n === 2) ? 'two'
                : 'other';
    },

    sn: C[1],

    so: C[1],

    sq: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)), n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return (n === 1) ? 'one'
            : (n10 === 4 && n100 !== 14) ? 'many'
                : 'other';
        }
        return (n === 1) ? 'one' : 'other';
    },

    sr: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2)),
            f10 = Number(f.slice(-1)), f100 = Number(f.slice(-2));
        if (ord) { return 'other'; }
        return (v0 && i10 === 1 && i100 !== 11
            || f10 === 1 && f100 !== 11) ? 'one'
            : (v0 && (i10 >= 2 && i10 <= 4) && (i100 < 12 || i100 > 14) || (f10 >= 2 && f10 <= 4) && (f100 < 12
                || f100 > 14)) ? 'few'
                : 'other';
    },

    ss: C[1],

    ssy: C[1],

    st: C[1],

    sv: function (n: number, ord: boolean) {
        const s = String(n).split('.'), v0 = !s[1], t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)),
            n100 = t0 && Number(s[0].slice(-2));
        if (ord) { return ((n10 === 1
            || n10 === 2) && n100 !== 11 && n100 !== 12) ? 'one' : 'other';
        }
        return (n === 1 && v0) ? 'one' : 'other';
    },

    sw: C[3],

    syr: C[1],

    ta: C[1],

    te: C[1],

    teo: C[1],

    th: C[0],

    ti: C[2],

    tig: C[1],

    tk: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1));
        if (ord) { return ((n10 === 6 || n10 === 9)
            || n === 10) ? 'few' : 'other';
        }
        return (n === 1) ? 'one' : 'other';
    },

    tl: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], f = s[1] || '', v0 = !s[1], i10 = Number( i.slice(-1) ), f10 = Number(f.slice(-1));
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return (v0 && (Number(i) === 1 || Number(i) === 2 || Number(i) === 3) || v0 && i10 !== 4 && i10 !== 6 && i10 !== 9
            || !v0 && f10 !== 4 && f10 !== 6 && f10 !== 9) ? 'one' : 'other';
    },

    tn: C[1],

    to: C[0],

    tr: C[1],

    ts: C[1],

    tzm: function (n: number, ord: boolean) {
        const s = String(n).split('.'), t0 = Number(s[0]) === n;
        if (ord) { return 'other'; }
        return ((n === 0 || n === 1)
            || (t0 && n >= 11 && n <= 99)) ? 'one' : 'other';
    },

    ug: C[1],

    uk: function (n: number, ord: boolean) {
        const s = String(n).split('.'), i = s[0], v0 = !s[1], t0 = Number(s[0]) === n, n10 = t0 && Number(s[0].slice(-1)),
            n100 = t0 && Number(s[0].slice(-2)), i10 = Number( i.slice(-1) ), i100 = Number(i.slice(-2));
        if (ord) { return (n10 === 3 && n100 !== 13) ? 'few' : 'other'; }
        return (v0 && i10 === 1 && i100 !== 11) ? 'one'
            : (v0 && (i10 >= 2 && i10 <= 4) && (i100 < 12
                || i100 > 14)) ? 'few'
                : (v0 && i10 === 0 || v0 && (i10 >= 5 && i10 <= 9)
                    || v0 && (i100 >= 11 && i100 <= 14)) ? 'many'
                    : 'other';
    },

    ur: C[3],

    uz: C[1],

    ve: C[1],

    vi: function (n: number, ord: boolean) {
        if (ord) { return (n === 1) ? 'one' : 'other'; }
        return 'other';
    },

    vo: C[1],

    vun: C[1],

    wa: C[2],

    wae: C[1],

    wo: C[0],

    xh: C[1],

    xog: C[1],

    yi: C[3],

    yo: C[0],

    yue: C[0],

    zh: C[0],

    zu: function (n: number, ord: boolean) {
        if (ord) { return 'other'; }
        return (n >= 0 && n <= 1) ? 'one' : 'other';
    }
};
