#!/usr/bin/env ts-node --ignore=false --compilerOptions {"module":"commonjs"}
/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

if (!process.env.DEBUG) {
    process.env.DEBUG = "app:log";
}

let fs = require('fs');
let path = require('path');
let ArgumentParser = require('argparse').ArgumentParser;
let TranslationService = require('./translation-service');
const generateToken = require('./generate-token.js');

let debug = require('debug');
let translationService: any;

var log = debug('app:log');
log.log = console.log.bind(console);

let parser = new ArgumentParser({
    addHelp: true,
    description: 'Collect English source from LocalizedComponents'
});

parser.addArgument(
    ['-s', '--source-dir'],
    {
        help: 'path to source directory where script will look for files ending in .l10n.ts',
        required: true,
    }
);

parser.addArgument(
    ['-p', '--product'],
    {
        help: 'VIP product name',
        required: true,
    }
);

parser.addArgument(
    ['-v', '--version'],
    {
        help: 'VIP product version',
        required: true,
    }
);

parser.addArgument(
    ['-c', '--component'],
    {
        help: 'VIP product component, usually AngularJS2',
        required: true,
    }
);

parser.addArgument(
    ['--host'],
    {
        help: 'VIP host',
        required: true,
    }
);

parser.addArgument(
    ['-r', '--refresh-token'],
    {
        help: 'Refresh token for CSP',
        required: false,
    }
);

let args = parser.parseArgs();

// When we import a TS file that imports Clarity Icons, we need to have
// some shimming available to avoid a crash and a failure to collect the
// English source for that TS file.
require('html-element/global-shim');
(global as any).HTMLElement = require('html-element').Element;
(document as any).cookie = "";

const walk = function (dir: string, done: Function) {
    let results: any[] = [];
    fs.readdir(dir, function (err: any, list: any) {
        if (err) {
            log(err);
            return;
        }

        let i = 0;
        (function next() {
            let file = list[i++];
            if (!file) return done(results);
            file = dir + '/' + file;
            fs.stat(file, (err: any, stat: any) => {
                if (stat && stat.isDirectory()) {
                    walk(file, (res: any) => {
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

let OUT: any = {};

function main() {
    walk(args.source_dir, (files: any[]) => {
        files.forEach(file => {
            if (file.match(/\.ts/)) {
                let component = fs.readFileSync(file, 'utf-8');
                if (component) {
                    // find l10n file
                    let l10nFile = component.match(/import(?:.*)ENGLISH(?:.*)from *["'](.*)["']/);
                    if (l10nFile) {
                        let l10nFilePathMatched = l10nFile[1];
                        let l10nFilePath = path.resolve(path.dirname(file), l10nFilePathMatched + '.ts');
                        let ENGLISH: any = {};
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
                                let e = ENGLISH[k].trim();
                                OUT[key + '.' + k] = e;
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

        function timer(ms: any) {
            return new Promise(res => setTimeout(res, ms));
        }

        let todo = Object.keys(OUT);
        let waiting: any = [];
        let failed: any = [];
        let done = 0;
        let failedCount = 0;
        let run = true;

        async function collect() {
            while (run) {
                await timer(50);

                let k: any;

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

tokenPromise.then((token: string) => {
    translationService = new TranslationService(
        args.host,
        token,
        args.product,
        args.version,
        args.component);

    main();
}, (error: any) => {
    log('Failed to get token');
    log(error);
});
