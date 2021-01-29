/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const consoleLogger = {
    log(args: any) { console.log(...args); },
    warn(args: any) { console.warn(...args); },
    error(args: any) { console.error(...args); },
};

export class Logger {
    private prefix: string;
    private logger: any;
    private options: any;
    private debug: boolean;
    constructor(concreteLogger: any, options = {}) {
        this.init(concreteLogger, options);
    }

    private init(concreteLogger: any, options: any = {}) {
        this.prefix = options.prefix || 'vip-js-client';
        this.logger = concreteLogger || consoleLogger;
        this.options = options;
        this.debug = options.debug || false;
    }

    setDebug(bool: boolean) {
        this.debug = bool;
    }

    log(...args: any[]) {
        return this.forward(args, 'log', '', true);
    }

    warn(...args: any[]) {
        return this.forward(args, 'warn', '', true);
    }

    error(...args: any[]) {
        return this.forward(args, 'error', '', false);
    }

    deprecate(...args: any[]) {
        return this.forward(args, 'warn', 'WARNING DEPRECATED: ', true);
    }

    forward(args: any, lvl: string, prefix: string, debugOnly: boolean) {
        if (debugOnly && !this.debug) { return null; }
        if (typeof args[0] === 'string') { args[0] = `${prefix}${this.prefix} ${args[0]}`; }
        return this.logger[lvl](args);
    }

    create(moduleName: string) {
        return new Logger(this.logger, {
            ...{ prefix: `${this.prefix}:${moduleName}:` },
            ...this.options,
        });
    }
}

export const basedLogger = new Logger(consoleLogger);
