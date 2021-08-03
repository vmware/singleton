# coding=utf-8
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os


class SgtnDebug:

    LOG_INTERNAL = ''

    @classmethod
    def set_internal_log(cls, file_name):
        cls.LOG_INTERNAL = file_name
        cls.log_text('debug', '--- internal log --- {0} ---'.format(file_name))

    @classmethod
    def log_text(cls, desc, data):
        if cls.LOG_INTERNAL:
            if not os.path.exists(cls.LOG_INTERNAL):
                f = open(cls.LOG_INTERNAL, 'w')
            else:
                f = open(cls.LOG_INTERNAL, 'a')
            text = '[{0}] {1}\n'.format(desc, data)
            f.write(text)
            f.close()
