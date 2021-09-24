# coding=utf-8
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#


import urllib.request as httplib
import contextvars

_singleton_locale_context = contextvars.ContextVar('_singleton_locale_', default=None)


class SgtnPyBase:

    @staticmethod
    def int_to_unicode(value):
        return chr(value)

    @staticmethod
    def get_httplib():
        return httplib

    @staticmethod
    def open_file(file_name, mode):
        return open(file_name, mode, encoding='utf-8')

    @staticmethod
    def set_current_locale(locale):
        _singleton_locale_context.set(locale)

    @staticmethod
    def get_current_locale():
        locale = _singleton_locale_context.get()
        return locale
