/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
Error.stackTraceLimit = Infinity;

require('core-js/es6');
require('core-js/es7/reflect');

// Typescript emit helpers polyfill
require('ts-helpers');

require('zone.js/dist/zone');
require('zone.js/dist/long-stack-trace-zone');
require('zone.js/dist/async-test');
require('zone.js/dist/fake-async-test');
require('zone.js/dist/sync-test');
require('zone.js/dist/proxy'); // since zone.js 0.6.15
require('zone.js/dist/jasmine-patch'); // put here since zone.js 0.6.14

// RxJS
require('rxjs');

var testing = require('@angular/core/testing');
var browser = require('@angular/platform-browser-dynamic/testing');

testing.TestBed.initTestEnvironment(
    browser.BrowserDynamicTestingModule,
    browser.platformBrowserDynamicTesting()
);

var testContext = require.context('../tests', true, /\.spec\.ts/);

function requireAll(requireContext) {
    return requireContext.keys().map(requireContext);
}

// requires and returns all modules that match
var modules = requireAll(testContext);