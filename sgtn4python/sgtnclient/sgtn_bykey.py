# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

from collections import OrderedDict
import threading
lock = threading.Lock()

from sgtn_locale import SingletonLocale, SingletonLocaleUtil

_indexLocaleItem = 0


class SingletonByKeyItem(object):

    def __init__(self, componentIndex, itemIndex):
        self._componentIndex = componentIndex

        self._pageIndex = itemIndex // SingletonByKey.PAGE_MAX_SIZE
        self._indexInPage = itemIndex % SingletonByKey.PAGE_MAX_SIZE

        self._sourceStatus = 0x01;
        self._next = None


class SingletonByKeyTable(object):

    def __init__(self, max):
        self._max = max
        self._table = [None] * max

    def get_page(self, id):
        return self._table[id]

    def new_page(self, id):
        array = [None] * self._max
        self._table[id] = array
        return array

    def get_item(self, pageIndex, indexInPage):
        array = self.get_page(pageIndex)
        if array is None:
            return None
        return array[indexInPage]

    def set_item(self, pageIndex, indexInPage, item):
        array = self.get_page(pageIndex)
        if array is None:
            array = self.new_page(pageIndex)

        array[indexInPage] = item

    def get_item_by_one_index(self, index):
        pageIndex = index // self._max
        indexInPage = index % self._max
        return self.get_item(pageIndex, indexInPage)

    def set_item_by_one_index(self, index, item):
        pageIndex = index // self._max
        indexInPage = index % self._max
        self.set_item(pageIndex, indexInPage, item)


class SingletonByKeyComponents(object):

    def __init__(self):
        self._count = 0
        self._componentTable = []
        self._componentIndexTable = {}

    def get_id(self, component):
        if not component:
            return -1

        componentIndex = self._componentIndexTable.get(component)
        if componentIndex != None:
            return componentIndex

        self._componentTable.append(component)
        self._componentIndexTable[component] = self._count
        self._count += 1
        return self._count - 1

    def get_name(self, id):
        if id < 0 or id >= self._count:
            return None

        return _componentTable[id]


class SingletonByKeyLocale(object):

    def __init__(self, bykey, locale, asSource):
        global _indexLocaleItem
        _indexLocaleItem += 1
        self._indexLocaleItem = _indexLocaleItem

        self._bykey = bykey
        self._locale = locale
        self._singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
        self._asSource = asSource
        self._isSourceLocale = self._singletonLocale.compare(bykey._singletonLocaleSource)

        self._messages = SingletonByKeyTable(SingletonByKey.PAGE_MAX_SIZE)
        self._components = SingletonByKeyTable(SingletonByKey.COMPONENT_PAGE_MAX_SIZE)

    def check_task(self, componentIndex, needCheck):
        if componentIndex >= 0 and needCheck:
            componentObj = self._components.get_item_by_one_index(componentIndex)
            if componentObj != None and componentObj.task != None:
                componentObj.task.check()

    def get_message(self, componentIndex, pageIndex, indexInPage, needCheck = True):
        self.check_task(componentIndex, needCheck)
        return self._messages.get_item(pageIndex, indexInPage)

    def set_message(self, message, componentObject, componentIndex, pageIndex, indexInPage):
        if componentObject:
            self._components.set_item_by_one_index(componentIndex, componentObject)

        self._messages.set_item(pageIndex, indexInPage, message)
        return True;


class SingletonLookup(object):

    def __init__(self, key, componentIndex, message):
        self._key = key
        self._componentIndex = componentIndex
        self._message = message

        self._add = 0
        self._aboveItem = None
        self._currentItem = None


class SingletonByKey(object):

    PAGE_MAX_SIZE = 1024;
    COMPONENT_PAGE_MAX_SIZE = 128;

    def __init__(self, localeSource, localeDefault, isDifferent, cacheType):
        self._itemCount = 0
        self._keyAttrTable = {}
        self._items = SingletonByKeyTable(SingletonByKey.PAGE_MAX_SIZE)
        self._componentTable = SingletonByKeyComponents()

        self._singletonLocaleSource = SingletonLocaleUtil.get_singleton_locale(localeSource)

        self._sources = {}
        self._locales = {}
        self._onlyByKey = (cacheType == 'by_key')

        self._isDifferent = isDifferent
        self._sourceLocal = None
        self._sourceRemote = None

        self._defaultLocale = localeDefault
        self._defaultRemote = None

    def set_item(self, item, pageIndex, indexInPage):
        self._items.set_item(pageIndex, indexInPage, item)
        return True

    def get_and_add_itemcount(self):
        count = self._itemCount
        self._itemCount += 1
        return count

    def get_locale_item(self, locale, asSource):
        table = self._sources if asSource else self._locales
        item = table.get(locale)
        if item is None:
            singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
            for oneLocale in table:
                oneSingletonLocale = SingletonLocaleUtil.get_singleton_locale(oneLocale)
                if singletonLocale.compare(oneSingletonLocale):
                    item = table[oneLocale]
                    break
            if item is None:
                item = SingletonByKeyLocale(self, locale, asSource)
            table[locale] = item
        return item

    def get_component_index(self, component):
        return self._componentTable.get_id(component)

    def get_string(self, key, componentIndex, localeItem, needFallback = False):
        if componentIndex < 0 and not self._onlyByKey:
            return None

        item = self._keyAttrTable.get(key)

        if componentIndex >= 0:
            while item:
                if item._componentIndex == componentIndex:
                    break
                item = item._next

        if item is None:
            localeItem.check_task(componentIndex, needFallback)
            return None

        if not needFallback:
            message = localeItem.get_message(componentIndex, item._pageIndex, item._indexInPage, False)
            return message

        message = None
        if item._sourceStatus & 0x01 == 0x01:
            message = localeItem.get_message(componentIndex, item._pageIndex, item._indexInPage)
            if message != None:
                return message

        if self._isDifferent:
            if not self._defaultRemote:
                self._defaultRemote = self.get_locale_item(self._defaultLocale, False)
            message = self._defaultRemote.get_message(componentIndex, item._pageIndex, item._indexInPage)

        if message is None:
            if item._sourceStatus & 0x04 == 0x04:
                message = self._sourceLocal.get_message(componentIndex, item._pageIndex, item._indexInPage)
            elif item._sourceStatus & 0x03 == 0x03:
                message = self._sourceRemote.get_message(componentIndex, item._pageIndex, item._indexInPage)

        if message is None:
            message = key
        return message

    def new_key_item(self, componentIndex):
        itemIndex = self.get_and_add_itemcount()
        item = SingletonByKeyItem(componentIndex, itemIndex)
        self.set_item(item, item._pageIndex, item._indexInPage)
        return item

    def _find_or_add(self, lookup):
        item = self._keyAttrTable.get(lookup._key)
        if item is None: # This is new
            lookup._currentItem = self.new_key_item(lookup._componentIndex)
            lookup._add = 1
            return

        while item:
            if item._componentIndex == lookup._componentIndex: # Found
                lookup._currentItem = item
                return
            lookup._aboveItem = item
            item = item._next

        lookup._currentItem = self.new_key_item(lookup._componentIndex)
        lookup._add = 2

    def do_set_string(self, key, componentObject, componentIndex, localeItem, message):
        lookup = SingletonLookup(key, componentIndex, message)
        self._find_or_add(lookup)

        item = lookup._currentItem
        if item is None:
            return False

        done = localeItem.set_message(message, componentObject, componentIndex, item._pageIndex, item._indexInPage)
        if done and localeItem._isSourceLocale:
            status = item._sourceStatus
            if localeItem._asSource:
                self._sourceLocal = localeItem;
                status |= 0x04
            elif localeItem._isSourceLocale:
                self._sourceRemote = localeItem
                status |= 0x02

            if (status & 0x06) != 0x06:
                status |= 0x01
            else:
                localSource = self._sourceLocal.get_message(componentIndex, item._pageIndex, item._indexInPage, False)
                remoteSource = self._sourceRemote.get_message(componentIndex, item._pageIndex, item._indexInPage, False)
                if localSource == remoteSource:
                    status |= 0x01
                else:
                    status &= 0x06
            item._sourceStatus = status

        # Finally, it's added in the table after it has been prepared to keep reading correct.
        if lookup._add == 1:
            self._keyAttrTable[key] = lookup._currentItem
        elif lookup._add == 2:
            lookup._aboveItem._next = lookup._currentItem
        return done;

    def set_string(self, key, componentObject, componentIndex, localeItem, message):
        if message is None or key is None or localeItem is None:
            return False

        text = self.get_string(key, componentIndex, localeItem)
        if message != text:
            with lock:
                text = self.get_string(key, componentIndex, localeItem)
                if message != text:
                    return self.do_set_string(key, componentObject, componentIndex, localeItem, message)
        return False

    def get_key_item(self, pageIndex, indexInPage):
        array = self._items.get_page(pageIndex)
        if array is None:
            return None
        return array[indexInPage]

    def get_messages(self, componentIndex, localeItem):
        messages = OrderedDict()
        if componentIndex >= 0 and localeItem:
            pages = {}
            for i in range(SingletonByKey.PAGE_MAX_SIZE):
                array = localeItem._messages.get_page(i)
                if array is None:
                    continue

                for k in range(SingletonByKey.PAGE_MAX_SIZE):
                    text = array[k]
                    if text != None:
                        item = self.get_key_item(i, k)
                        if item:
                            if i not in pages:
                                pages[i] = {}
                            pages[i][k] = ''

            for key in self._keyAttrTable:
                item = self._keyAttrTable.get(key)
                if item._pageIndex in pages:
                    array = pages[item._pageIndex]
                    if item._indexInPage in array:
                        messages[key] = self.get_string(key, componentIndex, localeItem)
        return messages
