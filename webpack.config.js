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
        filename: 'g11n-js-sdk.umd.js',
        library: 'g11n-js-sdk',
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
        filename: 'g11n-js-sdk.server.js',
        library: 'g11n-js-sdk',
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
    