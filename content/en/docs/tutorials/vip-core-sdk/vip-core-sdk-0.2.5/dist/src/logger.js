"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const consoleLogger = {
    log(args) { console.log(...args); },
    warn(args) { console.warn(...args); },
    error(args) { console.error(...args); },
};
class Logger {
    constructor(concreteLogger, options = {}) {
        this.init(concreteLogger, options);
    }
    init(concreteLogger, options = {}) {
        this.prefix = options.prefix || 'vip-js-client';
        this.logger = concreteLogger || consoleLogger;
        this.options = options;
        this.debug = options.debug || false;
    }
    setDebug(bool) {
        this.debug = bool;
    }
    log(...args) {
        return this.forward(args, 'log', '', true);
    }
    warn(...args) {
        return this.forward(args, 'warn', '', true);
    }
    error(...args) {
        return this.forward(args, 'error', '', false);
    }
    deprecate(...args) {
        return this.forward(args, 'warn', 'WARNING DEPRECATED: ', true);
    }
    forward(args, lvl, prefix, debugOnly) {
        if (debugOnly && !this.debug) {
            return null;
        }
        if (typeof args[0] === 'string') {
            args[0] = `${prefix}${this.prefix} ${args[0]}`;
        }
        return this.logger[lvl](args);
    }
    create(moduleName) {
        return new Logger(this.logger, Object.assign({ prefix: `${this.prefix}:${moduleName}:` }, this.options));
    }
}
exports.Logger = Logger;
exports.basedLogger = new Logger(consoleLogger);
