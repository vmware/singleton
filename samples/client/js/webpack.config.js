/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    entry: './src/i18n.utils.js',
    mode: 'production',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname + '/dist'),
        library: 'I18nUtils',
        libraryTarget: 'umd',
    },

    plugins: [new CopyWebpackPlugin([{ from: './src/config.js', to: 'config.js' }])]
};