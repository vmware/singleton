#!/usr/bin/env node
/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const { LogService, walkDirectory } = require('./utils');
const { VIPConfig, VIPService } = require("./vip");
const fs = require('fs');
const path = require('path');
const ts = require('typescript');
const ArgumentParser = require('argparse').ArgumentParser;
const generateToken = require('./generate-token.js');

class CollectSourceBundle {
    constructor(logger, vipService, vipConfig) {
        this.logger = logger;
        this.vipService = vipService;
        this.vipConfig = vipConfig;
        this.progress = {
            total: 0,
            current: []
        };
    }
    isSourceBundle(file) {
        return file.match(/l10n\.(ts|js)$/);
    }
    collectSources(sourceSet) {
        this.logger.debug('CollectSourceBundle.collectSources sourceSet: ', sourceSet);
        return this.vipService.collectSources(sourceSet);
    }
    convertDataForVIP(data) {
        var sourceSet = [];
        for (let i in data) {
            if (data[i]) {
                let sourceString = data[i], comment = '';
                // If the corresponding value of key is an array containing source and comment.
                if (Array.isArray(data[i])) {
                    if (!data[i][0]) { continue; }
                    sourceString = data[i][0];
                    comment = data[i][1] ? data[i][1] : ''
                }
                sourceSet.push({
                    commentForSource: comment,
                    key: i,
                    source: sourceString
                });
            }
        }
        return sourceSet;
    }
    throttleCollectSources(dataObject, stepSize, file) {
        var data = this.convertDataForVIP(dataObject);
        if (data.length === 0) {
            return;
        }
        var lastIndex = 0;
        this.progress.total = this.progress.total ? this.progress.total + data.length : data.length;
        for (let i = 1; i < data.length; i++) {
            if (i % stepSize === 0) {
                (function (lastIndex, i, that) {
                    that.collectSources(data.slice(lastIndex, i)).then(function ({ body }) {
                        if (body && body.response && body.response.code === 200) {
                            Array.prototype.push.apply(that.progress.current, data.slice(lastIndex, i));
                            that.logger.info(`Successfully collect strings for file `, file);
                            that.logger.info(`Successfully collect strings, index from ${lastIndex} to ${i - 1}`);
                        } else {
                            that.logger.info(`Failed collect strings file `, file);
                            that.logger.error(`Failed collect strings, index from ${lastIndex} to ${i - 1}`);
+                           that.logger.error(body);
                        }
                    }, function (err) {
                        that.logger.info(`Failed collect strings file `, file);
                        that.logger.error(`Failed collect strings, index from ${lastIndex} to ${i - 1}`);
                        that.logger.error(err);
                    });
                })(lastIndex, i, this);
                lastIndex = i;
            }
        }
        if (lastIndex < data.length) {
            (function (lastIndex, that) {
                that.collectSources(data.slice(lastIndex)).then(function ({ body }) {
                    if (body && body.response && body.response.code === 200) {
                        Array.prototype.push.apply(that.progress.current, data.slice(lastIndex));
                        that.logger.info(`Successfully collect strings for file `, file);
                        that.logger.info(`Successfully collect strings, index from ${lastIndex} to the end`);
                    } else {
                        that.logger.info(`Failed collect strings file `, file);
                        that.logger.error(`Failed collect strings, index from ${lastIndex} to the end`);
                    }

                }, function () {
                    that.logger.info(`Failed collect strings file `, file);
                    that.logger.error(`Failed collect strings, index from ${lastIndex} to the end`);
                });
            })(lastIndex, this);
        }
    }
}

const parser = new ArgumentParser({
    add_help: true,
    description: 'Collect English source for source bundle'
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
    ('--verbose'),
    {
        help: 'show more log info',
        required: false,
        action: 'store_true'
    }
);
parser.add_argument(
    ('-r', '--refresh-token'),
    {
        help: 'Refresh token for CSP',
        required: false,
    }
);
const args = parser.parse_args();
let vipConfig = new VIPConfig(args.host, args.product, args.version, args.component);
const logger = LogService.getLogServiceInstance(args.verbose);
let vipService = new VIPService(vipConfig, logger, null);

function run() {
    logger.debug('command line args', args);
    const workspace = path.resolve(process.cwd(), args.source_dir);
    logger.debug('workspace', workspace);

    try {
        let vipConfig = new VIPConfig(args.host, args.product, args.version, args.component);
        let collectSourceBundle = new CollectSourceBundle(logger, vipService, vipConfig);
        walkDirectory(args.source_dir, function (files) {
            logger.debug('walkDirectory resolve files', files);
            files.forEach(function (file) {
                logger.debug('walkDirectory current file', file);
                if (collectSourceBundle.isSourceBundle(file)) {
                    try {
                        let ENGLISH = {};
                        let l10n = fs.readFileSync(file, 'utf-8');
                        let result = ts.transpileModule(l10n, {
                            compilerOptions: {
                                "module": ts.ModuleKind.CommonJS,
                                "noImplicitUseStrict": true
                            }
                        });
                        logger.debug('typescript outputText', result.outputText);
                        var newModule = new module.constructor();
                        newModule._compile(result.outputText, '');

                        ENGLISH = newModule.exports.ENGLISH;
                        logger.debug(`the ENGLISH of the ${file}`, ENGLISH);
                        collectSourceBundle.throttleCollectSources(ENGLISH, 1000, file);
                    } catch (error) {
                        logger.error('failed to import ' + file, error);
                    }
                }
            });
        });
        process.on('exit', function (code) {
            if (code === 0) {
                logger.info('================================================');
                logger.info('Total Collect Source Strings: ', collectSourceBundle.progress.current.length);
                logger.info('================================================');
            }
        })
    } catch (error) {
        logger.error('cannot collect source bundle due to', error);
        process.exit(1);
    }
}

let tokenPromise = Promise.resolve('');
if (args.refresh_token) {
    tokenPromise = generateToken(args.host, args.refresh_token);
}

tokenPromise.then((token) => {
    vipService = new VIPService(
        vipConfig,
        logger,
        token);
    run();
}, (error) => {
    logger.error('Failed to get token');
    logger.error(error);
});