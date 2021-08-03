# coding=utf-8
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import urllib2 as httplib
import sys
reload(sys)
sys.setdefaultencoding('utf-8')


class SgtnPyBase:

    @staticmethod
    def int_to_unicode(value):
        return unichr(value)

    @staticmethod
    def get_httplib():
        return httplib

    @staticmethod
    def open_file(file_name, mode):
        return open(file_name, mode)
