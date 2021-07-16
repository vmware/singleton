# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import sys
import os
import json
import re
import time
import copy
import logging
import threading
from threading import Thread
from collections import OrderedDict

from sgtn_properties import Properties
from sgtn_util import FileUtil, NetUtil, SysUtil
from sgtn_util import LOG_TYPE_INFO, KEY_RESULT, KEY_HEADERS, KEY_ERROR
from sgtn_bykey import SingletonByKey
from sgtn_locale import SingletonLocale, SingletonLocaleUtil

from I18N import Config, Release, Translation

KEY_LOCALE = 'locale'
KEY_SOURCE = 'source'
KEY_ITEMS = 'format_items'

KEY_RESPONSE = 'response'
KEY_CODE = 'code'
KEY_DATA = 'data'
KEY_MESSAGES = 'messages'

KEY_PRODUCT = 'product'
KEY_VERSION = 'l10n_version'
KEY_SERVICE_URL = 'online_service_url'
KEY_OFFLINE_URL = 'offline_resources_base_url'
KEY_LOCAL_PATH = 'offline_resources_path'

KEY_DEFAULT_LOCALE = 'default_locale'
KEY_SOURCE_LOCALE = 'source_locale'
KEY_TRYDELAY = 'try_delay'
KEY_INTERVAL = 'cache_expired_time'
KEY_CACHEPATH = 'cache_path'
KEY_CACHETYPE = 'cache_type'
KEY_LOGPATH = 'log_path'
KEY_COMPONENTS = 'components'
KEY_LOCALES = 'locales'
KEY_LANG_TAG = 'language_tag'
KEY_COMPONENT_TAG = 'name'

KEY_COMPONENT_TEMPLATE = "component_template"
KEY_LOCALES_REFER = "locales_refer"
KEY_TEMPLATE = "template"

HEADER_REQUEST_ETAG = "If-None-Match"

LOCALE_DEFAULT = 'en-US'
MAX_THREAD = 1000
NOT_IMP_EXCEPTION = 'NotImplementedException'
LOCAL_TYPE_FILE = 'file'
LOCAL_TYPE_HTTP = 'http'
RES_TYPE_PROPERTIES = '.properties'
RES_TYPE_SGTN = '.json'


class ClientUtil:

    @classmethod
    def check_response_valid(cls, dict):
        if dict and KEY_RESULT in dict:
            status = dict[KEY_RESULT].get(KEY_RESPONSE)
            if status and KEY_CODE in status:
                code = status[KEY_CODE]
                if code == 200 or code == 604:
                    return True
        return False

    @classmethod
    def read_resource_files(cls, local_type, file_list):
        props = OrderedDict()

        try:
            for prop_file in file_list:
                if prop_file.endswith(RES_TYPE_PROPERTIES):
                    text = None
                    if (local_type == LOCAL_TYPE_HTTP):
                        text = NetUtil.http_get_text(prop_file)
                    else:
                        text = FileUtil.read_text_file(prop_file)
                    if text:
                        m = Properties().parse(text)
                        props.update(m)
                elif prop_file.endswith(RES_TYPE_SGTN):
                    m = None
                    if (local_type == LOCAL_TYPE_HTTP):
                        code, dt = NetUtil.http_get(prop_file, None)
                        if (code == 200):
                            m = dt.get(KEY_RESULT)
                    else:
                        m = FileUtil.read_json_file(prop_file)
                    if m:
                        m = m.get(KEY_MESSAGES)
                        props.update(m)
        except Exception as error:
            raise IOError('Error in loading property file. Check file(s) = ', file_list, ' ', error)

        return props


class SingletonConfig(Config):

    def __init__(self, base_path, config_data):
        self.base = base_path
        self.config_data = config_data

        self.product = config_data.get(KEY_PRODUCT)
        self.version = '%s' % config_data.get(KEY_VERSION)

        self.remote_url = config_data.get(KEY_SERVICE_URL)
        self.local_url = config_data.get(KEY_OFFLINE_URL)

        if self.local_url:
            parts = self.local_url.split('/')
            self.local_type = parts[0][:-1]

            if self.local_type == LOCAL_TYPE_FILE:
                start = 2
                needBasePath = False
                if len(parts) > 3:
                    if parts[3] == '..' or parts[3] == '.':
                        start = 3
                        needBasePath = True
                    if parts[3].endswith(':'):
                        start = 3
                self.local_url = '/'.join(parts[start:])
                if needBasePath:
                    self.local_url = os.path.join(base_path, self.local_url)

        self.log_path = self.get_path(KEY_LOGPATH)          # log path
        self.cache_path = self.get_path(KEY_CACHEPATH)      # cache path
        self.cache_type = self.get_item(KEY_CACHETYPE, 'default')   # cache type

        self.cache_expired_time = self.get_item(KEY_INTERVAL, 3600) # cache expired time
        self.try_delay = self.get_item(KEY_TRYDELAY, 10)    # try delay

        self.default_locale = self.get_item(KEY_DEFAULT_LOCALE, LOCALE_DEFAULT)
        self.source_locale = self.get_item(KEY_SOURCE_LOCALE, self.default_locale)

        self._expand_components()

    def _expand_locales(self, locales_def_array, template):
        locales = {}
        for k in range(len(locales_def_array)):
            locale_def = copy.deepcopy(locales_def_array[k])
            locales[locale_def.get(KEY_LANG_TAG)] = locale_def
            if KEY_LOCAL_PATH not in locale_def and template:
                locale_def[KEY_LOCAL_PATH] = copy.deepcopy(template.get(KEY_LOCAL_PATH))
        return locales

    def _expand_components(self):
        self.components = None
        components = self.config_data.get(KEY_COMPONENTS)
        if not components:
            return

        expand = {}
        self.components = {}
        for i in range(len(components)):
            component = components[i]
            if KEY_LOCALES in component:
                component[KEY_LOCALES] = self._expand_locales(component[KEY_LOCALES], None)
                self.components[component.get(KEY_COMPONENT_TAG)] = copy.deepcopy(component)
                continue

            template_name = component.get(KEY_TEMPLATE)
            if not template_name:
                template_name = KEY_COMPONENT_TEMPLATE

            if template_name not in expand:
                t = self.config_data.get(template_name)
                refer_name = t.get(KEY_LOCALES_REFER)

                refer = self.config_data.get(refer_name)
                if not refer:
                    continue
                expand[template_name] = self._expand_locales(refer, t)

            component[KEY_LOCALES] = expand[template_name]
            self.components[component.get(KEY_COMPONENT_TAG)] = copy.deepcopy(component)

    def get_config_data(self):
        # method of Config
        return self.config_data

    def get_info(self):
        # method of Config
        info = {'product': self.product, 'version': self.version,
                'remote': self.remote_url, 'local': self.local_url,
                'source_locale': self.source_locale, 'default_locale': self.default_locale}
        return info

    def extract_list(self, key, key_name, key_refer, refer):
        _dict = {}
        _define = self.config_data.get(key)
        if not _define:
            return None
        for one in _define:
            dup = copy.deepcopy(one)
            del dup[key_name]
            _dict[one[key_name]] = dup
            if key_refer not in dup and refer:
                dup[key_refer] = copy.deepcopy(refer)
        return _dict

    def get_item(self, key, default_value):
        value = self.config_data.get(key)
        if value is None:
            value = default_value
        return value

    def get_path(self, key):
        path = self.config_data.get(key)
        if path:
            if path.startswith('./') or path.startswith('../'):
                path = os.path.realpath(os.path.join(self.base, path))
        return path


class SingletonApi:
    VIP_PATH_HEAD = '/i18n/api/v2/translation/products/%s/versions/%s/'
    VIP_PARAMETER = 'pseudo=false&machineTranslation=false&checkTranslationStatus=false'
    VIP_GET_COMPONENT = 'locales/%s/components/%s?'

    def __init__(self, release_obj):
        self.rel = release_obj
        self.cfg = release_obj.cfg
        self.addr = self.cfg.remote_url

    def get_component_api(self, component, locale):
        head = self.VIP_PATH_HEAD % (self.cfg.product, self.cfg.version)
        path = self.VIP_GET_COMPONENT % (locale, component)
        return '%s%s%s%s' % (self.addr, head, path, self.VIP_PARAMETER)

    def get_localelist_api(self):
        head = self.VIP_PATH_HEAD % (self.cfg.product, self.cfg.version)
        return '%s%slocalelist' % (self.addr, head)

    def get_componentlist_api(self):
        head = self.VIP_PATH_HEAD % (self.cfg.product, self.cfg.version)
        return '%s%scomponentlist' % (self.addr, head)


class SingletonUpdateThread(Thread):

    def __init__(self, obj):
        Thread.__init__(self)
        self.obj = obj

    def run(self):
        self.obj.get_from_remote()


class SingletonAccessRemoteTask:

    def __init__(self, release_obj, obj):
        self.rel = release_obj
        self.obj = obj

        self.last_time = 0
        self.querying = False
        self.interval = self.rel.interval

    def set_retry(self, current):
        # try again after try_delay seconds
        self.last_time = current - self.interval + self.rel.try_delay

    def check(self):
        if not self.rel.cfg.remote_url:
            return

        access_remote = False
        if self.interval > 0:
            current = time.time()
            if current > self.last_time + self.interval:
                access_remote = True
        else:
            if self.last_time == 0:
                access_remote = True

        if not access_remote:
            return

        if self.querying:
            if self.obj.get_data_count() == 0:
                while(self.querying):
                    time.sleep(0.1)
            return

        self.querying = True
        if self.obj.get_data_count() == 0:
            self.obj.get_from_remote()
        else:
            th = SingletonUpdateThread(self.obj)
            th.start()


class SingletonComponent:

    def __init__(self, release_obj, locale, component, isLocalSource):
        self.rel = release_obj
        self.locale = locale
        self.localeItem = self.rel.bykey.get_locale_item(locale, isLocalSource)
        self.componentIndex = self.rel.bykey.get_component_index(component)
        self.component = component
        self.isLocalSource = isLocalSource
        self.countOfMessages = 0
        self.etag = None
        self.cache_path = None
        self.task = None if isLocalSource else SingletonAccessRemoteTask(release_obj, self)

        if self.task and self.rel.cache_path:
            self.cache_path = os.path.join(self.rel.cache_path, component, 'messages_%s.json' % locale)
            self.rel.log('--- cache file --- %s ---' % self.cache_path)

            if os.path.exists(self.cache_path):
                dt = FileUtil.read_json_file(self.cache_path)
                if KEY_MESSAGES in dt:
                    self.task.last_time = os.path.getmtime(self.cache_path)
                    self.set_messages(dt[KEY_MESSAGES])

    def set_messages(self, messages):
        for key in messages:
            text = messages[key]
            self.rel.bykey.set_string(key, self, self.componentIndex, self.localeItem, text)
        self.countOfMessages = len(messages)

    def get_messages(self):
        return self.rel.bykey.get_messages(self.componentIndex, self.localeItem)

    def get_message(self, key):
        return self.rel.bykey.get_string(key, self.componentIndex, self.localeItem)

    def is_messages_same(self, messages):
        for key in messages:
            text = messages[key]
            message = self.rel.bykey.get_string(key, self.componentIndex, self.localeItem)
            if message != text:
                return False
        return True

    def get_from_remote(self):
        current = time.time()

        try:
            # get messages
            addr = self.rel.api.get_component_api(self.component, self.locale)
            headers = {}
            if self.etag:
                headers[HEADER_REQUEST_ETAG] = self.etag
            code, dt = NetUtil.http_get(addr, headers)
            if code == 200 and ClientUtil.check_response_valid(dt):
                self.etag, interval = NetUtil.get_etag_maxage(dt.get(KEY_HEADERS))
                if interval:
                    self.task.interval = interval

                messages = dt[KEY_RESULT][KEY_DATA][KEY_MESSAGES]
                if self.cache_path:
                    if os.path.exists(self.cache_path) and self.is_messages_same(messages):
                        os.utime(self.cache_path, (current, current))
                    else:
                        self.rel.log('--- save --- %s ---' % self.cache_path)
                        FileUtil.save_json_file(self.cache_path, dt[KEY_RESULT][KEY_DATA])
                self.set_messages(messages)
                self.task.last_time = current
            elif code == 304:
                self.task.last_time = current
            else:
                self.task.set_retry(current)
        except Exception as e:
            self.task.set_retry(current)

        self.task.querying = False

    def get_data_count(self):
        return self.countOfMessages


class SingletonUseLocale:

    def __init__(self, singletonLocale, sourceLocale, isLocalSource, bykey):
        self.singletonLocale = singletonLocale
        self.locale = self.singletonLocale.get_original_locale()
        self.isLocalSource = isLocalSource

        singletonSourceLocale = SingletonLocaleUtil.get_singleton_locale(sourceLocale)
        self.isSourceLocale = self.locale in singletonSourceLocale.get_near_locale_list()

        self.localeItem = bykey.get_locale_item(self.locale, True) if isLocalSource and bykey else None

        self.components = {}


class SingletonReleaseBase:

    def __init__(self, cfg):
        self.cfg = cfg
        self.cache_path = None
        self.scope = None
        self.logger = None
        self.interval = 0
        self.try_delay = 0
        self.detach = False

        self.locale_list = []
        self.component_list = []

        self.remote_pool = {}
        self.source_pool = {}
        self.local_handled = {}
        self.component_handled = {}

        if not cfg:
            return

        if cfg.log_path:
            log_file = os.path.join(cfg.log_path, '%s_%s.log' % (self.cfg.product, self.cfg.version))
            self.init_logger(log_file)

        if cfg.cache_path:
            self.cache_path = os.path.join(cfg.cache_path, self.cfg.product, self.cfg.version)
            self.log('--- cache path --- %s ---' % self.cache_path)

        self.interval = cfg.cache_expired_time
        self.try_delay = cfg.try_delay

        self.task = SingletonAccessRemoteTask(self, self)
        self.get_scope()

        self.remote_default_locale = self.get_locale_supported(self.cfg.default_locale)
        self.remote_source_locale = self.get_locale_supported(self.cfg.source_locale)

        self.isDifferent = self.remote_default_locale != self.remote_source_locale

        self.bykey = SingletonByKey(self.cfg.source_locale, self.cfg.default_locale, self.isDifferent, self.cfg.cache_type)

        self.useSourceLocale = self.get_use_locale(self.cfg.source_locale, True)
        self._get_local_resource(self.useSourceLocale, self.cfg.source_locale)

        self.useDefaultLocale = None
        if self.isDifferent:
            self.useDefaultLocale = self.get_use_locale(self.cfg.default_locale, False)


    def get_use_locale(self, locale, asSource):
        pool = self.source_pool if asSource else self.remote_pool
        useLocale = pool.get(locale)
        if useLocale is None:
            singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
            useLocale = singletonLocale.find_item(pool, 1)

            if useLocale is None:
                useLocale = SingletonUseLocale(singletonLocale, self.cfg.source_locale, asSource, self.bykey)

            for one in useLocale.singletonLocale.get_near_locale_list():
                if one not in pool:
                    pool[one] = useLocale
        return useLocale

    def get_scope(self):
        self.api = SingletonApi(self)

        if self.cache_path:
            self.locale_list = FileUtil.read_json_file(os.path.join(self.cache_path, 'locale_list.json'))
            self.component_list = FileUtil.read_json_file(os.path.join(self.cache_path, 'component_list.json'))
        if not self.cfg.remote_url:
            return

        if not self.locale_list:
            self.get_from_remote()
        else:
            th = SingletonUpdateThread(self)
            th.start()

    def get_from_remote(self):
        self.task.last_time = time.time()

        try:
            # get locale list
            scope = self._get_scope_item(self.api.get_localelist_api(), KEY_LOCALES, 'locale_list.json')
            if scope:
                self.locale_list = scope

            # get component list
            scope = self._get_scope_item(self.api.get_componentlist_api(), KEY_COMPONENTS, 'component_list.json')
            if scope:
                self.component_list = scope
        except Exception as e:
            pass

        self.task.querying = False

    def get_data_count(self):
        if not self.locale_list or not self.component_list:
            return 0
        return len(self.locale_list) + len(self.component_list)

    def init_logger(self, log_file):
        self.logger = SysUtil.init_logger(log_file, 'sgtn_%s_%s' % (self.cfg.product, self.cfg.version))
        self.log('--- release --- %s --- %s --- %s ---' % (self.cfg.product, self.cfg.version, time.time()))

    def log(self, text, log_type = LOG_TYPE_INFO):
        SysUtil.log(self.logger, text, log_type)

    def _load_one_local(self, component, locale, path_define):
        if not path_define:
            return None
        for i in range(len(path_define)):
            path = path_define[i].replace('$COMPONENT', component).replace('$LOCALE', locale)
            path_define[i] = os.path.join(self.cfg.local_url, path)
        return ClientUtil.read_resource_files(self.cfg.local_type, path_define)

    def _get_scope_item(self, addr, key, keep_name):
        code, dt = NetUtil.http_get(addr, None)
        if code == 200 and ClientUtil.check_response_valid(dt):
            _, interval = NetUtil.get_etag_maxage(dt.get(KEY_HEADERS))
            if interval:
                self.task.interval = interval

            scope = dt[KEY_RESULT][KEY_DATA][key]
            if scope and self.cache_path:
                FileUtil.save_json_file(os.path.join(self.cache_path, keep_name), scope)
            return scope
        return None

    def _extract_info_from_dir(self, root):
        if self.cfg.local_type != LOCAL_TYPE_FILE:
            return

        components = {}
        dir_list, _ = FileUtil.get_dir_info(root)
        for component in dir_list:
            components[component] = {}
            component_obj = components[component]
            component_obj[KEY_LOCALES] = {}
            locales_cfg = component_obj.get(KEY_LOCALES)

            component_path = os.path.join(self.cfg.local_url, component)
            _, file_list = FileUtil.get_dir_info(component_path)
            for res_file in file_list:
                parts = re.split(r"messages(.*)\.", res_file)
                if (len(parts) == 3):
                    if parts[1].startswith('_'):
                        locale = parts[1][1:]
                    elif parts[1] == '':
                        locale = self.cfg.source_locale
                    locales_cfg[locale] = {KEY_LOCAL_PATH: [os.path.join(component, res_file)]}
        return components

    def _get_local_resource(self, useLocale, locale):
        if useLocale is None:
            return
        locale_item = useLocale.components

        if not self.cfg.local_url:
            return

        if not self.cfg.components:
            self.cfg.components = self._extract_info_from_dir(self.cfg.local_url)
            if not self.cfg.components:
                return

        for component in self.cfg.components:
            locales_cfg = self.cfg.components[component].get(KEY_LOCALES)
            locale_define = None
            if locales_cfg:
                singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
                locale_define = singletonLocale.find_item(locales_cfg, 0)

            combineKey = locale + '_!_' + component
            if locale_define and combineKey not in self.local_handled:
                path_define = locale_define.get(KEY_LOCAL_PATH)
                map = self._load_one_local(component, locale, path_define)
                component_obj = SingletonComponent(self, locale, component, useLocale.isLocalSource)
                component_obj.set_messages(map)
                locale_item[component] = component_obj
            self.local_handled[combineKey] = True

    def _get_remote_resource(self, locale, component):
        if not self.locale_list or not self.component_list:
            return None
        singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
        if not singletonLocale.is_in_locale_list(self.locale_list):
            return None
        if component not in self.component_list:
            return None

        components = self.get_use_locale(locale, False).components
        component_obj = components.get(component)
        if component_obj is None:
            self.log('--- component --- %s ---' % component)
            component_obj = SingletonComponent(self, locale, component, False)
            components[component] = component_obj

        component_obj.task.check()
        return component_obj

    def _get_component(self, locale, component):
        component_remote = self._get_remote_resource(locale, component)
        if component_remote:
            return component_remote

        component_obj = None
        if self.cfg.local_url:
            useLocale = self.get_use_locale(locale, False)
            combineKey = locale + '_!_' + component
            if combineKey not in self.local_handled:
                self._get_local_resource(useLocale, locale)
                self.local_handled[combineKey] = True
            if useLocale:
                component_obj = useLocale.components.get(component)
                if component_obj is None and useLocale.isSourceLocale:
                    component_obj = self.useSourceLocale.components.get(component)

        return component_obj

    def _get_message(self, component, key, source, locale):
        message = source if source != None else key
        if not key or not locale:
            return message

        if not self.bykey._onlyByKey and not component:
            return message

        self.task.check()

        componentIndex = self.bykey.get_component_index(component)
        if componentIndex >= 0:
            combineKey = locale + '_!_' + component
            if combineKey not in self.component_handled:
                self._get_component(self.remote_source_locale, component)
                componentObj = self._get_component(locale, component)
                if self.isDifferent:
                    self._get_component(self.remote_default_locale, component)
                if componentObj:
                    self.component_handled[combineKey] = True

        localeItem = self.bykey.get_locale_item(locale, False)
        message = self.bykey.get_string(key, componentIndex, localeItem, True)
        return message


class SingletonRelease(SingletonReleaseBase, Release, Translation):

    def get_config(self):
        # method of Release
        return self.cfg

    def get_translation(self):
        # method of Release
        return self

    def get_locale_strings(self, locale, asSource):
        # method of Translation
        collect = {}
        useLocale = self.get_use_locale(locale, asSource)
        if useLocale and useLocale.components:
            components = useLocale.components
            for component in components:
                collect[component] = components[component].get_messages()
        return collect

    def get_source(self, component, key, sourceInCode):
        componentIndex = self.bykey.get_component_index(component)
        source = self.bykey.get_string(key, componentIndex, self.useSourceLocale.localeItem, False)
        if source != None:
            return source

        source = self._get_message(component, key, sourceInCode, self.cfg.source_locale)
        return source

    def get_raw(self, component, key, sourceInCode, locale, items):
        useLocale = self.get_use_locale(locale, False)
        if useLocale.isSourceLocale:
            if sourceInCode != None:
                return sourceInCode
            return self.get_source(component, key, sourceInCode)

        source = self.get_source(component, key, sourceInCode)
        if sourceInCode != None and source != None and source != sourceInCode:
            return sourceInCode

        return self._get_message(component, key, source, locale)

    def get_string(self, component, key, **kwargs):
        # method of Translation
        sourceInCode = kwargs.get(KEY_SOURCE) if kwargs else None
        locale = kwargs.get(KEY_LOCALE) if kwargs else None
        items = kwargs.get(KEY_ITEMS) if kwargs else None

        if not locale:
            locale = SingletonClientManager().get_current_locale()
            
        text = self.get_raw(component, key, sourceInCode, locale, items)
        if text and items:
            if isinstance(items, list):
                text = text.format(*items)
            elif isinstance(items, dict):
                text = text.format(**items)

        if text is None:
            text = key
        return text

    def get_locale_supported(self, locale):
        # method of Translation
        return SysUtil.get_fallback_locale(locale)


class SingletonClientManager(object):
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = object.__new__(cls)
            cls._instance.init()
        return cls._instance

    def init(self):
        self._products = {}

    def add_config_file(self, config_file, replaceMap = None):
        config_text = FileUtil.read_text_file(config_file)
        if replaceMap:
            for key in replaceMap:
                config_text = config_text.replace(key, replaceMap[key])
        config_data = FileUtil.parse_datatree(config_text)

        base_path = os.path.dirname(os.path.realpath(config_file))
        cfg = self.add_config(base_path, config_data)
        return cfg

    def add_config(self, base_path, config_data):
        if not config_data:
            return
        cfg = SingletonConfig(base_path, config_data)
        release_obj = self.get_release(cfg.product, cfg.version)
        if release_obj is None:
            self.create_release(cfg)
        return cfg

    def get_release(self, product, version):
        if not product or not version:
            return None

        releases = self._products.get(product)
        if releases is None:
            return None

        return releases.get(version)

    def create_release(self, cfg):
        if not cfg or not cfg.product or not cfg.version:
            return

        releases = self._products.get(cfg.product)
        if releases is None:
            self._products[cfg.product] = {}
            releases = self._products.get(cfg.product)

        release_obj = releases.get(cfg.version)
        if release_obj is None:
            release_obj = SingletonRelease(cfg)
            releases[cfg.version] = release_obj

    def set_current_locale(self, locale):
        current = sys._getframe().f_back.f_back
        for i in range(10):
            if not hasattr(current, 'f_locals'):
                break

            locals = current.f_locals
            locals['_singleton_locale_'] = locale

            if not hasattr(current, 'f_back'):
                break
            current = current.f_back

    def get_current_locale(self):
        current = sys._getframe().f_back.f_back
        for i in range(10):
            if not hasattr(current, 'f_locals'):
                break

            locals = current.f_locals
            if '_singleton_locale_' in locals:
                return locals['_singleton_locale_']

            if not hasattr(current, 'f_back'):
                break
            current = current.f_back
        return LOCALE_DEFAULT
