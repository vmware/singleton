/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import { isDefined } from '../util';

/**
 * Enum for Type values for Stringable Object
 * type ObjectType = 'stringableList' | 'otherType' ;
 * @readonly
 */
export enum ObjectType {
    stringableList = 1,
    otherType
}

/**
 * Interface for classes that represent a stringable Object.
 * Providing two abastract methods here in interface is inntended for any Objects implementation,
 * so that the objects can be stringable
 */
export interface Stringable {
    check(): boolean;
    toString(): string;
}
/**
 * listObject is one object type which vip first supports for converting object to string
 * in future, if have more objects have requirements for converting into string, they have to
 * implement Stringable interface just like this listObject class.
 */
export class listObject implements Stringable {

    constructor(public obj: Object) {
        this.obj = obj;
    }
    listItems: {
        [index: number]: string;
    }
    separatorType: string;
    type: ObjectType;
    check(): boolean {
        return this.obj && this.obj['listItems'] && this.obj['listItems'] instanceof Array && this.obj['listItems'].every((i: any) => (typeof i === "string"))
            && this.obj['type'] && typeof this.obj['type'] == 'number' && this.obj['separatorType'] && typeof this.obj['separatorType'] == 'string';
    }
    toString(): string {
        return (this.obj as {})['listItems'].join(this.obj['separatorType']);
    }
}

/**
 * This is default object type which can't be converted into string, so directly print error with object structure in browser console
 */
export class defaultObject implements Stringable {
    constructor(public obj: Object) {
        this.obj = obj;
    }
    check(): boolean {
        return true;
    }
    toString(): string {
        console.error(JSON.stringify(this.obj));
        return JSON.stringify(this.obj);
    }

}

/**
 * Using factory model here is in order to make the functionality flexiable and scalable,
 * if have other objects require converting into string, only add corresponding condition judgement along with 
 * Object type class which implements Stringable interface
 * @param obj 
 */

export function factory(obj: Object) {
    if (obj['type'] == ObjectType.stringableList) {
        return new listObject(obj);
    }
    else {
        return new defaultObject(obj);
    }

}

/**
 * mainly handle source args Array, and make sure each parameter in Array converting into string 
 * @param [args] 
 * @returns  
 */
export function filterArgs(args?: any[] | {}) {
    if (isDefined(args) && Object.prototype.toString.call(args) == '[object Array]') {
        (args as any[]).forEach((arg, i) => {
            if (Object.prototype.toString.call(arg) == '[object Object]') {
                var obj = factory(arg);
                if (obj.check()) {
                    (args as any[]).splice(i, 1, obj.toString());
                } else {
                    console.error(JSON.stringify(arg));
                    (args as any[]).splice(i, 1, JSON.stringify(arg));
                }
            }
        });
    }
    return args;
}
