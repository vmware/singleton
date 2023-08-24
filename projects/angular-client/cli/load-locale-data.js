#!/usr/bin/env node
/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const ArgumentParser = require('argparse').ArgumentParser;
const path = require('path');
const fs = require('fs');
const { LogService } = require('./utils');
const { VIPConfig, VIPService } = require("./vip");


class LoadLocaleData {
    constructor(vipConfig, vipService, logger, locales, directory) {
        this.vipConfig = vipConfig;
        this.vipService = vipService;
        this.logger = logger;
        this.locales = this.getLocalesList(locales);
        this.directory = directory;
        this.logger.debug('Current locales are ', this.locales);
    };
    generatePackage(packagePath, data) {
        let that = this;
        this.mkDirByPath(path.dirname(packagePath), this.logger);
        fs.writeFile(packagePath, JSON.stringify(data, null, 2), 'utf-8', function (error) {
            if (error) {
                that.logger.error(`got an error when write file to ${packagePath}`, error);
                return;
            }
            that.logger.info(`Succeed write data to ${packagePath}`);
        });
    }
    mkDirByPath(absolutePath) {
        const sep = path.sep;
        let that = this;
        return absolutePath.split(sep).reduce(function (parentDir, childDir) {
            const currentDir = path.resolve(parentDir, childDir);
            try {
                fs.mkdirSync(currentDir);
            } catch (error) {
                if (error.code === 'EEXIST') {
                    that.logger.debug('directory is exist, skip', currentDir);
                    return currentDir;
                }
                if (error.code === 'ENOENT') { // Throw the original parentDir error on currentDir `ENOENT` failure.
                    throw new Error(`EACCES: permission denied, mkdir '${parentDir}'`);
                }

                const caughtErr = ['EACCES', 'EPERM', 'EISDIR'].indexOf(error.code) > -1;
                if (!caughtErr || caughtErr && currentDir === path.resolve(absolutePath)) {
                    throw error; // Throw if it's just the last created dir.
                }
            }
            return currentDir;

        }, '/');
    }
    getLocalesList(locales) {
        if (!locales || typeof locales !== 'string') {
            this.logger.error('cannot get locales due to ', locales);
            process.exit(1);
        }
        return locales.split(',');
    }
    generateLocaleDataBundles() {
        var that = this;
        var promise = new Promise(function (resolve, reject) {
            try {
                let locales = that.locales;
                for (let locale of locales) {
                    // load combine data or only translation this.vipConfig.scope
                    let translationPromise = that.vipConfig.scope
                        ? that.vipService.loadCombineData(locale)
                        : that.vipService.loadTranslation(locale);
                    translationPromise.then(function ({ body }) {
                        let { response } = body;
                        if (response.code === 200) {
                            const fileName = that.vipConfig.scope
                                ? `${locale}.json`
                                : `${that.vipConfig.TRANSLATION_PREFIX + locale}.json`
                            let translationPath = path.resolve(that.directory, fileName);
                            that.generatePackage(translationPath, body);
                        } else {
                            that.logger.error('cannot got resource due to ', response.message);
                        }
                    }).catch(function (e) {
                        that.logger.debug('loadI18nResource got an error with locale %s', locale);
                        that.logger.error('loadI18nResource got an error ', e);
                    });
                    resolve(locales);
                }
            } catch (error) {
                reject(error);
                that.logger.error('generateLocaleDataBundles got an error ', e);
            }
        });
        return promise;
    }
}
function run() {
    // get path argument
    let parser = new ArgumentParser({
        add_help: true,
        description: 'Download i18n resource files into local project'
    });

    parser.add_argument(
        ('-d', '--directory'),
        {
            help: 'The location that i18n files should be added',
            required: true
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
        ('--locales'),
        {
            help: 'The locales you want to download from vip',
            required: true,
        }
    );

    parser.add_argument(
        ('--scope'),
        {
            help: 'The pattern categories',
            required: false
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

    let args = parser.parse_args();
    const logger = LogService.getLogServiceInstance(args.verbose ? true : false);

    logger.debug('command line args', args);
    const workspace = path.resolve(process.cwd(), args.directory);
    logger.debug('workspace', workspace);
    try {
        var vipConfig = new VIPConfig(args.host, args.product, args.version, args.component, args.scope);
        var vipService = new VIPService(vipConfig, logger, null);
        var loadI18nData = new LoadLocaleData(vipConfig, vipService, logger, args.locales, args.directory);
        var locales = [];
        loadI18nData.generateLocaleDataBundles().then(function (res) {
            locales = res;
        });

        process.on('exit', function (code) {
            if (code === 0) {
                logger.info('================================================');
                logger.info('Total Locales: ', locales.length);
                logger.info('================================================');
            }
        })
    } catch (error) {
        logger.error('cannot generate package due to', error);
        process.exit(1);
    }
}
run();
