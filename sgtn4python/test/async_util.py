# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import threading
import asyncio


class I18N:

    @staticmethod
    def set_current_locale(locale):
        SingletonClientManager().set_current_locale(locale)

    @staticmethod
    def get_current_locale():
        return SingletonClientManager().get_current_locale()


class SingletonClientManager:
    def set_current_locale(self, locale):
        current = sys._getframe().f_back.f_back
        for i in range(10):
            if not hasattr(current, 'f_locals'):
                break

            locals = current.f_locals
            locals['_singleton_locale_'] = locale

            if not hasattr(current, 'f_back'):
                break
            current = current.f_back

    def get_current_locale(self):
        current = sys._getframe().f_back.f_back
        for i in range(10):
            if not hasattr(current, 'f_locals'):
                break

            locals = current.f_locals
            if '_singleton_locale_' in locals:
                return locals['_singleton_locale_']

            if not hasattr(current, 'f_back'):
                break
            current = current.f_back
        return LOCALE_DEFAULT


async def hello(head, locale, delay):
    tid = threading.current_thread().ident
    I18N.set_current_locale(locale)
    print("%s: Hello world! %s %s" % (head, tid, locale))
    r = await asyncio.sleep(delay)
    first_level(head, tid, 0)

def first_level(head, tid, fsn):
    second_level(head, tid, fsn)

def second_level(head, tid, ssn):
    output(head, tid, ssn)

def output(head, tid, osn):
    current = I18N.get_current_locale()
    print("%s: Hello again! %s %s" % (head, tid, current))


class AsyncWork():

    def hello(self):
        loop = asyncio.get_event_loop()
        loop.run_until_complete(asyncio.gather(
            hello('a', 'en', 5),
            hello('b', 'de', 3)
        ))
        loop.close()


if __name__ == '__main__':
    AsyncWork().hello()