# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import unittest

sys.path.append('../sgtnclient')
from sgtn_util import FileUtil, NetUtil, SysUtil
from sgtn_locale import SingletonLocaleUtil
from sgtn_debug import SgtnDebug

from util import Util, TestSimulate
import I18N


class TestClient(unittest.TestCase):

    def test_file_util(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        text = Util.read_text_file('data/data_utf8.txt')
        self.assertIn('cc=AA{x}BB{y}CC', text)
        
        dt = FileUtil.read_json_file('data/data.json')
        self.assertEqual(dt['aa'], 'aaa')
        print('--- json --- %s ---' % dt)

        dt['add'] = 'über'
        FileUtil.save_json_file('./log/data2.json', dt)
        dtLoad = FileUtil.read_json_file('./log/data2.json')
        self.assertEqual(dtLoad['add'], 'über')
        
        dt = FileUtil.read_datatree('config/sgtn_online_only.yml')
        self.assertEqual(dt['cache_type'], 'by_key')
        print('--- yaml --- %s ---' % dt['cache_type'])
        
        dir_list, file_list = FileUtil.get_dir_info('data')
        print('--- dir_list --- %s ---' % dir_list)
        print('--- file_list --- %s ---' % len(file_list))
        self.assertIn('http_response.txt', file_list)

        #SgtnDebug.set_internal_log('./log/debug.txt')
        SgtnDebug.log_text('add', 'aaa')
        SgtnDebug.log_text('add', 'bbb')
        SgtnDebug.log_text('add', {'aa': 'aaa'})

    def test_net_util(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        NetUtil.simulate = TestSimulate(False)
        NetUtil.simulate.simulate_data = Util.load_response(['data/http_response.txt'])

        dt = FileUtil.read_datatree('config/sgtn_online_only.yml')
        online_url = dt['online_service_url']
        parts = online_url.split('/')[:3]
        parts.append('i18n/api/v2/translation/products/PYTHON1/versions/1.0.0/localelist')
        url = '/'.join(parts)
        
        text = NetUtil.http_get_text(url)
        self.assertIn('productName', text)
        
        code, dt = NetUtil.http_get(url, None)
        self.assertEqual(code, 200)
        self.assertIn('data', dt['result'])

        etag, max_age = NetUtil.get_etag_maxage(dt['headers'])
        headers = {'If-None-Match': etag}        
        code, dt = NetUtil.http_get(url, headers)
        self.assertEqual(code, 304)

    def test_sys_util(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        locale = SysUtil.get_fallback_locale('ZH_cn')
        print('--- locale --- %s ---' % locale)
        self.assertEqual(locale, 'zh-Hans')

        locale = SysUtil.get_fallback_locale('EN_us')
        print('--- locale --- %s ---' % locale)
        self.assertEqual(locale, 'en')

        singletonLocale = SingletonLocaleUtil.get_singleton_locale('zh_Hans_CN')
        self.assertEqual(singletonLocale.get_near_locale(1), 'zh-HANS')


if __name__ == '__main__':
    unittest.main()
