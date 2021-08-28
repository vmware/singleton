# coding=utf-8
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

from sgtn_py_base import pybase, SgtnException
from sgtn_debug import SgtnDebug

import ssl
if hasattr(ssl, '_create_unverified_context'):  # for python 2.7
    ssl._create_default_https_context = ssl._create_unverified_context

PY_VER = sys.version_info.major
UTF8 = 'utf-8'

httplib = pybase.get_httplib()

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

    dir_map = {}

    @classmethod
    def read_text_file(cls, file_name):
        SgtnDebug.log_text('util', 'read file {0} / exist: {1}'.format(
            file_name, os.path.exists(file_name)))
        if os.path.exists(file_name) and os.path.isfile(file_name):
            f = open(file_name, 'rb')
            file_data = f.read()
            f.close()

            try:
                file_data = file_data.decode(UTF8)
                return file_data
            except UnicodeDecodeError as e:
                return None
        return None

    @classmethod
    def parse_json_from_text(cls, text):
        if not text:
            return None

        try:
            dict_data = json.loads(text, object_pairs_hook=OrderedDict)
            return dict_data
        except Exception as e:
            raise SgtnException(str(e))

    @classmethod
    def parse_json(cls, text):
        if text:
            try:
                return cls.parse_json_from_text(text)
            except SgtnException as e:
                return None
        return None

    @classmethod
    def parse_yaml_from_text(cls, text):
        try:
            import yaml
            dict_data = yaml.load(text, Loader=yaml.FullLoader)
            return dict_data
        except yaml.YAMLError as e:
            raise SgtnException(str(e))

    @classmethod
    def parse_yaml(cls, text):
        if text:
            try:
                return cls.parse_yaml_from_text(text)
            except SgtnException as e:
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

    @classmethod
    def save_json_file(cls, file_name, dict):
        dir = os.path.dirname(file_name)
        if not os.path.exists(dir):
            os.makedirs(dir)
        f = pybase.open_file(file_name, 'w')
        text = json.dumps(dict, ensure_ascii=False, indent=2)
        f.write(text)
        f.close()

    @classmethod
    def get_dir_info(cls, dir_name):
        dir_list = []
        file_list = []

        for one in cls.dir_map:
            if dir_name.endswith(one):
                dir_list = cls.dir_map[one]
                return dir_list, file_list

        try:
            ls = os.listdir(dir_name)
        except IOError as e:
            pass
        else:
            for fn in ls:
                temp = os.path.join(dir_name, fn)
                if os.path.isdir(temp):
                    dir_list.append(fn)
                else:
                    file_list.append(fn)
        return dir_list, file_list


class NetSimulate(object):
    """Net simulate interface"""

    def has_data(self):
        return False

    def get_data(self, url, request_headers):
        return None, None

    def is_record_enabled(self):
        return False

    def record_data(self, url, request_headers, text, headers):
        pass


class NetUtil:

    simulate = None

    @classmethod
    def _get_data(cls, url, request_headers, timeout=None):
        if not cls.simulate or not cls.simulate.has_data():
            req = httplib.Request(url)
            if request_headers:
                for key in request_headers:
                    req.add_header(key, request_headers[key])

            try:
                if timeout is None or timeout == 0:
                    res_data = httplib.urlopen(req)
                else:
                    res_data = httplib.urlopen(req, timeout=timeout)
            except IOError as e:
                raise SgtnException(str(e))

            headers = {}
            for h in res_data.headers:
                headers[h.lower()] = res_data.headers[h].lower()

            try:
                result = res_data.read()
            except IOError as e:
                raise SgtnException(str(e))

            try:
                text = result.decode(UTF8)
            except UnicodeDecodeError as e:
                raise SgtnException(str(e))

            if cls.simulate and cls.simulate.is_record_enabled():
                cls.simulate.record_data(url, request_headers, text, headers)
            return text, headers
        else:
            return cls.simulate.get_data(url, request_headers)
        return None, None

    @classmethod
    def http_get_text(cls, url, timeout=None):
        text = None
        try:
            text, _ = cls._get_data(url, None, timeout)
        except SgtnException as e:
            pass
        return text

    @classmethod
    def http_get(cls, url, request_headers, timeout=None):
        ret = {}
        code = 400
        try:
            text, headers = cls._get_data(url, request_headers, timeout)
            ret[KEY_RESULT] = FileUtil.parse_json_from_text(text)
            ret[KEY_HEADERS] = headers
            code = 200
        except SgtnException as e:
            err_msg = str(e)
            parts = re.split("Error ([0-9]*):", err_msg)
            if len(parts) > 1:
                code = int(parts[1])

            if code != 304:
                ret[KEY_ERROR] = 'HTTP ERROR: {0}'.format(str(e))
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
        cls.log(logger, '--- start --- python --- {0}'.format(sys.version.split('\n')[0]))
        return logger

    @classmethod
    def log(cls, logger, text, log_type=LOG_TYPE_INFO):
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
        parts = re.split(r"[\-_]", locale)
        parts[0] = parts[0].lower()
        if len(parts) > 1:
            parts[1] = parts[1].upper()
        locale = '-'.join(parts)

        fallback = LOCALE_MAP.get(locale.lower())
        if fallback:
            return fallback

        return parts[0]
