#!/usr/bin/env node
/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const superagent = require('superagent');
const ArgumentParser = require('argparse').ArgumentParser;
const path = require('path');
const fs = require('fs');
const { LogService } = require('./utils');
const { VIPConfig, VIPService } = require("./vip");


class LoadI18nPackage {
    constructor(vipConfig, vipService, logger, languages, directory) {
        this.vipConfig = vipConfig;
        this.vipService = vipService;
        this.logger = logger;
        this.languages = this.getLanguagesList(languages);
        this.directory = directory;
        this.logger.debug('Current languages are ', this.languages);
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
    getLanguagesList(languages) {
        if (!languages || typeof languages !== 'string') {
            this.logger.error('cannot get languages due to ', languages);
            process.exit(1);
        }
        return languages.split(',');
    }
    generateTranslationBundles() {
        var that = this;
        var promise = new Promise(function (resolve, reject) {
            try {
                let languages = that.languages;
                for (let lang of languages) {
                    // load translations
                    let translationPromise = that.vipService.loadTranslation(lang);
                    translationPromise.then(function ({ body }) {
                        let { response } = body;
                        if (response.code === 200) {
                            let translationPath = path.resolve(that.directory, `${that.vipConfig.TRANSLATION_PREFIX + lang}.json`);
                            that.generatePackage(translationPath, body);
                        } else {
                            that.logger.error('cannot got translation due to ', response.message);
                        }
                    }).catch(function (e) {
                        that.logger.debug('loadTranslation got an error with lang %s', lang);
                        that.logger.error('loadTranslation got an error ', e);
                    });
                    resolve(languages);
                }
            } catch (error) {
                reject(error);
                that.logger.error('generateTranslationBundles got an error ', e);
            }
        });
        return promise;
    }
}
function run() {
    // get path argument
    let parser = new ArgumentParser({
        add_help: true,
        description: 'Download i18n files into local project'
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
        ('--languages'),
        {
            help: 'The languages you want to download from vip',
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
    )

    let args = parser.parse_args();
    const logger = LogService.getLogServiceInstance(args.verbose ? true : false);

    logger.debug('command line args', args);
    const workspace = path.resolve(process.cwd(), args.directory);
    logger.debug('workspace', workspace);
    try {
        var vipConfig = new VIPConfig(args.host, args.product, args.version, args.component, args.languages);
        var vipService = new VIPService(vipConfig, logger, null);
        var loadI18nPackage = new LoadI18nPackage(vipConfig, vipService, logger, args.languages, args.directory);
        var languages = [];
        loadI18nPackage.generateTranslationBundles().then(function (lang) {
            languages = lang;
        });

        process.on('exit', function (code) {
            if (code === 0) {
                logger.info('================================================');
                logger.info('Total Languages: ', languages.length);
                logger.info('================================================');
            }
        })
    } catch (error) {
        logger.error('cannot generate package due to', error);
        process.exit(1);
    }
}
run();


