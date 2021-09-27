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

    @staticmethod
    def do_multitask(thread_obj, need_async):
        thread_obj.start()

    @staticmethod
    def set_current_locale(locale):
        current = sys._getframe().f_back.f_back
        while current is not None:
            if not hasattr(current, 'f_locals'):
                break

            locals = current.f_locals
            locals['_singleton_locale_'] = locale

            if not hasattr(current, 'f_back'):
                break
            current = current.f_back

    @staticmethod
    def get_current_locale():
        current = sys._getframe().f_back.f_back
        while current is not None:
            if not hasattr(current, 'f_locals'):
                break

            locals = current.f_locals
            if '_singleton_locale_' in locals:
                return locals['_singleton_locale_']

            if not hasattr(current, 'f_back'):
                break
            current = current.f_back
        return None
