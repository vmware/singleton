// Karma configuration
// Generated on Fri Apr 26 2019 09:38:59 GMT+0800 (GMT+08:00)

module.exports = function (config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine', "karma-typescript"],


    // list of files / patterns to load in the browser
    files: [
      // {pattern: 'test/*.*', watched: false, included: false},
      // {pattern: 'test1/*.*', watched: false, included: false},
      // {pattern: 'test2/*.*', watched: false, included: false},
      { pattern: 'utils/*.*', watched: true, included: true },
      { pattern: 'test_bundle/*.*', watched: true, included: true },
      { pattern: "resources/**/*.ts", watched: true, included: true },
      { pattern: "resources/assets/*.json", watched: false, included: false, served: true, nocache: false },
      { pattern: "data/*.*", watched: false, included: false, served: true, nocache: false },
    ],

    proxies: {
      "/resources/": "http://localhost:9876/base/resources/"
    },

    // list of files / patterns to exclude
    exclude: [
      "node_modules",
      'test/**/*.js',
      'test1/**/*.js',
      'test2/**/*.ts',
      'test3/**/*.ts',
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      "test_bundle/*.ts": ["karma-typescript"],
      "resources/*.ts": "karma-typescript",
      "utils/*.ts": "karma-typescript",
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', "karma-typescript", 'kjhtml'],


    // web server port
    port: 9876,

    // urlRoot: '/',

    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    loggers: [
      { type: 'console' }
    ],


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    // You can capture any browser manually by opening the browser and visiting the URL where the Karma web server is listening (by default it is http://localhost:9876/).
    browsers: [
      // 'Chrome',
      // 'ChromeHeadless',
      'ChromeDebugging'
    ],

    client: {
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },

    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity,

    customLaunchers: {
      ChromeDebugging: {
        base: 'Chrome',
        flags: ['--remote-debugging-port=9333']
      }
    },
  });

  // require('ts-node').register({ 
  //   compilerOptions: { 
  //     module: 'commonjs' 
  //   } 
  // });
  // require('./karma.conf.ts');
};
