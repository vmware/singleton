/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
var fs = require('fs');

var LogService = (function () {
    var logServiceInstance;
    var isEabled = true;
    function init(isEabled) {
        isEabled = !!isEabled;
        return {
            debug: function () {
                if (isEabled) {
                    const arrs = Array.prototype.slice.call(arguments);
                    console.log.apply(console, ['DEBUG'].concat(arrs));
                }
            },
            info: function () {
                console.log.apply(console, arguments);
            },
            error: function () {
                console.error.apply(console, arguments);
            },
            warn: function () {
                console.warn.apply(console, arguments);
            },
        };
    }
    return {
        getLogServiceInstance: function (isEabled) {
            if (!logServiceInstance) {
                return init(isEabled);
            }
            return logServiceInstance;
        }
    };
})();
var walkDirectory = function (dir, done) {
    let results = [];
    fs.readdir(dir, function (err, list) {
        if (err) {
            console.log(err);
            return;
        }

        let i = 0;
        (function next() {
            let file = list[i++];
            if (!file) return done(results);
            file = dir + '/' + file;
            fs.stat(file, (err, stat) => {
                if (stat && stat.isDirectory()) {
                    walkDirectory(file, (res) => {
                        results = results.concat(res);
                        next();
                    });
                } else if (err) {
                    console.log(err);
                } else {
                    results.push(file);
                    next();
                }
            });
        })();
    });
};

exports.LogService = LogService;
exports.walkDirectory = walkDirectory;