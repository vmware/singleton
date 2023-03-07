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

class CollectSourceBundle {
    constructor(logger, vipService, vipConfig, cliOptions) {
        this.logger = logger;
        this.vipService = vipService;
        this.vipConfig = vipConfig;
        this.progress = {
            total: 0,
            current: []
        };
        this.cliOptions = cliOptions;
    }
    isSourceBundle(file) {
        return file.match(/l10n\.(ts|js)$/);
    }
    collectSources(sourceSet) {
        this.logger.debug('CollectSourceBundle.collectSources sourceSet: ', sourceSet);
        return this.vipService.collectSources(sourceSet);
    }
    convertDataForVIP(data) {
        const isEncode = this.cliOptions.encode ? true : false;
        const sourceFormat = isEncode ? this.cliOptions.encode + ",STRING" : "STRING";
        var sourceSet = [];
        for (let i in data) {
            const source = isEncode ? this.encodeString(data[i]) : data[i];
            sourceSet.push({
                commentForSource: '',
                key: i,
                source,
                sourceFormat,
            });
        }
        return sourceSet;
    }
    encodeString(str) {
        var buff = Buffer.from(str);
        var res = buff.toString(this.cliOptions.encode);
        return res;
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
                    that.collectSources(data.slice(lastIndex, i)).then(function (response) {
                        if (response && response.status === 200) {
                            Array.prototype.push.apply(that.progress.current, data.slice(lastIndex, i));
                            that.logger.info(`Successfully collect strings for file `, file);
                            that.logger.info(`Successfully collect strings, index from ${lastIndex} to ${i - 1}`);
                        } else {
                            that.logger.info(`Failed collect strings file `, file);
                            that.logger.error(`Failed collect strings, index from ${lastIndex} to ${i - 1} \n`, res && res.response && res.response.message);
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
                that.collectSources(data.slice(lastIndex)).then(function (response) {
                    if (response && response.status === 200) {
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
function run() {
    const parser = new ArgumentParser({
        addHelp: true,
        description: 'Collect English source for source bundle'
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
        ['--verbose'],
        {
            help: 'show more log info',
            required: false,
            action: 'storeTrue'
        }
    );

    parser.addArgument(
        ['--encode'],
        {
            help: 'Encode the resource.',
            required: false,
        }
    );

    parser.addArgument(
        ['--moduletype'],
        {
            help: 'transform ECMAScript modules to CommonJS',
            required: false
        }
    );

    const args = parser.parseArgs();
    const logger = LogService.getLogServiceInstance(args.verbose);
    if (args.moduletype === 'ES6') {
        require('@babel/register')({
            plugins: ['@babel/plugin-transform-modules-commonjs']
        });
    }
    logger.debug('command line args', args);
    const workspace = path.resolve(process.cwd(), args.source_dir);
    logger.debug('workspace', workspace);

    try {
        let vipConfig = new VIPConfig(args.host, args.product, args.version, args.component);
        let vipService = new VIPService(vipConfig, logger, null);
        let encode = args.encode && args.encode.toUpperCase();
        if (encode && encode !== "BASE64") {
            logger.error("The encoding type '" + encode + "' is not supported.");
            return;
        }
        const cliOptions = {
            encode: encode,
        };

        let collectSourceBundle = new CollectSourceBundle(logger, vipService, vipConfig, cliOptions);
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
run();
