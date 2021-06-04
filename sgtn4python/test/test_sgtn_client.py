# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#
import unittest

import os
import time
import json

import sys
sys.path.append('../sgtnclient')
import I18N
from sgtn_util import FileUtil, NetUtil

from util import Util, allTestData

PRODUCT = 'PYTHON1'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'de'


class TestClient(unittest.TestCase):

    def prepare_sub_path(self, sub):
        current_path = os.path.dirname(__file__)
        sub_path = os.path.join(current_path, sub)
        if not os.path.exists(sub_path):
            os.makedirs(sub_path)

    def show(self, text1, text2, value):
        print('--- %s --- %s --- %s ---' % (text1, text2, value))

    def check_locale(self, trans, locale):
        fallback_locale = trans.get_locale_supported(locale)
        self.show('locale', locale, fallback_locale)

    def dict2string(self, dict):
        return json.dumps(dict, ensure_ascii = False, indent = 2)

    def need_wait(self, cfg_info):
        if (cfg_info.get('local') and cfg_info.get('remote')):
            return True
        return False


    def test_api(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        NetUtil.simulate_data = Util.load_response(['data/http_response.txt'])
        #NetUtil.record_data = {}

        Util.load_test_data(['data/test_define.txt'])

        print('--- test start ---')

        self.prepare_sub_path('log')
        self.prepare_sub_path('singleton')

        I18N.add_config_file('config/sgtn_online_only.yml')

        start = time.time()
        I18N.set_current_locale(LOCALE)
        I18N.set_current_locale(LOCALE)
        current = I18N.get_current_locale()
        print('--- current --- %s ---' % current)
        self.assertEqual(current, 'de')

        rel = I18N.get_release(PRODUCT, VERSION)

        cfg = rel.get_config()
        cfg_info = cfg.get_info()
        self.show('config', 'info', self.dict2string(cfg_info))

        trans = rel.get_translation()
        self.check_locale(trans, 'ZH_cn')
        self.check_locale(trans, 'EN_us')

        components = ['about', 'contact']
        locales = ['de', 'zh-Hans']
        for comp in components:
            for loc in locales:
                trans.get_string(comp, '$', locale = loc)
        Util.run_test_data(self, trans, 'TestGetString1')

        found = trans.get_string('TT', 'about.title')
        print('--- found --- wrong component --- %s ---' % found)

        if (self.need_wait(cfg_info)):
            time.sleep(5)

        found = trans.get_string(None, 'about.title', format_items = ['11', '22'])
        print('--- found --- format in array --- %s ---' % found)
        self.assertEqual(found, 'Über Version 22 of Product 11')
        found = trans.get_string(None, 'about.title2', format_items = {'x': 'ee', 'y': 'ff'})
        print('--- found --- format in dict --- %s ---' % found)
        self.assertEqual(found, 'Über Version ee of Product ff')

        spent = time.time() - start

        data = trans.get_locale_strings('en-US', True)
        print('--- source --- en-US --- %s ---' % data)

        data = trans.get_locale_strings('en-US', False)
        print('--- translate --- en-US --- %s ---' % data)

        data = trans.get_locale_strings('de', False)
        print('--- translate --- de --- %s ---' % data)

        if (self.need_wait(cfg_info)):
            found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
            print('--- found2 --- 4 --- %s ---' % found)

        if NetUtil.record_data is not None:
            time.sleep(5)
            FileUtil.save_json_file('data/simulate.json', NetUtil.record_data)
        print('--- test --- end --- %s ---' % spent)


if __name__ == '__main__':
    unittest.main()
