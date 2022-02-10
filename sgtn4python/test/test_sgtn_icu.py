# -*-coding:UTF-8 -*-
#
# Copyright 2020-2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import unittest

import sys
sys.path.append('../sgtnclient')
import sgtn_icu

_support_icu = False
try:
    from icu import Locale
    _temp = Locale('en')
    _support_icu = True
except:
    pass


class TestIcu(unittest.TestCase):

    def test_api(self):
        checked = sgtn_icu.sgtn_is_icu_available()
        print('--- support icu --- {0} ---'.format(checked))
        self.assertEqual(checked, _support_icu)
        if not checked:
            txt = sgtn_icu.sgtn_icu_format('de', 'abc {0} efg {1}', [22, 'tt'])
            self.assertEqual('abc 22 efg tt', txt)
            return

        txt = sgtn_icu.sgtn_icu_format('de', 'abc {0} efg {1}', [11, 'ss'])
        self.assertEqual('abc 11 efg ss', txt)

        txt = sgtn_icu.sgtn_icu_format('de-DE', 'money {0,number,\u00A4#}', [23])
        self.assertEqual('money €23', txt)

        txt = sgtn_icu.sgtn_icu_format('en', 'I bought {0, plural, one {# book} other {# books}}.', [12])
        self.assertEqual('I bought 12 books.', txt)

        txt = sgtn_icu.sgtn_icu_format('en', 'I bought {0, plural, one {# book} other {# books}}.', [1])
        self.assertEqual('I bought 1 book.', txt)


if __name__ == '__main__':
    unittest.main()

