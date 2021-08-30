# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import re


class SingletonLocale(object):

    def __init__(self, locale):
        self._localeList = [locale]

    def get_near_locale_list(self):
        return self._localeList

    def add_near_locale(self, locale):
        if locale in self._localeList:
            return False

        self._localeList.append(locale)
        return True

    def get_count(self):
        return len(self._localeList)

    def get_near_locale(self, index):
        if index < 0 or index >= self.get_count():
            return None
        return self._localeList[index]

    def get_original_locale(self):
        return self.get_near_locale(0)

    def get_relate_locale(self, checkList):
        if checkList is None:
            return None

        for one in checkList:
            temp = SingletonLocaleUtil.get_singleton_locale(one)
            if self.compare(temp):
                return temp

        return None

    def compare(self, singletonLocale):
        if singletonLocale is None:
            return False

        return self.is_in_locale_list(singletonLocale.get_near_locale_list())

    def is_in_locale_list(self, checkList):
        if checkList is None:
            return False

        for i in range(self.get_count()):
            if self.get_near_locale(i) in checkList:
                return True

        return False

    def find_item(self, items, start):
        for i in range(start, self.get_count()):
            nearLocale = self.get_near_locale(i)
            item = items.get(nearLocale)
            if item:
                return item
        return None

    def set_items(self, items, item):
        for i in range(self.get_count()):
            nearLocale = self.get_near_locale(i)
            items[nearLocale] = item


class SingletonLocaleUtil(object):

    DEFAULT_LOCALE = "en-US"
    FALLBACK = {
      'zh-CN': 'zh-Hans',
      'zh-TW': 'zh-Hant',
      'zh-HANS': 'zh-Hans',
      'zh-HANT': 'zh-Hant'
    }

    _localeFallbackMap = {}

    @classmethod
    def get_singleton_locale(cls, locale):
        if locale is None:
            return cls.get_singleton_locale(SingletonLocaleUtil.DEFAULT_LOCALE)

        singletonLocale = cls._localeFallbackMap.get(locale.lower())
        if singletonLocale:
            return singletonLocale

        parts = re.split(r'[\_|\-]', locale)
        parts[0] = parts[0].lower()
        if len(parts) > 1:
            parts[1] = parts[1].upper()
        if len(parts) > 2:
            parts = parts[0:2]
        original = '-'.join(parts)
        singletonLocale = SingletonLocale(locale)
        singletonLocale.add_near_locale(original)
        fallback = cls.FALLBACK.get(original)
        if fallback:
            singletonLocale.add_near_locale(fallback)
        elif len(parts) > 1:
            singletonLocale.add_near_locale(parts[0])

        cls._localeFallbackMap[locale.lower()] = singletonLocale
        return singletonLocale
