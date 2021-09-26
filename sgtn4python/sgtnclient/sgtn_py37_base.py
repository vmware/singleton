# coding=utf-8
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#


import urllib.request as httplib
import contextvars
import asyncio

_singleton_locale_context = contextvars.ContextVar('_singleton_locale_', default=None)


class SgtnAsyncWork():

    def __init__(self, thread_obj):
        self._thread_obj = thread_obj

    async def do_work(self):
        self._thread_obj.do_work()

    def start(self):
        try:
            loop = asyncio.get_running_loop()
        except RuntimeError:  # if cleanup: 'RuntimeError: There is no current event loop..'
            loop = None

        if loop and loop.is_running():
            task = loop.create_task(self.do_work())
        else:
            asyncio.run(self.do_work())


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
    def do_multitask(thread_obj, need_async):
        if need_async:
            work = SgtnAsyncWork(thread_obj)
            work.start()
        else:
            thread_obj.start()

    @staticmethod
    def set_current_locale(locale):
        _singleton_locale_context.set(locale)

    @staticmethod
    def get_current_locale():
        locale = _singleton_locale_context.get()
        return locale
