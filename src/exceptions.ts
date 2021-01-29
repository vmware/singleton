/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

/**
 * Customize Error message for timeout issue.
 * @class VIPTimeOutError
 * @extends {Error}
 */

import {Type, ɵstringify as stringify} from '@angular/core';

export class VIPTimeOutError extends Error {
    constructor(m: string) {
        super(m);
        // Set the prototype explicitly.
        Object.setPrototypeOf(this, VIPTimeOutError.prototype);
    }
    message: string = this.message +
    '; Please check the network connection with VIP server and timeout settings.';
}

export function invalidPipeArgumentError(type: Type<any>, value: Object) {
    return Error(`InvalidPipeArgument: '${value}' for pipe '${stringify(type)}'`);
}

export function invalidParamater( message: string) {
    return Error(`InvalidParamater: '${message}'`);
}
