#!/usr/bin/env python
#
# Copyright 2020 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#
""" Check files license and copyright header
    Argument: -f, --files, given files or directories list, split by ':' in local
                and '\n' in Travis CI(TRAVIS_COMMIT_RANGE shows each files in line)
                if no given arg, all files in current folder(including sub folder) will be checked.
              -e, --exclude, excluded path, split multiple paths by ':', relative and absolute path are all accepted.
              -n, --number, check headers within number of lines
              -a, --all, check files with any extension.
              -p, --pattern, regular expression to match mutiple lines header, using '\n' to separate each line.
                If no given, use Singleton pattern in constant 'SINGLETON_COPYRIGHT_PATTERN'
              -r, --required, name pattern list of file required header even file extension is not in the check list
                If same pattern in required and unrequired arguments, required argument will take priority.
              -u, --unrequired, name pattern list of file not required header even file extension is in the check list
                If same pattern in required and unrequired arguments, required argument will take priority
              -l, --list, extensions of files to be checked, separate by ':', invalid once argument '-a' exists.
                If no given, use extensions in constant 'SOURCE_CODE_EXTENSIONS'

    Scenario 1: Using in travis ci
        Only check the commit files, just need '-f' and optional '-n' argument
        example: python3 ./check_headers.py -f "$(git diff --name-only $TRAVIS_COMMIT_RANGE)" -n 10
    Scenario 2: Using in local developing evnironment
        python ./check_headers.py -f "singleton/singleton-g11n-csharp-client:singleton/singleton-g11n-js-client"
                                  -e "singleton/singleton-g11n-js-client/samples"
                                  -p ".*Copyright [1-9][0-9]{3}(-[1-9][0-9]{3})? VMware, Inc\.\s+\n.*SPDX-License-Identifier: EPL-2.0\s+$"
                                  -l "js:cs:html"

"""
import os
import re
import sys
import argparse
import datetime


SOURCE_CODE_EXTENSIONS = [".java", ".go", ".ts", ".js", ".asp", ".aspx", ".jsp", ".html", ".css", ".php",
".sh", ".py", ".rb", ".cpp", ".c", ".cs", ".h", ".hpp", ".swift", ".sql", ".vb", ".ps1", ".m", ".mm", ".gradle", ".bat", ".xml"]
REQUIRED_PATTERN_LIST = ["makefile", "dockerfile"] #name list of file must to be checked even extension name is not in the list or has no extension name, case not sensitive
SINGLETON_PATTERN = ".*Copyright ([1-9][0-9]{3}-)?"+str(datetime.datetime.now().year)+" VMware, Inc\.\s+\n.*SPDX-License-Identifier: EPL-2.0\s+$"
NOT_REQUIRED_PATTERN_LIST = ["__init__.py", ".*.designer.cs"]
PATH_SEPARATOR = ":"
EXTENSION_SEPARATOR = ":"
TRAVIS_COMMIT_RANGE_PATH_SEPARATOR = "\n"

def need_header(filepath, ext_list, required_pattern_list, unrequired_pattern_list):
    path, filename = os.path.split(filepath)
    filename = filename.lower()
    for pattern in required_pattern_list:
        if re.match(pattern, filename):
            return True
    for pattern in unrequired_pattern_list:
        if re.match(pattern, filename):
            return False
    if not filename.endswith(tuple(ext_list)):
        return False
    return True

def check_header(filepath, line_num, pattern):
    with open(filepath, encoding="utf8") as file:
        patterns = pattern.split("\n")
        for i in range(1, line_num):
            try:
                line = file.readline()
                if re.match(patterns[0], line):
                    is_match = True
                    for i in range(1, len(patterns)):
                        line = file.readline()
                        if not re.match(patterns[i], line):
                            is_match = False
                    return is_match
            except UnicodeDecodeError:
                # raise 'UnicodeDecodeError' exception once read a binary file which does not need to be checked.
                pass
        return False
    return True

def is_path_excluded(path, excluded_paths):
    absolute_path = os.path.abspath(path)
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
    parser.add_argument("-a", "--all", help="check all kind of extension", action="store_true")
    parser.add_argument("-p", "--pattern", help="headers pattern", type=str, default=SINGLETON_PATTERN)
    parser.add_argument("-r", "--required", help="file name list must have header", type=str, default=REQUIRED_PATTERN_LIST)
    parser.add_argument("-u", "--unrequired", help="file name list have no header", type=str, default=NOT_REQUIRED_PATTERN_LIST)
    parser.add_argument("-l", "--list", help="extensions of files to be checked", type=str, default="")
    args = parser.parse_args()

    files_str = args.files
    line_num = args.number
    exclude_str = args.exclude
    check_all_extension = args.all
    headers_pattern = args.pattern
    required_pattern_list = args.required
    unrequired_pattern_list = args.unrequired
    ext_list_str = args.list

    errs = []
    excluded_paths = []
    if exclude_str != "":
        excluded_paths = exclude_str.split(PATH_SEPARATOR)
    if ext_list_str == "":
        ext_list = SOURCE_CODE_EXTENSIONS
    else:
        ext_list = ext_list_str.replace(" ", "").split(EXTENSION_SEPARATOR)
        for index, extension in enumerate(ext_list):
            if not extension.startswith("."):
                ext_list[index] = "." + extension

    print("============== Start to check files ================")
    if files_str == "":
        print("No given files, check all files in current folder(including sub folder).")
        files = [os.getcwd()]
    elif TRAVIS_COMMIT_RANGE_PATH_SEPARATOR in files_str:
        files = files_str.split(TRAVIS_COMMIT_RANGE_PATH_SEPARATOR)
    elif PATH_SEPARATOR in files_str:
        files = files_str.split(PATH_SEPARATOR)
    else:
        files = [files_str]
    print("Target files/directories:\n    {}".format("\n    ".join(files)))
    for f in files:
        if os.path.isdir(f):
            for dirpath, dirnames, filenames in os.walk(f):
                if excluded_paths != [] and is_path_excluded(dirpath, excluded_paths):
                    continue
                for filename in filenames:
                    filepath = os.path.join(dirpath, filename)
                    if check_all_extension or need_header(filepath, ext_list, required_pattern_list, unrequired_pattern_list):
                        # print("Checking current file: '{}'".format(filepath))
                        if check_header(filepath, line_num, headers_pattern) is False:
                            errs.append(filepath)
        else:
            if check_all_extension or need_header(f, ext_list, required_pattern_list, unrequired_pattern_list):
                # print("Checking current file: '{}'".format(f))
                if check_header(f, line_num, headers_pattern) is False:
                    errs.append(f)
    if len(errs) != 0:
        print("Files miss license or copyright headers:\n    {}".format("\n    ".join(errs)))
        print("========================================= Fail =========================================\n" +
              "    Missing headers in {} files, see file list above.\n".format(len(errs)) +
              "    Header check pattern '{}'\n".format(headers_pattern) +
              "    Details refer to contributing guide to see how to add license and copyright headers.\n" +
              "========================================================================================")
        sys.exit(1)
    print("============== PASS ==============")
