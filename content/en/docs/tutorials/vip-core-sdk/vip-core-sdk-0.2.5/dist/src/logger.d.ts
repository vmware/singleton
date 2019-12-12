export declare class Logger {
    private prefix;
    private logger;
    private options;
    private debug;
    constructor(concreteLogger: any, options?: {});
    private init;
    setDebug(bool: boolean): void;
    log(...args: any[]): any;
    warn(...args: any[]): any;
    error(...args: any[]): any;
    deprecate(...args: any[]): any;
    forward(args: any, lvl: string, prefix: string, debugOnly: boolean): any;
    create(moduleName: string): Logger;
}
export declare const basedLogger: Logger;
