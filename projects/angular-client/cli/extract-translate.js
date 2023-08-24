#!/usr/bin/env node
/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
let fs = require('fs');
let ArgumentParser = require('argparse').ArgumentParser;

let extract_util = require('./extract-util');


let arg_parser = new ArgumentParser({
    add_help: true,
    description: "Convert pipe 'translate' to 'vtranslate'"
});

arg_parser.add_argument(
    ('-s', '--source-dir'),
    {
        help: 'Source Directory',
        required: true,
    }
);

arg_parser.add_argument(
    ('-t', '--target-dir'),
    {
        help: 'Target Directory',
        required: true,
    }
);

let args = arg_parser.parse_args();

var util = new extract_util.Util()
var ex = new extract_util.Extract()


/*
 * class App
 */
function App() {

    this.args = process.argv.splice(2);

    this.source_dir = ''
    this.target_dir = ''

    this.all_files = []
    this.conflict = 0
    this.repeat = 0

    this.exd = new extract_util.ExtractData()
}

App.prototype.handle_path = function(path) {
    var path_data = new extract_util.ExtractPathData(path);

    var files = fs.readdirSync(path);

    for(var i=0; i<files.length; i++) {
        var filename = files[i];

        var one = util.combine_path(path, filename);

        if (fs.existsSync(one)) {
            if (fs.statSync(one).isDirectory()) {
                this.handle_path(one);
            } else if (fs.statSync(one).isFile()) {
                if (one.endsWith('.html') || one.endsWith('.ts') || one.endsWith('.js')) {
                    this.handle_one(path, one, path_data)
                }
            }
        }
    };
}

App.prototype.handle_one = function(path, one, pdata) {
    this.all_files.push(one);
    var content = util.read(one);

    this.exd.path = path;
    pdata.file_name = one.substring(path.length);
    pdata.file_pieces = [];

    ex.regx(content, this.exd, pdata);

    if (pdata.file_pieces.length > 0) {
        var target_path =
            this.target_dir +
            this.exd.path.substring(this.source_dir.length);
        util.mkdirs(target_path);

        var target_file = util.combine_path(target_path, pdata.file_name);
        var text = pdata.file_pieces.join('');
        util.write(target_file, text);
    }
}

/*
 * Log directory for displaying result
 */
App.prototype.print_dir_for_result = function(dir) {
    if (this.exd.print_dir) {
        var text = "\n    file --- " + dir;
        if (text.length < 70) {
            text += '----------------------------------------------------';
            text = text.substring(0, 70);
        }
        util.log(text);
        this.exd.print_dir = false;
    }
}

/*
 * Show expression item
 */
App.prototype.show_expression = function(items, i, current) {
    this.print_dir_for_result(current);
    var ex_item = {};
    ex_item.before = util.get_brief(items[i-1].block, -1);
    ex_item.pipe = items[i].block;
    ex_item.after = util.get_brief(items[i+1].block, 0);

    var key_part = (items[i-1].key == null) ? '' : items[i-1].key + ' ---';
    util.log('        expression --- ' + key_part);
    util.log(ex_item);
}

/*
 * Handle result, output statistics, write the resource file
 */
App.prototype.handle_result = function() {
    util.log('\n--- statistics ------------------------------------------------------');
    var abnormal = this.exd.index - this.exd.count;

    var kv_table = {};
    var extracted = 0;
    var items = this.exd.items;
    var current = (items.length > 0) ? items[0].path : null;
    this.exd.print_dir = true;
    for(var i=1; i<items.length-1; i+=2) {
        if (items[i].block == null) {
            this.exd.print_dir = true;
            current = items[i+1].path;
            continue;
        }
        if (!items[i].is_pipe) {
            this.show_expression(items, i, current);
            continue;
        }

        var key = items[i-1].key;
        var source = items[i].source;
        if(kv_table.hasOwnProperty(key)) {
            if (kv_table[key] != source) {
                this.print_dir_for_result(current);
                util.log('        conflict --- ' + key);
                this.conflict ++;
            } else {
                if (util.show_info) {
                    this.print_dir_for_result(current);
                    util.log('        repeat --- ' + key);
                }
                this.repeat ++;
            }

            continue;
        }

        kv_table[key] = source;
        extracted ++;

        if (items[i+1].parameter != null && util.show_info) {
            this.print_dir_for_result(current);
            util.log('        parameter --- ' + key);
            var para_item = {};
            para_item.source = items[i].source;
            para_item.parameter = util.get_brief(items[i+1].parameter, 0);
            util.log(para_item);
        }
    }
    var text = 'const ENGLISH = ' + JSON.stringify(kv_table, null, 4);
    util.write(this.target_dir + 'English_source.js', text);

    util.log('\n--- expression --- ' + abnormal);
    util.log('--- conflict --- ' + this.conflict);
    util.log('--- repeat --- ' + this.repeat);
    util.log('--- extracted --- ' + extracted);
    util.log('--- total --- ' + this.exd.count);
}

/*
 * main()
 *
 * return
 *      whether the parameter format is correct
 */
App.prototype.main = function() {
    this.source_dir = util.combine_path(args.source_dir, null);
    this.target_dir = util.combine_path(args.target_dir, null);

    util.log("--- search --- " + this.source_dir);
    this.handle_path(this.source_dir);

    this.handle_result();

    return true;
}

util.log("--- start ---\n");

app = new App()
if (!app.main()) {
    util.log("There should be two parameters: \n  1. source directory \n  2. target directory");
}

util.log("\n--- end ---");
