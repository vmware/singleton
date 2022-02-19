# -*-coding:UTF-8 -*-
#
# Copyright 2020-2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import time
import json
import sys

sys.path.append('../sgtnclient')
import I18N
#from sgtnclient import I18N

PRODUCT = 'PYTHON'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'de'

CONFIG_FILES = [
    'sample_online_only.yml',
    'sample_online_localsource.yml',
    'sample_offline_disk.yml',
    'sample_offline_remote.yml',
    'sample_offline_remote_original.yml'
    ]
CONFIG_INDEX = 0


class SampleApplication():

    def prepare_sub_path(self, sub):
        self.code_path = os.path.dirname(__file__)
        print('--- code path --- %s' % self.code_path)

        sub_path = os.path.join(self.code_path, sub)
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

    def use_icu(self, cfg, trans):
        _support_icu = False
        try:
            from icu import Locale
            _temp = Locale('en')
            _support_icu = True
        except:
            return

        if cfg.format_type != 'icu':
            return
        print('--- use icu ---')
        txt = trans.format('en', 'I bought {0, plural, one {# book} other {# books}}.', [12])
        print('--- plural --- %s ---' % txt)
        txt = trans.get_string('about', 'icu.plural1', locale = 'en-US', format_items = [1])
        print('--- plural --- %s ---' % txt)
        print('--- end icu ---')

    def main(self):

        KEY = 'about.title'
        SOURCE = 'About'

        print('--- start sample ---')

        self.prepare_sub_path('log')
        self.prepare_sub_path('singleton')

        #
        # step 1
        #     Initialize configuration file
        #
        I18N.add_config_file(os.path.join(self.code_path, CONFIG_FILES[CONFIG_INDEX]))

        start = time.time()
        I18N.set_current_locale(LOCALE)
        current = I18N.get_current_locale()
        print('--- current --- %s ---' % current)

        #
        # step 2
        #     Get release object
        #
        rel = I18N.get_release(PRODUCT, VERSION)

        cfg = rel.get_config()
        #self.show('config', 'data', self.dict2string(cfg.get_config_data()))
        cfg_info = cfg.get_info()
        self.show('config', 'info', self.dict2string(cfg_info))

        #
        # step 3
        #     Get translation object
        #
        trans = rel.get_translation()
        self.check_locale(trans, 'ZH_cn')
        self.check_locale(trans, 'EN_us')
        self.use_icu(cfg, trans)

        #
        # step 4
        #     Set global locale
        #
        I18N.set_current_locale(LOCALE)

        #
        # step 5
        #     Get translation messages by get_string()
        #
        found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = 'en-US')
        print('--- found --- source --- %s ---' % found)
        found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
        print('--- found --- %s --- %s ---' % (LOCALE, found))
        found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = 'hh')
        print('--- found --- %s --- %s ---' % ('hh', found))
        found = trans.get_string(COMPONENT, KEY, source = SOURCE)
        print('--- found --- current --- %s ---' % found)
        found = trans.get_string(COMPONENT, KEY)
        print('--- found --- only by key --- %s ---' % found)
        found = trans.get_string('TT', KEY)
        print('--- found --- wrong component --- %s ---' % found)

        _locales = trans.get_locales('zh-CN')
        print('--- locales --- %s' % self.dict2string(_locales))

        if (self.need_wait(cfg_info)):
            for i in range(10):
                time.sleep(5)
                found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
                print('--- found --- %s --- %s --- %s ---' % (i, LOCALE, found))

        found = trans.get_string(COMPONENT, 'aa', format_items = ['11', '22'])
        print('--- found --- array in format --- %s ---' % found)
        found = trans.get_string(COMPONENT, 'cc', format_items = {'x': 'ee', 'y': 'ff'})
        print('--- found --- dict in format --- %s ---' % found)

        spent = time.time() - start

        data = trans.get_locale_strings('en-US', True)
        print('--- source --- en-US --- %s ---' % data)

        data = trans.get_locale_strings('en-US', False)
        print('--- translate --- en-US --- %s ---' % data)

        data = trans.get_locale_strings(LOCALE, False)
        print('--- translate --- %s --- %s ---' % (LOCALE, data))

        if (self.need_wait(cfg_info)):
            found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
            print('--- found --- wait --- %s ---' % found)

        print('--- sample --- end --- %s ---' % spent)


SampleApplication().main()
