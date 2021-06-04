/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
const fs = require('fs');
const path = require("path");


/*
 * class Util
 */
function Util() {
    this.show_info = false;
}
/*
 * class ExtractUtil
 */
function ExtractUtil() {
}
/*
 * class ExtractItem
 */
function ExtractItem() {
    this.is_pipe = false;
    this.block = '';
    this.changed = '';

    this.key = null;
    this.key_quote = null;
    this.source_end = null;
    this.ahead = null;
    this.source = null;
    this.parameter = null;
}
/*
 * class ExtractData
 */
function ExtractData() {
    this.path = ''
    this.items = []
    this.index = 0
    this.count = 0
    this.print_dir = false
}
/*
 * class ExtractPathData
 */
function ExtractPathData(handle_path) {
    this.path = handle_path;
    this.is_path_printed = false;

    this.file_name = ''
    this.file_pieces = []
}


var util = new Util()
var exutil = new ExtractUtil()


/*
 * Log an item
 */
Util.prototype.log = function(item){
    console.log(item);
}

/*
 * Read a file in synchronized mode
 */
Util.prototype.read = function(file_name) {
    var content = fs.readFileSync(file_name, 'utf-8');
    return content;
}

/*
 * Write a file in synchronized mode
 */
Util.prototype.write = function(file_name, content) {
    fs.writeFileSync(file_name, content);
}

/*
 * Create new directories in a path.
 */
Util.prototype.mkdirs = function(dirname) {
    if (fs.existsSync(dirname)) {
        return true;
    }

    if (this.mkdirs(path.dirname(dirname))) {
        fs.mkdirSync(dirname);
        return true;
    }
}

/*
 * Combine to make a path
 *
 * The return will end with '/' if 'second' is null or ''.
 *
 */
Util.prototype.combine_path = function(first, second) {
    if (second == null) {
        second = ''
    }
    return (first + '/' + second).replace('//', '/');
}

/*
 * Get the last char of a string
 */
Util.prototype.get_last_char = function(text) {
    if (text == null || text.length == 0) {
        return null;
    }

    return text.substring(text.length - 1);
}

/*
 * Get the last char of a string
 */
Util.prototype.get_item = function(array, index) {
    if (index < 0) {
        return array[array.length + index];
    }
    return array[index];
}

/*
 * Get brief text
 */
Util.prototype.get_brief = function(text, index) {
    var parts = text.split(/[\r]*\n/);
    var str = this.get_item(parts, index);
    return str;
}


/*
 * Get regx for key token
 */
ExtractUtil.prototype.get_key_token = function(token) {
    var reg = null;

    if (token == '"') {
        reg = /\s*\"([\w|\-|\.]*)\"/;
    } else if (token == "'") {
        reg = /\s*\'([\w|\-|\.]*)\'/;
    } else if (token == '`') {
        reg = /\s*`([\w|\-|\.]*)`/;
    }

    return reg;
}

/*
 * Get regx for ahead token
 */
ExtractUtil.prototype.get_ahead_token = function(token) {
    var reg = null;

    if (token == '"') {
        reg = /(\s*\")/;
    } else if (token == "'") {
        reg = /(\s*\')/;
    } else if (token == '`') {
        reg = /(\s*`)/;
    }

    return reg;
}

/*
 * Extract key
 */
ExtractUtil.prototype.get_key = function(item) {
    var text = item.block;
    var key_quote = util.get_last_char(text);
    if (key_quote == null) {
        return null;
    }

    var reg = this.get_key_token(key_quote);
    if (reg == null) {
        return null;
    }

    var parts = text.split(reg);
    if (parts.length < 3 || util.get_item(parts, -1) != '') {
        return null;
    }

    item.key_quote = key_quote;
    item.key = util.get_item(parts, -2);

    text = util.get_item(parts, -3) + key_quote;
    var ahead = this.get_ahead_token(key_quote);
    var ahead_parts = text.split(ahead);
    if (ahead_parts.length > 2) {
        item.ahead = util.get_item(util.get_item(ahead_parts, -3), -1);
        if (item.ahead == '+') {
            item.key = null;
        }
    }
    return item.key;
}

/*
 * Replace the keyword 'translate' with 'vtranslate'.
 */
ExtractUtil.prototype.replace_pipe = function(str) {
    var reg = /(\|\s*translate\s*\:\s*)/;
    var parts = str.split(reg);
    if (parts.length > 1) {
        // There is the keyword 'translate'.
        parts[1] = parts[1].replace('translate', 'vtranslate');
    }
    return parts.join('');
}

/*
 * Extract source message.
 */
ExtractUtil.prototype.get_source = function(str) {
    var reg = null;
    if (str != null && str.length > 0) {
        var token = str[0];
        if (token == '"') {
            reg = /([\\]*\")/;
        } else if (token == '`') {
            reg = /([\\]*`)/;
        } else if (token == "'") {
            reg = /([\\]*\')/;
        }
    }
    if (reg == null) {
        return null;
    }

    var parts = str.split(reg);
    var strs = [];

    for(var i=2; i<parts.length; i++) {
        if(i % 2 == 1) {
            if (parts[i].length % 2 == 0) {
                strs.push(parts[i]);
            } else {
                strs.push(parts[i].substring(0, parts[i].length - 1));
                break;
            }
        } else {
            strs.push(parts[i]);
        }
    }

    return strs.join('');
}


/*
 * class Extract
 */
function Extract() {
}

/*
 * Build the new file with converted pipes.
 */
Extract.prototype.build_convert = function(items, exd, pdata) {
    for(var i=0; i<items.length; i++) {
        var item = items[i];
        var text = item.changed;
        if (text.length == 0) {
            text = item.block;
        }
        pdata.file_pieces.push(text);

        if (i % 2 == 1) {
            exd.index ++;

            if (item.is_pipe) {
                exd.count ++;
            }
        }

        exd.items.push(item);
    }

    if (items.length > 0) {
        items[0].path = util.combine_path(exd.path, pdata.file_name);
    }

    // make even number of items
    var item = new ExtractItem();
    item.block = null;
    exd.items.push(item);
}

/*
 * Extract a piece of source.
 */
Extract.prototype.extract_source = function(items, i, raw) {
    var token = raw[0];
    var source = exutil.get_source(raw);
    if (source == null) {
        return;
    }

    var code = 'var full = ' + token + source + token;

    try {
        var parts = code.split(/\s*\n\s*/g);
        code = parts.join(' ');
        eval(code);

    } catch(error) {
        console.log(code);
        util.log(items[i]);
        return;
    }

    this.sources.push(full);
    var changed = raw.substring(source.length + 2);

    var first = changed.trim()[0];
    if (first != '+') {
        items[i+2].source_end = first;
        var v = changed.split(first);
        v[0] = '';
        changed = v.join(first);

        this.valid = true;
        if (first == ':' || first == ',') {
            items[i+2].parameter = changed.substring(2);
            items[i+2].changed = changed.substring(1);
        } else {
            items[i+2].changed = changed;
        }

        return;
    }

    var v = changed.split('+');
    v[0] = '';
    raw = v.join('+').substring(1).trim();
    this.extract_source(items, i, raw);
}

/*
 * Build the new file with converted pipes.
 */
Extract.prototype.extract = function(items, i, key) {
    if (util.show_info) {
        util.log('--- key --- ' + key);
    }

    this.valid = false;
    this.sources = [];

    this.extract_source(items, i, items[i+2].block);
    if (this.valid) {
        items[i+1].source = this.sources.join('');

        var str = exutil.replace_pipe(items[i+1].block);
        if (items[i+2].parameter == null) {
            str = str.replace(':', '');
        }
        items[i+1].changed = str;
        items[i+1].is_pipe = true;
    }
}

/*
 * Scan and convert the pipe from a text.
 *
 * Parameter:
 *      ExtractData
 *
 */
Extract.prototype.regx = function(str, exd, pdata) {
    var reg = /(\s*\|\s*translate\s*\:\s*)/;
    var segs = str.split(reg);

    var items = [];
    for(var i=0; i<segs.length; i++) {
        item = new ExtractItem();
        item.block = segs[i];
        items.push(item);
    }
    if (items.length < 3) {
        // There is no 'translate' pipe.
        return;
    }

    if (!pdata.is_path_printed) {
        // Whether current path has already been displayed?
        util.log("\n--- path --- " + exd.path);
        pdata.is_path_printed = true;
    }
    util.log("    file --- " + pdata.file_name);

    var parts = [];
    for(var i=0; i<items.length - 2; i++) {
        var raw = items[i].block
        var text = raw
        if (i % 2 == 0) {
            var cut = 20;
            if (raw.length > cut * 2 + 6) {
                text = raw.substring(0, cut);
                text += '......';
                text += raw.substring(raw.length - cut);
            }

            var key = exutil.get_key(items[i]);
            if (key) {
                this.extract(items, i, key);
            }
        }
        parts.push(text);
    }

    this.build_convert(items, exd, pdata);
}


/*
 * List exported functions.
 */
module.exports = {
    Util,
    Extract,
    ExtractData,
    ExtractPathData
}
