"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const ARG_OPN = '{';
const ARG_CLS = '}';
const ARG_SEP = ',';
const NUM_ARG = '#';
const ESC = '\'';
function parse(pattern) {
    return parseAST({
        pattern: String(pattern),
        index: 0
    }, '');
}
exports.parse = parse;
function parseAST(current, parentType) {
    const pattern = current.pattern;
    const length = pattern.length;
    const elements = [];
    let text = parseText(current, parentType);
    if (text) {
        elements.push(text);
    }
    while (current.index < length) {
        if (pattern[current.index] === ARG_CLS) {
            if (!parentType) {
                throw expected(current);
            }
            break;
        }
        elements.push(parsePlaceholder(current));
        text = parseText(current, parentType);
        if (text) {
            elements.push(text);
        }
    }
    return elements;
}
function parseText(current, parentType) {
    const pattern = current.pattern;
    const length = pattern.length;
    const isPlural = parentType === 'plural';
    let text = '';
    while (current.index < length) {
        let char = pattern[current.index];
        if (char === ARG_OPN || char === ARG_CLS ||
            (isPlural && char === NUM_ARG)) {
            break;
        }
        else if (char === ESC) {
            char = pattern[++current.index];
            if (char === ESC) {
                text += char;
                ++current.index;
            }
            else if (char === ARG_OPN || char === ARG_CLS ||
                (isPlural && char === NUM_ARG)) {
                text += char;
                while (++current.index < length) {
                    char = pattern[current.index];
                    if (char === ESC && pattern[current.index + 1] === ESC) { // double is always 1 '
                        text += ESC;
                        ++current.index;
                    }
                    else if (char === ESC) {
                        ++current.index;
                        break;
                    }
                    else {
                        text += char;
                    }
                }
            }
            else {
                text += ESC;
            }
        }
        else {
            text += char;
            ++current.index;
        }
    }
    return text;
}
function isWhitespace(code) {
    return ((code >= 0x09 && code <= 0x0D) ||
        code === 0x20 || code === 0x85 || code === 0xA0 || code === 0x180E ||
        (code >= 0x2000 && code <= 0x200D) ||
        code === 0x2028 || code === 0x2029 || code === 0x202F || code === 0x205F ||
        code === 0x2060 || code === 0x3000 || code === 0xFEFF);
}
function skipWhitespace(current) {
    const pattern = current.pattern;
    const length = pattern.length;
    while (current.index < length && isWhitespace(pattern.charCodeAt(current.index))) {
        ++current.index;
    }
}
function parsePlaceholder(current) {
    const pattern = current.pattern;
    if (pattern[current.index] === NUM_ARG) {
        ++current.index; // move passed #
        return [NUM_ARG];
    }
    /* istanbul ignore if should be unreachable if parseAST and parseText are right */
    if (pattern[current.index] !== ARG_OPN) {
        throw expected(current, ARG_OPN);
    }
    ++current.index; // move passed {
    skipWhitespace(current);
    const id = parseId(current);
    if (!id) {
        throw expected(current, 'placeholder id');
    }
    skipWhitespace(current);
    let char = pattern[current.index];
    if (char === ARG_CLS) {
        ++current.index;
        return [id];
    }
    if (char !== ARG_SEP) {
        throw expected(current, ARG_SEP + ' or ' + ARG_CLS);
    }
    ++current.index;
    skipWhitespace(current);
    const type = parseId(current);
    if (!type) {
        throw expected(current, 'placeholder type');
    }
    skipWhitespace(current);
    char = pattern[current.index];
    if (char === ARG_CLS) {
        if (type === 'plural') {
            throw expected(current, type + ' sub-messages');
        }
        ++current.index;
        return [id, type];
    }
    if (char !== ARG_SEP) {
        throw expected(current, ARG_SEP + ' or ' + ARG_CLS);
    }
    ++current.index;
    skipWhitespace(current);
    let arg;
    if (type === 'plural') {
        skipWhitespace(current);
        arg = [id, type, parseSubMessages(current, type)];
    }
    skipWhitespace(current);
    if (pattern[current.index] !== ARG_CLS) {
        throw expected(current, ARG_CLS);
    }
    ++current.index;
    return arg;
}
function parseId(current) {
    const pattern = current.pattern;
    const length = pattern.length;
    let id = '';
    while (current.index < length) {
        const char = pattern[current.index];
        if (char === ARG_OPN || char === ARG_CLS || char === ARG_SEP ||
            char === NUM_ARG || char === ESC || isWhitespace(char.charCodeAt(0))) {
            break;
        }
        id += char;
        ++current.index;
    }
    return id;
}
function parseSubMessages(current, parentType) {
    const pattern = current.pattern;
    const length = pattern.length;
    const options = {};
    while (current.index < length && pattern[current.index] !== ARG_CLS) {
        const selector = parseId(current);
        if (!selector) {
            throw expected(current, 'sub-message selector');
        }
        skipWhitespace(current);
        const arr = parseSubMessage(current, parentType);
        options[selector] = arr;
        skipWhitespace(current);
    }
    if (!options['other'] && parentType === 'plural') {
        throw expected(current, null, null, '"other" sub-message must be specified in plural');
    }
    return options;
}
function parseSubMessage(current, parentType) {
    if (current.pattern[current.index] !== ARG_OPN) {
        throw expected(current, ARG_OPN + ' to start sub-message');
    }
    ++current.index; // move passed {
    const message = parseAST(current, parentType);
    if (current.pattern[current.index] !== ARG_CLS) {
        throw expected(current, ARG_CLS + ' to end sub-message');
    }
    ++current.index; // move passed }
    return message;
}
function expected(current, expectedstr, found, message) {
    const pattern = current.pattern;
    const lines = pattern.slice(0, current.index).split(/\r?\n/);
    const offset = current.index;
    const line = lines.length;
    const column = lines.slice(-1)[0].length;
    found = found || ((current.index >= pattern.length) ? 'end of message pattern'
        : (parseId(current) || pattern[current.index]));
    if (!message) {
        message = errorMessage(expectedstr, found);
    }
    message += ' in ' + pattern.replace(/\r?\n/g, '\n');
    return new MessageSyntaxError(message, expectedstr, found, offset, line, column);
}
// Error function
function errorMessage(expectedstr, found) {
    if (!expectedstr) {
        return 'Unexpected ' + found + ' found';
    }
    return 'Expected ' + expectedstr + ' but found ' + found;
}
/**
 * SyntaxError
 *  Holds information about bad syntax found in a message pattern
 **/
function MessageSyntaxError(message, expectedstr, found, offset, line, column) {
    Error.call(this, message);
    this.name = 'SyntaxError';
    this.message = message;
    this.expected = expectedstr;
    this.found = found;
    this.offset = offset;
    this.line = line;
    this.column = column;
}
MessageSyntaxError.prototype = Object.create(Error.prototype);
