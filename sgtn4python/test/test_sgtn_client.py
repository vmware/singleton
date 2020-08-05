# -*-coding:UTF-8 -*-
import unittest

import os
import time
import json

import sys
sys.path.append('..')
from sgtn_client import I18n, Release, Translation
from sgtn_util import FileUtil, NetUtil


PRODUCT = 'PYTHON'
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
        
        NetUtil.simulate_data = FileUtil.read_json_file('./simulate.json')
        #NetUtil.record_data = {}

        KEY = 'about.title'
        SOURCE = 'About'

        print('--- test start ---')

        self.prepare_sub_path('log')
        self.prepare_sub_path('singleton')

        I18n.add_config_file('sgtn_client.yml')

        start = time.time()
        I18n.set_current_locale(LOCALE)
        I18n.set_current_locale(LOCALE)
        current = I18n.get_current_locale()
        print('--- current --- %s ---' % current)
        self.assertEqual(current, 'de')

        rel = I18n.get_release(PRODUCT, VERSION)

        cfg = rel.get_config()
        cfg_info = cfg.get_info()
        self.show('config', 'info', self.dict2string(cfg_info))

        trans = rel.get_translation()
        self.check_locale(trans, 'ZH_cn')
        self.check_locale(trans, 'EN_us')

        found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
        print('--- found --- 4 --- %s ---' % found)
        self.assertEqual(found, 'Über')
        
        found = trans.get_string(COMPONENT, KEY, source = SOURCE)
        print('--- found --- 3 --- %s ---' % found)
        found = trans.get_string(COMPONENT, KEY)
        print('--- found --- 1a --- %s ---' % found)
        found = trans.get_string('TT', KEY)
        print('--- found --- 1b --- %s ---' % found)

        if (self.need_wait(cfg_info)):
            time.sleep(5)

        found = trans.get_string(COMPONENT, 'aa', format_items = ['11', '22'])
        print('--- found --- 21 --- %s ---' % found)
        found = trans.get_string(COMPONENT, 'cc', x = 'ee', y = 'ff')
        print('--- found --- 22 --- %s ---' % found)

        spent = time.time() - start

        data = trans.get_locale_strings('en-US')
        #print('--- source --- 2 --- %s ---' % data)

        data = trans.get_locale_strings('de')
        #print('--- source --- 0 --- %s ---' % data)

        if (self.need_wait(cfg_info)):
            found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
            print('--- found2 --- 4 --- %s ---' % found)

        if NetUtil.record_data is not None:
            time.sleep(5)
            FileUtil.save_json_file('./simulate.json', NetUtil.record_data)
        print('--- test --- end --- %s ---' % spent)


if __name__ == '__main__':
    unittest.main()
