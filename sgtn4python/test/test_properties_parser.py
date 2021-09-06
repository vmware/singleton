# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import unittest

sys.path.append('../sgtnclient')

from sgtn_properties import Properties
from sgtn_util import FileUtil

from util import Util


class TestClient(unittest.TestCase):

    def test_properties_parser(self):
        print('\n--- unittest --- %s --- python %s\n' % (
            sys._getframe().f_code.co_name, sys.version_info.major))

        text = u'qq1=qqq\n#the middle one\nbb2:bbb #tail\ncc3 abc好abc好'
        print(text)
        
        p = Properties()
        m = p.parse(text)

        self.assertEqual(m['bb2'], 'bbb #tail')
        print('--- map1 --- %s ---' % m)

        import json
        t = json.dumps(m, indent=2, ensure_ascii=False)
        print(t)

        text = Util.read_text_file('data/data_utf8.txt')
        m = p.parse(text)

        self.assertEqual(m['username'], u'Username用户名')
        print('--- map2 --- %s ---' % m)

        text = Util.read_text_file('data/data_ascii.txt')
        m = p.parse(text)

        self.assertEqual(m['username'], u'Username用户名')
        print('--- map3 --- %s ---' % m)


if __name__ == '__main__':
    unittest.main()
