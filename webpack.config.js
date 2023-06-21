/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const helpers = require('./config/helpers');

const webConfig = {
mode: 'production',

resolve: {
    extensions: ['.ts', '.js']
},

entry: helpers.root('index.ts'),

output: {
    path: helpers.root('bundles'),
    publicPath: '/',
    filename: 'singleton.core.umd.js',
    library: 'singletoncore',
    libraryTarget: 'umd',
    globalObject: `typeof self !== 'undefined' ? self : this`
},



module: {
    rules: [{
        enforce: 'pre',
        test: /\.ts$/,
        loader: 'tslint-loader',
        exclude: [helpers.root('node_modules')]
    }, {
        test: /\.ts$/,
        loader: 'awesome-typescript-loader',
        options: {
            declaration: false
        },
        exclude: [/\.spec\.ts$/]
    }]
},
};

const nodeConfig = {
    mode: 'production',
    target: 'node',
    
    resolve: {
        extensions: ['.ts', '.js']
    },
    
    entry: helpers.root('index.ts'),
    
    output: {
        path: helpers.root('bundles'),
        publicPath: '/',
        filename: 'singleton.core.umd.server.js',
        library: 'singletoncore',
        libraryTarget: 'umd'
    },
    
    module: {
        rules: [{
            enforce: 'pre',
            test: /\.ts$/,
            loader: 'tslint-loader',
            exclude: [helpers.root('node_modules')]
        }, {
            test: /\.ts$/,
            loader: 'awesome-typescript-loader',
            options: {
                declaration: false
            },
            exclude: [/\.spec\.ts$/]
        }]
    },
    };

    module.exports = [ webConfig, nodeConfig ];
    