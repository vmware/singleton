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

from util import Util, TestSimulate

_plans_pool = FileUtil.parse_yaml(Util.read_text_file('data/test_plan.yml'))

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

    def need_wait(self, cfg_info):
        if (cfg_info.get('local') and cfg_info.get('remote')):
            return True
        return False

    def do_test(self, plan):
        outside = plan['outside']
        outside['l10n_version'] = VERSION
        cfg = I18N.add_config_file(plan['config'], outside)
        self.assertEqual(cfg.get_info()['version'], VERSION)

        start = time.time()
        I18N.set_current_locale(LOCALE)
        I18N.set_current_locale(LOCALE)
        current = I18N.get_current_locale()
        print('--- current --- %s ---' % current)
        self.assertEqual(current, 'de')

        rel = I18N.get_release(plan['outside']['product'], VERSION)

        cfg = rel.get_config()
        cfg_info = cfg.get_info()
        self.show('config', 'info', Util.dict2string(cfg_info))

        trans = rel.get_translation()
        self.check_locale(trans, 'ZH_cn')
        self.check_locale(trans, 'EN_us')

        Util.run_test_data(self, trans, 'TestShowCache')

        if cfg_info['pseudo']:
            if cfg_info['remote']:
                Util.run_test_data(self, trans, 'TestGetStringPseudoOnline')
                if cfg_info['local']:
                    Util.run_test_data(self, trans, 'TestGetStringPseudoOnlineWithLocal')
                else:
                    Util.run_test_data(self, trans, 'TestGetStringPseudoOnlineOnly')
            else:
                Util.run_test_data(self, trans, 'TestGetStringPseudoOffline')
            Util.run_test_data(self, trans, 'TestShowCache')
            print('--- test --- end ---')
            return

        Util.run_test_data(self, trans, 'TestGetString1')
        Util.run_test_data(self, trans, 'TestGetString1T')
        if Util.is_async_supported():
            Util.run_test_data(self, trans, 'TestGetString1A')
        Util.run_test_data(self, trans, 'TestGetString2')

        if cfg_info['remote'] is None and cfg._config_data.get('components') is not None:
            Util.run_test_data(self, trans, 'TestGetStringOffline')

        groupName = 'TestGetStringSameLocale'
        if cfg.get_info()['default_locale'] != cfg.get_info()['source_locale']:
            groupName = 'TestGetStringDifferentLocale'
        Util.run_test_data(self, trans, groupName)
        Util.run_test_data(self, trans, 'TestGetStringTemp')

        I18N.set_current_locale(LOCALE)
        found = trans.get_string('TT', 'about.title')
        print('--- found --- wrong component --- %s ---' % found)

        if self.need_wait(cfg_info):
            time.sleep(5)

        found = trans.get_string(None, 'about.title', format_items = ['11', '22'])
        print('--- found --- format in array --- %s ---' % found)
        self.assertEqual(found, 'Über Version 22 of Product 11')
        found = trans.get_string(None, 'about.title2', format_items = {'x': 'ee', 'y': 'ff'})
        print('--- found --- format in dict --- %s ---' % found)
        self.assertEqual(found, 'Über Version ee of Product ff')

        if self.need_wait(cfg_info):
            Util.run_test_data(self, trans, 'TestGetString3')

        Util.run_test_data(self, trans, 'TestShowCache')

        spent = time.time() - start
        time.sleep(1)
        print('--- test --- end --- %s ---' % spent)

    def prepare_before(self):
        FileUtil.dir_map['../data/l10n'] = {'about', 'contact'}

        Util.load_test_data(['data/test_prepare.txt', 'data/test_define_before.txt'])
        Util.run_test_data(self, None, 'TestLoadBeforeService')

        return _plans_pool['plans_before']

    def prepare_pseudo(self):
        FileUtil.dir_map['../data/l10n'] = {'about', 'aboutadd', 'contact'}

        Util.load_test_data(['data/test_prepare.txt', 'data/test_define_pseudo.txt'])
        Util.run_test_data(self, None, 'TestLoadPseudoService')

        return _plans_pool['plans_pseudo']

    def prepare(self):
        FileUtil.dir_map['../data/l10n'] = {'about', 'aboutadd', 'contact'}

        Util.load_test_data(['data/test_prepare.txt', 'data/test_define.txt'])
        Util.run_test_data(self, None, 'TestLoadService')

        return _plans_pool['plans']

    def run_plans(self, plans, index):
        Util.run_test_data(self, None, 'TestDelay1')

        if index is not None:
            self.do_test(plans[index])
        else:
            for i in range(len(plans)):
                self.do_test(plans[i])

    def test_api(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        NetUtil.simulate = TestSimulate(False)

        print('--- test start ---')

        self.prepare_sub_path('log')
        self.prepare_sub_path('singleton')

        self.run_plans(self.prepare_before(), None)
        self.run_plans(self.prepare_pseudo(), None)
        self.run_plans(self.prepare(), None)

        if NetUtil.simulate and NetUtil.simulate.is_record_enabled():
            time.sleep(5)
            FileUtil.save_json_file('data/simulate.json', NetUtil.simulate.record)


if __name__ == '__main__':
    unittest.main()
