"use strict";
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Error for missing required parameter
 * @param type
 * @param name
 */
function ParamaterError(type, name) {
    return Error(`Paramater: '${name}' required for '${type}'`);
}
exports.ParamaterError = ParamaterError;
/**
 * Error for invalid parameter
 * @param message
 */
function invalidParamater(message) {
    return Error(`InvalidParamater: '${message}'`);
}
exports.invalidParamater = invalidParamater;
