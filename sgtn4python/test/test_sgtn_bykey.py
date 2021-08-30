# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import unittest

sys.path.append('../sgtnclient')
from sgtn_locale import SingletonLocale, SingletonLocaleUtil
from sgtn_bykey import SingletonByKey, SingletonByKeyLocale, SingletonByKeyComponents


class Config:

    def __init__(self):
        self.source_locale = 'en'
        self.default_locale = 'en'
        self.cache_type = 'by_key'
        self.pseudo = False


class TestByKey(unittest.TestCase):

    def check_one(self, bykey, key, message, idComponent, localeObj):
        bykey.set_string(key, None, idComponent, localeObj, message)
        msg = bykey.get_string(key, -1, localeObj)
        print('--- message --- %s --- %s' % (key, msg))

    def show_one(self, bykey, key, localeObj):
        msg = bykey.get_string(key, -1, localeObj)
        print('--- message --- %s --- %s' % (key, msg))

    def test_release(self):
        cfg = Config()
        bykey = SingletonByKey(cfg, False)
        id1 = bykey.get_component_index('first')
        id2 = bykey.get_component_index('second')
        print('--- component id --- %s' % [id1, id2])

        localeObj1 = bykey.get_locale_item('en', True)
        localeObj2 = bykey.get_locale_item('en', False)
        localeObj3 = bykey.get_locale_item('de_de', False)
        localeObj4 = bykey.get_locale_item('de-DE', False)

        item = bykey.get_key_item(0, 0)

        bykey.set_string("key1", None, id1, localeObj1, "en_message")
        bykey.set_string("key1", None, id1, localeObj2, "en_message")

        self.check_one(bykey, "key1", "de_message", id1, localeObj3)
        self.check_one(bykey, "key2", "de_message3", id1, localeObj3)
        self.check_one(bykey, "key1", "de_message2", id1, localeObj3)

        self.show_one(bykey, "key1", localeObj4)

        messages = bykey.get_messages(id1, localeObj3)
        print('--- messages --- %s' % messages)

    def test_locale(self):
        sgtnLocale = SingletonLocaleUtil.get_singleton_locale(None)
        print('--- locale --- %s' % sgtnLocale.get_near_locale_list())

        sgtnLocale = SingletonLocaleUtil.get_singleton_locale('zh_cn')
        print('--- locale --- %s' % sgtnLocale.get_near_locale_list())

        sgtnLocale = SingletonLocaleUtil.get_singleton_locale('de_de')
        print('--- locale --- %s' % sgtnLocale.get_near_locale_list())


if __name__ == '__main__':
    unittest.main()
