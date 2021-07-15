# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import re
import json
import copy
import time
import logging
import threading
from threading import Thread
from collections import OrderedDict

sys.path.append('../sgtnclient')
if sys.version_info.major == 2:
    # Support utf8 text
    reload(sys)
    sys.setdefaultencoding('utf-8')

from sgtn_util import FileUtil, NetUtil, SysUtil
from sgtn_properties import Properties

import I18N


def init_logger():
    _logger = logging.getLogger('test')
    _logger.setLevel(logging.DEBUG)
    handler = logging.StreamHandler()
    _logger.addHandler(handler)
    _logger.info('\n--- start --- python %s ---' % sys.version_info.major)
    return _logger

logger = init_logger()

allTestData = {}
lock = threading.Lock()


class TestThread(Thread):

    def __init__(self, idThread, ut, trans, group, needPrint, times):
        Thread.__init__(self)

        self.idThread = idThread
        self.ut = ut
        self.trans = trans
        self.group = group
        self.needPrint = needPrint
        self.times = times

    def do_one_getstring(self, item, needPrint):
        key = item['key']
        source = item.get('source')
        component = item.get('component')
        locale = item.get('locale')
        if not locale:
            locale = I18N.get_current_locale()
        expect = item.get('expect')

        found = self.trans.get_string(component, key, locale = locale, source = source)
        if found != expect:
            print('--- error --- %s' % self.ut.dict2string(item))
        self.ut.assertEqual(found, expect)
        if needPrint:
            logger.info('--- [%s]%s --- %s --- %s --- %s --- %s' % (
                self.group['NAME'], self.idThread, component, key, locale, expect))

    def end_test(self):
        with lock:
            if self.group['_running'] > 0:
                self.group['_running'] -= 1

    def do_one_item(self, one, needPrint):
        if one['type'] == 'GetString':
            self.do_one_getstring(one, needPrint)
            return self.group['_interval']
        elif one['type'] == 'SetLocale':
            I18N.set_current_locale(one['locale'])
        return 0

    def do_test(self):
        time.sleep(self.idThread % 2 * 0.1)
        for i in range(self.times):
            needPrint = (i == 0) and self.needPrint
            for one in self.group['tests']:
                delay = self.do_one_item(one, needPrint)
                if delay > 0:
                    time.sleep(self.group['_interval']*0.001)
        self.end_test()

    def run(self):
        self.do_test()


def _load_response(response, text):
    parts = re.split('---data---.*[\r|\n]*', text)
    segs = re.split('---header---.*[\r|\n]*', parts[0])
    lines = re.split('\n', segs[0])
    url = lines[0].strip()[5:]
    if len(segs) > 1 and len(segs[1].strip()) > 2:
        header_part = json.loads(segs[1])
        if header_part:
            tail = json.dumps(header_part, ensure_ascii = False)
            url = '%s<<headers>>%s' % (url, tail)

    segs = re.split('---header---.*[\r|\n]*',  parts[1])
    response[url] = {'text': segs[0], 'headers': {}}
    if len(segs) > 1 and len(segs[1].strip()) > 2:
        pieces = re.split('---code---.*[\r|\n]*',  segs[1])
        response[url]['headers'] = json.loads(pieces[0])
        if len(pieces) > 1:
            response[url]['code'] = int(pieces[1])


def _load_test_data(text):
    global allTestData
    parser = Properties()

    parts = re.split('---test---.*[\r|\n]*', text)
    for testData in parts:
        if len(testData.strip()) > 5:
            segs = re.split('---data---.*[\r|\n]*', testData);
            dt = parser.parse(segs[0])

            allTestData[dt['NAME']] = dt
            dt['tests'] = []
            for i in range(1, len(segs)):
                dtTest = parser.parse(segs[i])
                dt['tests'].append(dtTest)


class Util(object):

    @staticmethod
    def load_response(files):
        response = {}

        for i in range(9):
            product = 'PYTHON%s' % (i+1)
            version = '1.0.0'

            for one in files:
                text = FileUtil.read_text_file(one)
                text = text.replace('$PRODUCT', product).replace('$VERSION', version)
                parts = re.split('---api---.*[\r|\n]*', text)

                for apiData in parts:
                    if len(apiData.strip()) > 5:
                        _load_response(response, apiData)
        return response

    @staticmethod
    def load_test_data(files):
        for one in files:
            text = FileUtil.read_text_file(one)
            _load_test_data(text)

    @staticmethod
    def is_async_supported():
        return sys.version_info.major * 1000 + sys.version_info.minor >= 3005

    @staticmethod
    def run_test_data(ut, trans, groupName):
        global allTestData
        group = allTestData.get(groupName)
        if not group:
            return
        group = copy.deepcopy(group)
        if 'DATAFROM' in group:
            group['tests'] = copy.deepcopy(allTestData.get(group['DATAFROM'])['tests'])

        start = time.time()
        times = int(group['TIMES']) if 'TIMES' in group else 1
        threads = int(group['THREAD']) if 'THREAD' in group else 0
        asyncNum = int(group['ASYNC']) if 'ASYNC' in group else 0
        group['_interval'] = int(group['INTERVAL']) if 'INTERVAL' in group else 0
        print('--- group --- %s --- %s ---' % (groupName, times))

        if threads == 0 and asyncNum == 0:
            group['_running'] = 0
            tt = TestThread(0, ut, trans, group, True, times)
            tt.do_test()
        else:
            cut = threads if threads > 0 else asyncNum
            group['_running'] = cut
            count = times // cut
            batches = []
            for i in range(cut):
                needPrint = (i==0) or (i==cut-1)
                tt = TestThread(i+1, ut, trans, group, needPrint, count)
                batches.append(tt)

            if threads == 0 and Util.is_async_supported():
                from async_util import AsyncWork
                AsyncWork().test(group, batches)
            else:
                for tt in batches:
                    tt.start()

            while group['_running'] > 0:
                time.sleep(0.001)
            mtype = 'thread' if asyncNum == 0 else 'async'
            print('--- %s--- %s --- over ---' % (groupName, mtype))

        if times > 1 and group['_interval'] == 0:
            print('--- time span --- %s(s) --- %s ---' % (time.time() - start, times * len(group['tests'])))

