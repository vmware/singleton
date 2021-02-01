# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import time
import json

import sys
sys.path.append('..')
from sgtn_client import I18N, Release, Translation


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
        I18N.add_config_file(CONFIG_FILES[CONFIG_INDEX])

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

        if (self.need_wait(cfg_info)):
            time.sleep(5)

        found = trans.get_string(COMPONENT, 'aa', format_items = ['11', '22'])
        print('--- found --- array in format --- %s ---' % found)
        found = trans.get_string(COMPONENT, 'cc', format_items = {'x': 'ee', 'y': 'ff'})
        print('--- found --- dict in format --- %s ---' % found)

        spent = time.time() - start

        data = trans.get_locale_strings('en-US')
        #print('--- source --- 2 --- %s ---' % data)

        data = trans.get_locale_strings('de')
        #print('--- source --- 0 --- %s ---' % data)

        if (self.need_wait(cfg_info)):
            found = trans.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
            print('--- found2 --- 4 --- %s ---' % found)

        print('--- sample --- end --- %s ---' % spent)


SampleApplication().main()
