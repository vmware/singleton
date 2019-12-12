/**
 * Error for missing required parameter
 * @param type
 * @param name
 */
export declare function ParamaterError(type: string, name: string): Error;
/**
 * Error for invalid parameter
 * @param message
 */
export declare function invalidParamater(message: string): Error;
