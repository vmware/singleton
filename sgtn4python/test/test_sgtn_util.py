# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import unittest

sys.path.append('..')
if sys.version_info.major == 2:
    # Support utf8 text
    reload(sys)
    sys.setdefaultencoding('utf-8')

from sgtn_util import FileUtil, NetUtil, SysUtil


class TestClient(unittest.TestCase):

    def test_file_util(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        text = FileUtil.read_text_file('./data.txt')
        self.assertIn('cc=AA{x}BB{y}CC', text)
        
        dt = FileUtil.read_json_file('./data.json')
        self.assertEqual(dt['aa'], 'aaa')
        print('--- json --- %s ---' % dt)

        dt['add'] = 'über'
        FileUtil.save_json_file('./log/data2.json', dt)
        dtLoad = FileUtil.read_json_file('./log/data2.json')
        self.assertEqual(dtLoad['add'], 'über')
        
        dt = FileUtil.read_datatree('./sgtn_client.yml')
        self.assertEqual(dt['log_path'], './log/')
        print('--- yaml --- %s ---' % dt['log_path'])
        
        dir_list, file_list = FileUtil.get_dir_info('.')
        print('--- dir_list --- %s ---' % dir_list)
        print('--- file_list --- %s ---' % len(file_list))
        self.assertIn('sgtn_client.yml', file_list)

    def test_net_util(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        NetUtil.simulate_data = FileUtil.read_json_file('./simulate.json')

        dt = FileUtil.read_datatree('./sgtn_client.yml')
        online_url = dt['online_service_url']
        parts = online_url.split('/')[:3]
        parts.append('i18n/api/v2/translation/products/PYTHON/versions/1.0.0/localelist')
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


if __name__ == '__main__':
    unittest.main()
