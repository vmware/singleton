# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import unittest
import time
import threading
from threading import Thread

sys.path.append('../sgtnclient')
if sys.version_info.major == 2:
    # Support utf8 text
    reload(sys)
    sys.setdefaultencoding('utf-8')

import I18N

count = 0
WAIT = 6

from util import logger, Util


class WorkThread(Thread):

    def __init__(self, locale):
        global count
        count += 1
        Thread.__init__(self)
        self.locale = locale
        self.count = count

    def run(self):
        global logger

        I18N.set_current_locale(self.locale)

        time.sleep(WAIT)

        theLocale = I18N.get_current_locale()

        thid = threading.current_thread().ident
        logger.info('--- %s --- th%s --- %s ---' % (self.count, thid, theLocale))


class TestMultiTask(unittest.TestCase):

    def do_test_current_locale_in_threads(self):
        global logger

        locales = ['en', 'en-US', 'de', 'zh-CN', 'fr']

        for k in range(3):
            for i in range(100):
                th = WorkThread(locales[i % len(locales)])
                th.start()
            time.sleep(1)
            logger.info('>\n>\n>\n')

        I18N.set_current_locale('de')
        theLocale = I18N.get_current_locale()
        time.sleep(WAIT)

    def do_test_current_locale_in_async(self):
        global logger
        if Util.is_async_supported():
            from async_util import AsyncWork 
            AsyncWork().hello()

    def test_all(self):
        self.do_test_current_locale_in_threads()
        self.do_test_current_locale_in_async()
        
        logger.info('--- end ---')
        time.sleep(1)


if __name__ == '__main__':
    unittest.main()

