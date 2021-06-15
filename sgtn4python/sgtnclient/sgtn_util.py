# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import sys
import json
import re
import logging
from collections import OrderedDict
from sgtn_properties import Properties

import ssl
if hasattr(ssl, '_create_unverified_context'):  # for python 2.7
    ssl._create_default_https_context = ssl._create_unverified_context

PY_VER = sys.version_info.major
UTF8 = 'utf-8'

if PY_VER == 2:
    import urllib2 as httplib

    # Support utf8 text
    reload(sys)
    sys.setdefaultencoding(UTF8)
else:
    import urllib.request as httplib

KEY_RESULT = 'result'
KEY_HEADERS = 'headers'
KEY_ERROR = 'error'

LOG_TYPE_INFO = 'info'
LOG_TYPE_ERROR = 'error'

# below keys are in lower case
LOCALE_MAP = {
    'zh-hant': 'zh-Hant',
    'zh-tw': 'zh-Hant',
    'zh-hans': 'zh-Hans',
    'zh-cn': 'zh-Hans'
    }

class FileUtil:

    @classmethod
    def read_text_file(cls, file_name):
        if os.path.exists(file_name) and os.path.isfile(file_name):
            f = open(file_name, 'rb')
            file_data = f.read()
            f.close()

            try:
                file_data = file_data.decode(UTF8)
                return file_data
            except Exception as e:
                return None
        return None

    @classmethod
    def parse_json(cls, text):
        if text:
            try:
                dict_data = json.loads(text, object_pairs_hook = OrderedDict)
                return dict_data
            except Exception as e:
                return None
        return None

    @classmethod
    def parse_yaml(cls, text):
        if text:
            try:
                import yaml
                dict_data = yaml.load(text, Loader = yaml.FullLoader)
                return dict_data
            except Exception as e:
                return None
        return None

    @classmethod
    def parse_datatree(cls, text):
        if text:
            data = cls.parse_yaml(text)
            if data is None:
                data = cls.parse_json(text)
            return data
        return None

    @classmethod
    def read_json_file(cls, file_name):
        file_data = cls.read_text_file(file_name)
        return cls.parse_json(file_data)

    @classmethod
    def read_datatree(cls, file_name):
        file_data = cls.read_text_file(file_name)
        return cls.parse_datatree(file_data)

        return cls.read_json_file(file_name)

    @classmethod
    def save_json_file(cls, file_name, dict):
        dir = os.path.dirname(file_name)
        if not os.path.exists(dir):
            os.makedirs(dir)
        if PY_VER == 2:
            f = open(file_name, 'w')
        else:
            f = open(file_name, 'w', encoding='utf-8')
        text = json.dumps(dict, ensure_ascii = False, indent = 2)
        f.write(text)
        f.close()

    @classmethod
    def get_dir_info(cls, dir_name):
        dir_list = []
        file_list = []

        try:
            ls = os.listdir(dir_name)
        except:
            pass
        else:
            for fn in ls:
                temp = os.path.join(dir_name, fn)
                if (os.path.isdir(temp)):
                    dir_list.append(fn)
                else:
                    file_list.append(fn)
        return dir_list, file_list

class NetUtil:

    simulate_data = None
    record_data = None

    @classmethod
    def _get_data(cls, url, request_headers):
        if not cls.simulate_data:
            req = httplib.Request(url)
            if request_headers:
                for key in request_headers:
                    req.add_header(key, request_headers[key])
            res_data = httplib.urlopen(req)

            headers = {}
            for h in res_data.headers:
                headers[h.lower()] = res_data.headers[h].lower()

            result = res_data.read()
            text = result.decode(UTF8)

            if cls.record_data is not None:
                header_part = json.dumps(request_headers) if request_headers else request_headers
                key = '%s<<headers>>%s' % (ur, header_part) if header_part else url
                cls.record_data[key] = {'text': text, 'headers': headers}
            return text, headers
        else:
            header_part = json.dumps(request_headers) if request_headers else request_headers
            key = '%s<<headers>>%s' % (url, header_part) if header_part else url
            kept = cls.simulate_data.get(key)
            if kept:
                if 'code' in kept:
                    if kept['code'] == 304:
                        raise Exception('Error 304:')
                return kept['text'], kept['headers']
        return None, None

    @classmethod
    def http_get_text(cls, url):
        text = None
        try:
            text, _ = cls._get_data(url, None)
        except Exception as e:
            pass
        return text

    @classmethod
    def http_get(cls, url, request_headers):
        ret = {}
        code = 400
        try:
            text, headers = cls._get_data(url, request_headers)
            ret[KEY_RESULT] = json.loads(text, object_pairs_hook = OrderedDict)
            ret[KEY_HEADERS] = headers
            code = 200
        except Exception as e:
            err_msg = str(e)
            parts = re.split("Error ([0-9]*):", err_msg)
            if len(parts) > 1:
                code = int(parts[1])

            if code != 304:
                ret[KEY_ERROR] = 'HTTP ERROR: %s' % str(e)
        return code, ret

    @classmethod
    def get_etag_maxage(cls, headers):
        if headers is None:
            return None, None
        etag = headers.get('etag')
        text = headers.get('cache-control')
        if text is None:
            return etag, None

        parts = re.split("max\\-age[ ]*\\=[ ]*([0-9]*)[ ]*", text)
        if len(parts) < 2:
            return etag, None
        return etag, float(parts[1])


class SysUtil:

    @classmethod
    def init_logger(cls, log_file, log_name):
        handler = logging.FileHandler(log_file)
        formatter = logging.Formatter('%(asctime)s %(message)s')
        handler.setFormatter(formatter)

        logger = logging.getLogger(log_name)
        logger.addHandler(handler)
        logger.setLevel(logging.INFO)

        cls.log(logger, '')
        cls.log(logger, '--- start --- python --- %s' % (sys.version.split('\n')[0]))
        return logger

    @classmethod
    def log(cls, logger, text, log_type = LOG_TYPE_INFO):
        if logger:
            if log_type == LOG_TYPE_INFO:
                logger.info(text)
                return
            elif log_type == LOG_TYPE_ERROR:
                logger.error(text)
                return
        print(text)

    @classmethod
    def get_fallback_locale(cls, locale):
        parts = re.split("[\-_]", locale)
        parts[0] = parts[0].lower()
        if len(parts) > 1:
            parts[1] = parts[1].upper()
        locale = '-'.join(parts)

        fallback = LOCALE_MAP.get(locale.lower())
        if fallback:
            return fallback

        return parts[0]

