export function ParamaterError(type: any, name: Object) {
    return Error(`Paramater: '${name}' required for '${type}'`);
}
