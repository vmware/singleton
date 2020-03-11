#!/usr/bin/env python
#
# Copyright 2020 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#
""" Check files license and copyright headers
    Argument: -f, --files, given files list, split multiple files by ' ' in local
                and '\n' in Travis CI(TRAVIS_COMMIT_RANGE shows each files in line)
                if no given arg, all files in current folder(including sub folder) will be checked.
              -e, --exclude, excluded path, split multiple paths by ' ', relative and absolute path are all accepted.
              -n, --number, check headers within number of lines
"""
import os
import re
import sys
import argparse

EXTENSION_LIST = (".java", ".go", ".ts", ".js", ".asp", ".aspx", ".jsp", ".html", ".php",
".sh", ".py", ".rb", ".cpp", ".c", ".cs", ".swift", ".sql", ".vb", ".ps1", ".m", ".mm")
VMWARE_COPYRIGHT_PATTERN = ".*Copyright [1-9][0-9]{3}(-[1-9][0-9]{3})? VMware, Inc."
EPL_LICENSE = "SPDX-License-Identifier: EPL-2.0"

def need_header(filename):
    # only source code need header
    if not filename.endswith(EXTENSION_LIST):
        return False
    # __init__.py file has no code
    if filename == "__init__.py":
        return False
    return True

def check_header(filename, line_num):
    if need_header(filename):
        with open(filename, encoding="utf8") as file:
            for i in range(1, line_num):
                line = file.readline()
                if re.match(VMWARE_COPYRIGHT_PATTERN, line):
                    line = file.readline()
                    return EPL_LICENSE in line
            return False
    return True

def is_path_excluded(absolute_path, excluded_paths):
    for exclude_path in excluded_paths:
        exclude_abspath = os.path.abspath(exclude_path)
        # check if given 'absolute_path' is same with excluding one or is sub path of excluding one
        if absolute_path == exclude_abspath or absolute_path.startswith(exclude_abspath+os.sep):
            return True
    return False

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-f", "--files", help="files for checking", type=str, default="")
    parser.add_argument("-e", "--exclude", help="excluding path", type=str, default="")
    parser.add_argument("-n", "--number", help="line number to check", type=int, default=5)
    args = parser.parse_args()

    files_str = args.files
    line_num = args.number
    exclude_str = args.exclude
    errs = []
    count = 0
    excluded_paths = []
    if exclude_str != "":
        excluded_paths = exclude_str.split(" ")
    print("============== Start to check files ================")
    if files_str == "":
        print("No given files, check all files in current folder(including sub folder).")
        for dirpath, dirnames, filenames in os.walk(os.getcwd()):
            if excluded_paths != [] and is_path_excluded(dirpath, excluded_paths):
                continue
            for f in filenames:
                filepath = os.path.join(dirpath, f)
                if check_header(filepath, line_num) is False:
                    errs.append(filepath)
                    count += 1
    else:
        if "\n" in files_str:
            files = files_str.split("\n")
        elif " " in files_str:
            files = files_str.split(" ")
        else:
            files = [files_str]
        print("Given files:\n{}".format("\n".join(files)))
        for f in files:
            print("Checking current file: '{}'".format(f))
            if check_header(f, line_num) is False:
                errs.append(f)
                count += 1
    if count != 0:
        print("Files miss license or copyright headers:\n{}".format("\n".join(errs)))
        print("========================================= Fail =========================================\n" +
              "    Missing headers in {} files, see file list above.\n".format(count) +
              "    Refer to contributing guide to see how to add license and copyright headers.\n" +
              "========================================================================================")
        sys.exit(1)
    print("============== PASS ==============")
