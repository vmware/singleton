#!/usr/bin/env node
/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
require('ts-node').register({
    ignore: false,
    compilerOptions: {'module':'commonjs'}
});

if (!process.env.DEBUG) {
    process.env.DEBUG = "app:log";
}

let fs = require('fs');
let path = require('path');
let ArgumentParser = require('argparse').ArgumentParser;
let TranslationService = require('./translation-service');
const generateToken = require('./generate-token.js');

let debug = require('debug');
let translationService;

var log = debug('app:log');
log.log = console.log.bind(console);

let parser = new ArgumentParser({
    add_help: true,
    description: 'Collect English source from LocalizedComponents'
});

parser.add_argument(
    ('-s', '--source-dir'),
    {
        help: 'Path to source directory where script will look for files ending in .l10n.ts',
        required: true,
    }
);

parser.add_argument(
    ('-p', '--product'),
    {
        help: 'Singleton product name',
        required: true,
    }
);

parser.add_argument(
    ('-v', '--version'),
    {
        help: 'Singleton product version',
        required: true,
    }
);

parser.add_argument(
    ('-c', '--component'),
    {
        help: 'Singleton product component, usually AngularJS2',
        required: true,
    }
);

parser.add_argument(
    ('--host'),
    {
        help: 'Singleton host',
        required: true,
    }
);

parser.add_argument(
    ('-r', '--refresh-token'),
    {
        help: 'Refresh token for CSP',
        required: false,
    }
);

let args = parser.parse_args();

// When we import a TS file that imports Clarity Icons, we need to have
// some shimming available to avoid a crash and a failure to collect the
// English source for that TS file.
require('html-element/global-shim');
global.HTMLElement = require('html-element').Element;
document.cookie = "";

const walk = function (dir, done) {
    let results = [];
    fs.readdir(dir, function (err, list) {
        if (err) {
            log(err);
            return;
        }

        let i = 0;
        (function next() {
            let file = list[i++];
            if (!file) return done(results);
            file = dir + '/' + file;
            fs.stat(file, (err, stat) => {
                if (stat && stat.isDirectory()) {
                    walk(file, (res) => {
                        results = results.concat(res);
                        next();
                    });
                } else if (err) {
                    log(err);
                } else {
                    results.push(file);
                    next();
                }
            });
        })();
    });
};

let OUT = {};
let KEYINFO = {};

function main() {
    walk(args.source_dir, (files) => {
        files.forEach(file => {
            if (file.match(/\.ts/)) {
                let component = fs.readFileSync(file, 'utf-8');
                if (component) {
                    // find l10n file
                    let l10nFile = component.match(/import(?:.*)ENGLISH(?:.*)from *["'](.*)["']/);
                    if (l10nFile) {
                        let l10nFilePathMatched = l10nFile[1];
                        let l10nFilePath = path.resolve(path.dirname(file), l10nFilePathMatched + '.ts');
                        let ENGLISH = {};
                        try {
                            ENGLISH = require(l10nFilePath.substring(0, l10nFilePath.length - 3)).ENGLISH;
                        } catch (e) {
                            log('failed to import ' + l10nFilePath, e);
                            return;
                        }
                        // find l10n key
                        let componentL10nKey = component.match(/L10nKey: ['"](.*)['"]/);
                        if (componentL10nKey) {
                            let key = componentL10nKey[1];
                            Object.keys(ENGLISH).forEach(k => {
                                if (OUT[key + '.' + k]) {
                                    log(`-----------------------Detected Duplicate Keys-------------------------`);

                                    log(`KEY        :    '${key + '.' + k}'`);
                                    log(`PREFIX     :    '${key}'`);
                                    log(`SUFFIX-KEY :    '${k}'`);
                                    log(`VALUE      :    '${ENGLISH[k]}'`);
                                    log(`PATH       :    '${l10nFilePath}'`);

                                    log(`---------Below Duplicate KEY:VALUE will replace above KEY:VALUE--------`);
                                    log(`DUP KEY    :    '${key + '.' + k}'`);
                                    log(`PREFIX     :    '${KEYINFO[key + '.' + k][0]}'`);
                                    log(`SUFFIX-KEY :    '${KEYINFO[key + '.' + k][1]}'`);
                                    log(`VALUE      :    '${KEYINFO[key + '.' + k][2]}'`);
                                    log(`PATH       :    '${KEYINFO[key + '.' + k][3]}'`);

                                    log(`--------------------------------End-----------------------------------`);
                                }
                                let e = ENGLISH[k].trim();
                                OUT[key + '.' + k] = e;
                                KEYINFO[key + '.' + k] = [key, k, ENGLISH[k], l10nFilePath];
                            });
                        } else {
                            log('no key for ' + file);
                        }

                    } else {
                        log('no l10n file for ' + file);
                    }
                } else {
                    log('no component for ' + file);
                }
            }
        });

        function timer(ms) {
            return new Promise(res => setTimeout(res, ms));
        }

        let todo = Object.keys(OUT);
        let waiting = [];
        let failed = [];
        let done = 0;
        let failedCount = 0;
        let run = true;

        async function collect() {
            while (run) {
                await timer(50);

                let k;

                if (todo.length) {
                    k = todo.pop();
                } else if (failed.length) {
                    k = failed.pop();
                }

                run = todo.length !== 0 || failed.length > 0 || waiting.length > 0;

                if (!k) {
                    continue;
                }

                log(`collecting source for ${k}`);

                waiting.push(k);

                translationService.collectSource(k, OUT[k]).then(() => {
                    log(`successfully collected source for ${k}`);
                    waiting.pop();
                    done++;
                }, () => {
                    log(`failed to collect source for ${k}`);
                    failed.push(k);
                    failedCount++;
                    waiting.pop();
                });

                log(`Remaining: ${todo.length}, Failed: ${failed.length}, Pending: ${waiting.length}`);
            }

            log(`Collected ${done} strings, failed attempts: ${failedCount}`);
        };

        collect();
    });
}

let tokenPromise = Promise.resolve('');

if (args.refresh_token) {
    tokenPromise = generateToken(args.host, args.refresh_token);
}

tokenPromise.then((token) => { translationService = new TranslationService(
        args.host,
        token,
        args.product,
        args.version,
        args.component);

    main();
}, (error) => {
    log('Failed to get token');
    log(error);
});
