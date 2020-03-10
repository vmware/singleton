#
# Copyright 2020 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import re
import sys

CHECK_LINES = 5
EXTENSION_LIST = (".java", ".go", ".ts", ".js", ".asp", ".aspx", ".jsp", ".html", ".php",
".sh", ".py", ".rb", ".cpp", ".c", ".cs", ".swift", ".sql", ".vb", ".ps1", ".m", ".mm")
VMWARE_COPYRIGHT_PATTERN = ".*Copyright [1-9][0-9]{3}(-[1-9][0-9]{3})? VMware, Inc."
EPL_LICENSE = "SPDX-License-Identifier: EPL-2.0"

def need_header(filename):
    if not filename.endswith(EXTENSION_LIST):
        return False
    if filename == "__init__.py":
        return False
    return True

def check_license(filename):
    if need_header(filename):
        with open(filename, encoding="utf8") as file:
            for i in range(1, CHECK_LINES):
                line = file.readline()
                if re.match(VMWARE_COPYRIGHT_PATTERN, line):
                    line = file.readline()
                    return EPL_LICENSE in line
            return False
    return True

if __name__ == "__main__":
    errs = []
    count = 0
    for subdir, dirnames, files in os.walk(os.getcwd()):
        for file in files:
            filepath = os.path.join(subdir, file)
            if check_license(filepath) is False:
                errs.append(filepath)
                count += 1
    if count != 0:
        print("Missing license or copyright headers in {} files:\n{}".format(count, "\n".join(errs)))
        sys.exit(1)
