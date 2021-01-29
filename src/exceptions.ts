/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

/**
 * Error for missing required parameter
 * @param type
 * @param name
 */
export function ParamaterError(type: string, name: string) {
    return Error(`Paramater: '${name}' required for '${type}'`);
}

/**
 * Error for invalid parameter
 * @param message
 */
export function invalidParamater( message: string) {
    return Error(`InvalidParamater: '${message}'`);
}
