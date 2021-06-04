# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import re
import json
from collections import OrderedDict

sys.path.append('../sgtnclient')
if sys.version_info.major == 2:
    # Support utf8 text
    reload(sys)
    sys.setdefaultencoding('utf-8')

from sgtn_util import FileUtil, NetUtil, SysUtil
from sgtn_properties import Properties
    
allTestData = {}


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

def _test_one(ut, trans, one):
    key = one['key']
    source = one.get('source')
    component = one.get('component')
    locale = one.get('locale')
    expect = one.get('expect')

    found = trans.get_string(component, key, locale = locale)
    ut.assertEqual(found, expect)
    print('--- ok --- %s --- %s --- %s --- %s' % (component, key, locale, expect))


class Util(object):
    
    @staticmethod
    def load_response(files):
        response = {}
        
        for i in range(2):
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
    def run_test_data(ut, trans, groupName):
        global allTestData
        group = allTestData.get(groupName)
        if not group:
            return 
        for one in group['tests']:
            if one['type'] == 'GetString':
                _test_one(ut, trans, one)
        
