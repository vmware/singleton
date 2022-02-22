# -*-coding:UTF-8 -*-
#
# Copyright 2020-2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import sys
import re
import time
import copy
from threading import Thread
from collections import OrderedDict

from sgtn_properties import Properties
from sgtn_util import FileUtil, NetUtil, SysUtil, Data
from sgtn_util import LOG_TYPE_INFO, KEY_RESULT, KEY_HEADERS
from sgtn_bykey import SingletonByKey
from sgtn_locale import SingletonLocaleUtil
from sgtn_icu import SgtnIcu
from sgtn_py_base import pybase, SgtnException

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
KEY_SOURCE_PATH = 'source_resources_path'
KEY_LOAD_ON_STARTUP = 'load_on_startup'
KEY_MULTITASK = 'multitask'
KEY_FORMAT = 'format'

KEY_DEFAULT_LOCALE = 'default_locale'
KEY_SOURCE_LOCALE = 'source_locale'
KEY_PSEUDO = 'pseudo'
KEY_TRYWAIT = 'try_wait'
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
LOCAL_TYPE_FILE = 'file'
LOCAL_TYPE_HTTP = 'http'
RES_TYPE_PROPERTIES = '.properties'
RES_TYPE_SGTN = '.json'

_SOURCE_ALIAS = 'latest'

_FROM_LOCAL = 'from_local'
_FROM_REMOTE = 'from_remote'


class ClientUtil:

    @classmethod
    def check_response_valid(cls, dict):
        if dict and KEY_RESULT in dict:
            sub = dict[KEY_RESULT]
            if not sub:
                return False

            status = sub.get(KEY_RESPONSE)
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
                    if local_type == LOCAL_TYPE_HTTP:
                        text = NetUtil.http_get_text(prop_file)
                    else:
                        text = FileUtil.read_text_file(prop_file)
                    if text:
                        m = Properties().parse(text)
                        props.update(m)
                elif prop_file.endswith(RES_TYPE_SGTN):
                    m = None
                    if local_type == LOCAL_TYPE_HTTP:
                        code, dt = NetUtil.http_get(prop_file, None)
                        if code == 200:
                            m = dt.get(KEY_RESULT)
                    else:
                        m = FileUtil.read_json_file(prop_file)
                    if m:
                        m = m.get(KEY_MESSAGES)
                        props.update(m)
        except Exception as error:
            raise IOError('Error in loading property file. Check file(s) = ', file_list, ' ', error)

        return props

    @classmethod
    def get_combine_key(cls, locale, component, ty, value):
        return '{0}_!_{1}_!_{2}_!_{3}'.format(locale, component, ty, value)


class SingletonConfig(Config):

    def __init__(self, base_path, config_data):
        self._base = base_path
        self._config_data = config_data

        self.product = config_data.get(KEY_PRODUCT)
        self.version = '{0}'.format(config_data.get(KEY_VERSION))

        self.remote_url = config_data.get(KEY_SERVICE_URL)
        self.local_url = config_data.get(KEY_OFFLINE_URL)
        self.is_online_supported = True if self.remote_url else False
        self.is_offline_supported = True if self.local_url else False
        self.is_load_on_startup = True if config_data.get(KEY_LOAD_ON_STARTUP) else False
        self.format_type = self._get_item(KEY_FORMAT, 'default')

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

        self.log_path = self._get_path(KEY_LOGPATH)         # log path
        self.cache_path = self._get_path(KEY_CACHEPATH)     # cache path
        self.cache_type = self._get_item(KEY_CACHETYPE, 'default')      # cache type

        self.cache_expired_time = self._get_item(KEY_INTERVAL, 3600)  # cache expired time
        self.try_wait = self._get_item(KEY_TRYWAIT, 10)     # try wait

        self.default_locale = self._get_item(KEY_DEFAULT_LOCALE, LOCALE_DEFAULT)
        self.source_locale = self._get_item(KEY_SOURCE_LOCALE, self.default_locale)

        self.pseudo = KEY_PSEUDO in self._config_data and self._config_data[KEY_PSEUDO]
        self.need_async = self._get_item(KEY_MULTITASK, None) == 'async'

        self._expand_components()

    def get_config_data(self):
        """method of Config"""
        return self._config_data

    def get_info(self):
        """method of Config"""
        info = {'product': self.product, 'version': self.version,
                'remote': self.remote_url, 'local': self.local_url, 'pseudo': self.pseudo,
                'source_locale': self.source_locale, 'default_locale': self.default_locale}
        return info

    def _get_item(self, key, default_value):
        value = self._config_data.get(key)
        if value is None:
            value = default_value
        return value

    def _get_path(self, key):
        path = self._config_data.get(key)
        if path:
            if path.startswith('./') or path.startswith('../'):
                path = os.path.realpath(os.path.join(self._base, path))
        return path

    def _expand_locales(self, locales_def_array, template):
        locales = {}
        for one in locales_def_array:
            locale_def = copy.deepcopy(one)
            locales[locale_def.get(KEY_LANG_TAG)] = locale_def
            if KEY_LOCAL_PATH not in locale_def and template:
                locale_def[KEY_LOCAL_PATH] = copy.deepcopy(template.get(KEY_LOCAL_PATH))
        return locales

    def _expand_components(self):
        self.components = None
        components = self._config_data.get(KEY_COMPONENTS)
        if not components:
            return

        expand = {}
        self.components = {}
        for component in components:
            if KEY_LOCALES in component:
                component[KEY_LOCALES] = self._expand_locales(component[KEY_LOCALES], None)
                self.components[component.get(KEY_COMPONENT_TAG)] = copy.deepcopy(component)
                continue

            template_name = component.get(KEY_TEMPLATE)
            if not template_name:
                template_name = KEY_COMPONENT_TEMPLATE

            if template_name not in expand:
                t = self._config_data.get(template_name)
                refer_name = t.get(KEY_LOCALES_REFER)

                refer = self._config_data.get(refer_name)
                if not refer:
                    continue
                expand[template_name] = self._expand_locales(refer, t)

            component[KEY_LOCALES] = expand[template_name]
            self.components[component.get(KEY_COMPONENT_TAG)] = copy.deepcopy(component)


class SingletonApi:
    VIP_PATH_HEAD = '/i18n/api/v2/translation/products/{0}/versions/{1}/'
    VIP_PARAMETER = 'pseudo={0}&machineTranslation=false&checkTranslationStatus=false'
    VIP_GET_COMPONENT = 'locales/{0}/components/{1}?'

    def __init__(self, release_obj):
        cfg = release_obj.cfg
        self._product = cfg.product
        self._version = cfg.version
        self._addr = cfg.remote_url

    def get_component_api(self, component, locale, pseudo=False):
        head = self.VIP_PATH_HEAD.format(self._product, self._version)
        path = self.VIP_GET_COMPONENT.format(locale, component)
        pseudoText = 'true' if pseudo else 'false'
        parameter = self.VIP_PARAMETER.format(pseudoText)
        return '{0}{1}{2}{3}'.format(self._addr, head, path, parameter)

    def get_localelist_api(self):
        head = self.VIP_PATH_HEAD.format(self._product, self._version)
        return '{0}{1}localelist'.format(self._addr, head)

    def get_componentlist_api(self):
        head = self.VIP_PATH_HEAD.format(self._product, self._version)
        return '{0}{1}componentlist'.format(self._addr, head)


class SingletonUpdateThread(Thread):

    def __init__(self, remote_task, obj):
        Thread.__init__(self)
        self._task = remote_task
        self._obj = obj

        self._task.querying = True

    def do_work(self):
        self._obj.get_from_remote()
        self._task.querying = False

    def run(self):
        self.do_work()


class SingletonAccessRemoteTask:

    def __init__(self, release_obj, obj):
        self._rel = release_obj
        self._obj = obj
        self._need_async = self._rel.cfg.need_async

        self.last_time = 0
        self.querying = False
        self.interval = release_obj.interval

    def set_retry(self, current):
        # try again after try_wait seconds
        self.last_time = current - self.interval + self._rel.try_wait

    def check(self):
        if not self._rel.cfg.remote_url:
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
            if self._obj.get_data_count() == 0:
                while self.querying:
                    time.sleep(0.1)
            return

        th = SingletonUpdateThread(self, self._obj)
        if self._obj.get_data_count() == 0:
            th.do_work()
        else:
            pybase.do_multitask(th, self._need_async)


class SingletonComponent:

    def __init__(self, release_obj, singletonLocale, component, asSource, fromRemote):
        self._rel = release_obj
        self._singletonLocale = singletonLocale
        self._locale = singletonLocale.get_original_locale()
        self._localeUse = self._locale
        self._localeItem = self._rel.bykey.get_locale_item(self._locale, asSource)
        self._componentIndex = self._rel.bykey.get_component_index(component)
        self._component = component
        self._asSource = asSource
        self._fromRemote = fromRemote
        self._countOfMessages = 0
        self._etag = None
        self._cache_path = None
        self._localHandled = False
        self._pseudo = self._rel.cfg.pseudo
        if self._localeItem.isSourceLocale:
            if self._pseudo:
                self._pseudo = False
                self._localeUse = _SOURCE_ALIAS
            if asSource and fromRemote:
                self._localeUse = _SOURCE_ALIAS

        self.task = SingletonAccessRemoteTask(release_obj, self) if fromRemote else None
        self.is_local = True

        if self.task and self._rel.cache_path:
            self._cache_path = os.path.join(self._rel.cache_path, component, 'messages_{0}.json'.format(self._locale))
            self._rel.log('--- cache file --- {0} ---'.format(self._cache_path))

            if os.path.exists(self._cache_path):
                dt = FileUtil.read_json_file(self._cache_path)
                if KEY_MESSAGES in dt:
                    self.task.last_time = os.path.getmtime(self._cache_path)
                    self.set_messages(dt[KEY_MESSAGES])

    def set_messages(self, messages):
        for key in messages:
            text = messages[key]
            if self.is_local and self._pseudo and not self._localeItem.isSourceLocale:
                text = self._rel.bykey.add_pseudo(text)
            if not self.is_local or not self._pseudo or self._localeItem.isSourceLocale:
                self._rel.bykey.set_string(key, self, self._componentIndex, self._localeItem, text)
        self._countOfMessages = len(messages)

    def get_messages(self):
        return self._rel.bykey.get_messages(self._componentIndex, self._localeItem)

    def get_message(self, key):
        return self._rel.bykey.get_string(key, self._componentIndex, self._localeItem)

    def is_messages_same(self, messages):
        for key in messages:
            text = messages[key]
            message = self._rel.bykey.get_string(key, self._componentIndex, self._localeItem)
            if message != text:
                return False
        return True

    def get_from_remote(self):
        current = time.time()

        try:
            # get messages
            addr = self._rel.api.get_component_api(self._component, self._localeUse, self._pseudo)
            headers = {}
            if self._etag:
                headers[HEADER_REQUEST_ETAG] = self._etag
            code, dt = NetUtil.http_get(addr, headers, self._rel.try_wait)
            if code == 200 and ClientUtil.check_response_valid(dt):
                self._etag, interval = NetUtil.get_etag_maxage(dt.get(KEY_HEADERS))
                if interval:
                    self.task.interval = interval

                messages = dt[KEY_RESULT][KEY_DATA][KEY_MESSAGES]
                if self._cache_path:
                    if os.path.exists(self._cache_path) and self.is_messages_same(messages):
                        os.utime(self._cache_path, (current, current))
                    else:
                        self._rel.log('--- save --- {0} ---'.format(self._cache_path))
                        FileUtil.save_json_file(self._cache_path, dt[KEY_RESULT][KEY_DATA])

                self.is_local = False
                # follow above
                self.set_messages(messages)

                self.task.last_time = current
                self._localHandled = True
            elif code == 304:
                self.task.last_time = current
            else:
                self.task.set_retry(current)
        except SgtnException as e:
            self.task.set_retry(current)

        self.get_from_local()

    def get_data_count(self):
        return self._countOfMessages

    def get_from_local(self):
        if self._localHandled:
            return
        self._localHandled = True

        config = self._rel.get_config()
        if config.is_offline_supported:
            self._rel.update.load_local_message(self._singletonLocale, self._component, self._asSource, self._fromRemote)


class SingletonUseLocale:

    def __init__(self, release, singletonLocale, sourceLocale, asSource):
        self._rel = release
        self._locale = singletonLocale.get_original_locale()
        self._asSource = asSource

        self._is_online_supported = release.get_config().is_online_supported
        self._components = {}

        self.singletonLocale = singletonLocale

        singletonSourceLocale = SingletonLocaleUtil.get_singleton_locale(sourceLocale)
        self.is_source_locale = self._locale in singletonSourceLocale.get_near_locale_list()

        bykey = self._rel.bykey
        self.localeItem = bykey.get_locale_item(self._locale, asSource)

    def get_component(self, component, use_remote):
        component_obj = self._components.get(component)
        if component_obj is None:
            relateLocale = self._rel.get_locale_in_scope(self.singletonLocale, component)
            if not relateLocale:
                return None

            component_obj = SingletonComponent(self._rel, relateLocale, component, self._asSource, use_remote)
            self._components[component] = component_obj

            if not self._is_online_supported or not use_remote:
                component_obj.get_from_local()

        if self._is_online_supported and use_remote:
            if component_obj.task:
                component_obj.task.check()

        return component_obj

    def get_all_strings(self):
        collect = {}
        for component in self._components:
            collect[component] = self._components[component].get_messages()
        return collect


class SingletonUpdate:

    def __init__(self, release):
        self._rel = release
        self._local_handled = {}

    def _extract_info_from_dir(self, root):
        cfg = self._rel.cfg
        if cfg.local_type != LOCAL_TYPE_FILE:
            return

        components = {}
        dir_list, _ = FileUtil.get_dir_info(root)
        for component in dir_list:
            components[component] = {}
            component_obj = components[component]
            component_obj[KEY_LOCALES] = {}
            locales_cfg = component_obj.get(KEY_LOCALES)

            component_path = os.path.join(cfg.local_url, component)
            _, file_list = FileUtil.get_dir_info(component_path)
            for res_file in file_list:
                parts = re.split(r"messages(.*)\.", res_file)
                if len(parts) == 3:
                    if parts[1].startswith('_'):
                        locale = parts[1][1:]
                    elif parts[1] == '':
                        locale = cfg.source_locale
                    locales_cfg[locale] = {KEY_LOCAL_PATH: [os.path.join(component, res_file)]}
        return components

    def get_scope_item(self, addr, key, keep_name):
        code, dt = NetUtil.http_get(addr, None, self._rel.try_wait)
        if code == 200 and ClientUtil.check_response_valid(dt):
            _, interval = NetUtil.get_etag_maxage(dt.get(KEY_HEADERS))
            if interval:
                self._rel.task.interval = interval

            scope = dt[KEY_RESULT][KEY_DATA][key]
            if scope and self._rel.cache_path:
                FileUtil.save_json_file(os.path.join(self._rel.cache_path, keep_name), scope)
            if scope:
                scope.sort()
            return scope
        return None

    def _load_locale_bundle(self, component, locale, pathDefine, asSource):
        combineKey = ClientUtil.get_combine_key(locale, component, 'local', asSource)
        if combineKey in self._local_handled:
            return

        self._local_handled[combineKey] = True
        map = self._load_one_local(component, locale, pathDefine)

        useLocale = self._rel.get_use_locale(locale, asSource)
        component_obj = useLocale.get_component(component, False)
        component_obj.is_local = True
        # follow above
        component_obj.set_messages(map)

    def load_local_message(self, singletonLocale, component_name, asSource, is_direct):
        if singletonLocale is None:
            return
        locale = singletonLocale.get_original_locale()
        cfg = self._rel.cfg
        local_scope = self._rel.local_scope

        if not cfg.local_url:
            return

        if not cfg.components:
            cfg.components = self._extract_info_from_dir(cfg.local_url)
            if not cfg.components:
                return

        if not local_scope.component_list:
            local_scope.update_component_list(cfg.components)
        if not local_scope.locale_list:
            local_scope.update_locale_list(cfg.components)

        if not cfg.is_online_supported:
            is_direct = True

        for component in local_scope.component_list:
            if component_name and component != component_name:
                continue

            locales_cfg = cfg.components[component].get(KEY_LOCALES)
            locale_define = None
            if locales_cfg:
                locale_define = singletonLocale.find_item(locales_cfg, 0)
            if not locale_define:
                continue

            combineKey = ClientUtil.get_combine_key(locale, component, 'direct', is_direct)
            if combineKey not in self._local_handled:
                self._local_handled[combineKey] = True

                path_define = locale_define.get(KEY_SOURCE_PATH)
                path_define_local = locale_define.get(KEY_LOCAL_PATH)
                if not asSource or path_define is None:
                    path_define = path_define_local
                    path_define_local = None

                self._load_locale_bundle(component, locale, path_define, asSource)

                if path_define_local:
                    if is_direct:
                        self._load_locale_bundle(component, locale, path_define_local, False)
                    else:
                        self._rel.useSourceRemote.get_component(component, True)

    def _load_one_local(self, component, locale, path_define):
        cfg = self._rel.cfg
        if not path_define:
            return None
        is_source_locale = cfg.source_locale == locale
        paths = []
        for i, v in enumerate(path_define):
            locale_underline = '' if is_source_locale else '_' + locale
            path = v.replace('$COMPONENT', component).replace('$LOCALE', locale)
            path = path.replace('$LC', locale_underline)
            paths.append(os.path.join(cfg.local_url, path))
        return ClientUtil.read_resource_files(cfg.local_type, paths)


class SingletonReleaseScopeInfo:

    def __init__(self):
        self.locale_list = []
        self.component_list = []

    def update_locale_list_from_path(self, cache_path):
        locale_list = FileUtil.read_json_file(os.path.join(cache_path, 'locale_list.json'))
        if locale_list is None:
            locale_list = []
        self.locale_list = locale_list

    def update_component_list_from_path(self, cache_path):
        component_list = FileUtil.read_json_file(os.path.join(cache_path, 'component_list.json'))
        if component_list is None:
            component_list = []
        component_list.sort()
        self.component_list = component_list

    def update_component_list(self, components):
        component_list = []
        for component in components:
            component_list.append(component)
        component_list.sort()

        for component in component_list:
            if component not in self.component_list:
                self.component_list.append(component)

    def update_locale_list(self, components):
        for component in components:
            for locale in components[component][KEY_LOCALES]:
                if locale not in self.locale_list:
                    self.locale_list.append(locale)

    def _mix_list(self, target, first, second):
        ar = [first, second]
        for from_list in ar:
            if from_list:
                for one in from_list:
                    if one not in target:
                        target.append(one)

    def mix(self, first, second):
        self._mix_list(self.locale_list, first.locale_list, second.locale_list)
        self._mix_list(self.component_list, first.component_list, second.component_list)


class SingletonReleaseBase:

    def __init__(self):
        self.cache_path = None
        self.scope = None
        self.logger = None
        self.interval = 0
        self.try_wait = 0

        self.local_scope = SingletonReleaseScopeInfo()
        self.remote_scope = SingletonReleaseScopeInfo()

    def get_locale_in_scope(self, singletonLocale, component):
        if not component:
            return None

        mix_scope = SingletonReleaseScopeInfo()
        mix_scope.mix(self.local_scope, self.remote_scope)
        if component not in mix_scope.component_list:
            return None

        relateLocale = singletonLocale.get_relate_locale(mix_scope.locale_list)
        return relateLocale

    def set_logger(self, logger):
        self.logger = logger
        self.log('')
        self.log('--- start --- python --- {0}'.format(sys.version.split('\n')[0]))
        self.log('--- release --- {0} --- {1} --- {2} ---'.format(self.cfg.product, self.cfg.version, time.time()))

    def log(self, text, log_type=LOG_TYPE_INFO):
        SysUtil.log(self.logger, text, log_type)

    def _init_logger(self, log_file):
        self.set_logger(SysUtil.init_logger(self.cfg))

    def _get_scope(self):
        if self.cache_path:
            self.remote_scope.update_locale_list_from_path(self.cache_path)
            self.remote_scope.update_component_list_from_path(self.cache_path)
        if not self.cfg.remote_url:
            return

        th = SingletonUpdateThread(self.task, self)
        if not self.remote_scope.locale_list or self.cfg.is_online_supported:
            th.do_work()
        else:
            pybase.do_multitask(th, self.cfg.need_async)


class SingletonReleaseForCache(SingletonReleaseBase):

    def __init__(self):
        SingletonReleaseBase.__init__(self)

        self.update = SingletonUpdate(self)

        self.remote_pool = {}
        self.source_pool = {}

        self.bykey = None

        self.bundle_handled = {}

    def get_use_locale(self, locale, asSource):
        pool = self.source_pool if asSource else self.remote_pool
        useLocale = pool.get(locale)
        if useLocale is None:
            singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
            useLocale = singletonLocale.find_item(pool, 1)

            if useLocale is None:
                useLocale = SingletonUseLocale(self, singletonLocale, self.cfg.source_locale, asSource)

            for one in useLocale.singletonLocale.get_near_locale_list():
                if one not in pool:
                    pool[one] = useLocale
        return useLocale

    def _init_forcache(self):
        if self.bykey:
            return

        self.bykey = SingletonByKey(self.cfg, self._is_different)

        self.useSourceLocale = self.get_use_locale(self.cfg.source_locale, True)
        self.useSourceRemote = self.get_use_locale(self.cfg.source_locale, False)
        # follow above
        if self.cfg.local_url:
            self.load_local_bundle(self.cfg.source_locale, None)

        self.useDefaultLocale = None
        if self._is_different:
            self.useDefaultLocale = self.get_use_locale(self.cfg.default_locale, False)

        self._get_scope()

        if not self.cfg.is_online_supported:
            self._check_load_on_startup(False)

    def _get_remote_resource(self, locale, component):
        if not self.remote_scope.locale_list or not self.remote_scope.component_list:
            return None
        singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
        if not singletonLocale.is_in_locale_list(self.remote_scope.locale_list):
            return None
        if component not in self.remote_scope.component_list:
            return None

        useLocale = self.get_use_locale(locale, False)
        component_obj = useLocale.get_component(component, True)

        component_obj.task.check()
        return component_obj

    def _check_with_key(self, message, sourceInCode, key):
        if message is None:
            if sourceInCode is not None:
                if self.cfg.pseudo:
                    message = self.bykey.add_pseudo(sourceInCode)
                else:
                    message = sourceInCode
            else:
                message = key
        return message

    def _get_message(self, component, key, locale):
        if not key or not locale:
            return None

        if not self.bykey._onlyByKey and not component:
            return None

        componentIndex = self.bykey.get_component_index(component)
        if componentIndex >= 0:
            combineKey = ClientUtil.get_combine_key(locale, component, 'handle', 'bundle')
            if combineKey not in self.bundle_handled:
                if self.cfg.is_online_supported:
                    self.useSourceRemote.get_component(component, True)

                useLocale = self.get_use_locale(locale, False)
                componentObj = useLocale.get_component(component, True)
                if componentObj:
                    self.bundle_handled[combineKey] = True

                if self.useDefaultLocale:
                    self.useDefaultLocale.get_component(component, True)

        localeItem = self.bykey.get_locale_item(locale, False)
        found = self.bykey.get_string(key, componentIndex, localeItem, True)
        return found

    def _get_source_msg(self, component, key):
        componentIndex = self.bykey.get_component_index(component)
        source = self.bykey.get_string(key, componentIndex, self.useSourceLocale.localeItem, False)
        if source is not None or not self.cfg.is_online_supported:
            return source

        source = self._get_message(component, key, self.cfg.source_locale)
        return source

    def _get_raw_msg(self, component, key, sourceInCode, locale, items):
        useLocale = self.get_use_locale(locale, False)
        if useLocale.is_source_locale:
            if sourceInCode is not None:
                return sourceInCode
            source = self._get_source_msg(component, key)
            return self._check_with_key(source, None, key)

        if not self.cfg.pseudo:
            source = self._get_source_msg(component, key)
            if sourceInCode is not None and source is not None and source != sourceInCode:
                return sourceInCode

        msg = self._get_message(component, key, locale)
        return self._check_with_key(msg, sourceInCode, key)

    def _format_by_map(self, text, map):
        return text.format(**map)

    def format(self, locale, text, array):
        if 'icu' == self.cfg.format_type:
            return SgtnIcu.format(locale, text, array)
        return text.format(*array)


class SingletonReleaseInternal(SingletonReleaseForCache):

    def __init__(self):
        SingletonReleaseForCache.__init__(self)

        self._is_loaded_on_startup = False

    def set_config(self, cfg):
        if not cfg:
            return

        self.cfg = cfg
        # follow above
        self.api = SingletonApi(self)

        self._init_logger(cfg)

        if cfg.cache_path:
            self.cache_path = os.path.join(cfg.cache_path, self.cfg.product, self.cfg.version)
            self.log('--- cache path --- {0} ---'.format(self.cache_path))

        self.interval = cfg.cache_expired_time
        self.try_wait = cfg.try_wait

        self.remote_default_locale = self.get_locale_supported(self.cfg.default_locale)
        self.remote_source_locale = self.get_locale_supported(self.cfg.source_locale)
        self._is_different = self.remote_default_locale != self.remote_source_locale

        self.task = SingletonAccessRemoteTask(self, self)
        # follow above
        self._init_forcache()

    def get_from_remote(self):
        self.task.last_time = time.time()

        try:
            # get locale list
            scope = self.update.get_scope_item(self.api.get_localelist_api(), KEY_LOCALES, 'locale_list.json')
            if scope:
                self.remote_scope.locale_list = scope

            # get component list
            scope = self.update.get_scope_item(self.api.get_componentlist_api(), KEY_COMPONENTS, 'component_list.json')
            if scope:
                self.remote_scope.component_list = scope
        except SgtnException as e:
            pass

        self._check_load_on_startup(True)

        # follow above
        if self.cfg.local_url is None:
            for component in self.remote_scope.component_list:
                component_obj = self.useSourceLocale.get_component(component, True)

    def get_data_count(self):
        if not self.remote_scope.locale_list or not self.remote_scope.component_list:
            return 0
        return len(self.remote_scope.locale_list) + len(self.remote_scope.component_list)

    def load_local_bundle(self, locale, component):
        singletonLocale = SingletonLocaleUtil.get_singleton_locale(locale)
        isSource = singletonLocale.compare(self.useSourceLocale.singletonLocale)
        self.update.load_local_message(singletonLocale, component, isSource, False)

    def _check_load_on_startup(self, by_remote):
        if not self._is_loaded_on_startup and self.cfg.is_load_on_startup:
            self._is_loaded_on_startup = True
            if by_remote:
                self._load_remote_on_startup()
            else:
                self._load_local_on_startup([])

    def _load_local_on_startup(self, done):
        for component in self.local_scope.component_list:
            if component in done:
                continue

            for locale in self.local_scope.locale_list:
                self.load_local_bundle(locale, component)

    def _load_remote_on_startup(self):
        for component in self.remote_scope.component_list:
            for locale in self.remote_scope.locale_list:
                useLocale = self.get_use_locale(locale, False)
                component_obj = useLocale.get_component(component, True)
                component_obj.get_from_remote()

        self._load_local_on_startup(self.remote_scope.component_list)


class SingletonRelease(SingletonReleaseInternal, Release, Translation):

    def __init__(self):
        SingletonReleaseInternal.__init__(self)

    def get_config(self):
        """method of Release"""
        return self.cfg

    def get_translation(self):
        """method of Release"""
        return self

    def get_locale_supported(self, locale):
        """method of Translation"""
        return SysUtil.get_fallback_locale(locale)

    def get_locale_strings(self, locale, as_source):
        """method of Translation"""
        useLocale = self.get_use_locale(locale, as_source)
        return useLocale.get_all_strings()

    def get_string(self, component, key, **kwargs):
        """method of Translation"""
        self.task.check()

        sourceInCode = kwargs.get(KEY_SOURCE) if kwargs else None
        locale = kwargs.get(KEY_LOCALE) if kwargs else None
        items = kwargs.get(KEY_ITEMS) if kwargs else None

        if not locale:
            locale = SingletonReleaseManager().get_current_locale()

        text = self._get_raw_msg(component, key, sourceInCode, locale, items)
        if text and items:
            if isinstance(items, list):
                text = self.format(locale, text, items)
            elif isinstance(items, dict):
                text = self._format_by_map(text, items)

        if text is None:
            text = key
        return text

    def _get_locale_pool(self, obj):
        source_locale = self.cfg.source_locale
        source_near_list = SingletonLocaleUtil.get_singleton_locale(source_locale).get_near_locale_list()
        other_source_locales = []

        obj.actual_source_locale = None
        obj.locale_pool = {}
        if self.local_scope.locale_list:
            if source_locale in self.local_scope.locale_list:
                obj.actual_source_locale = source_locale

            for one in self.local_scope.locale_list:
                obj.locale_pool[one] = {_FROM_LOCAL: True, _FROM_REMOTE: False}

                if obj.actual_source_locale is None and one in source_near_list:
                    obj.actual_source_locale = one

        # must follow local scope
        if self.remote_scope.locale_list:
            if obj.actual_source_locale is None and source_locale in self.remote_scope.locale_list:
                obj.actual_source_locale = source_locale

            for one in self.remote_scope.locale_list:
                if one not in obj.locale_pool:
                    obj.locale_pool[one] = {_FROM_LOCAL: False}
                obj.locale_pool[one][_FROM_REMOTE] = True

                if obj.actual_source_locale is None and one in source_near_list:
                    obj.actual_source_locale = one

        for one in obj.locale_pool:
            if one != obj.actual_source_locale and one in source_near_list:
                other_source_locales.append(one)

        for one in other_source_locales:
            del obj.locale_pool[one]

    def get_locales(self, display_locale=None):
        obj = Data()
        self._get_locale_pool(obj)

        locales = {}
        for one in obj.locale_pool:
            locales[one] = {}
            if one == obj.actual_source_locale:
                locales[one]['as'] = 'source'
                locales[one]['from'] = 'local' if obj.locale_pool[one][_FROM_LOCAL] else 'remote'
            else:
                locales[one]['from'] = 'remote' if obj.locale_pool[one][_FROM_REMOTE] else 'local'

            display_name = SgtnIcu.get_locale_display_name(one, display_locale)
            if display_name:
                locales[one]['display'] =  display_name

        return locales


class SingletonReleaseManager(object):
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = object.__new__(cls)
            cls._instance._init()
        return cls._instance

    def add_config_file(self, config_file, outside_config=None):
        """for I18N"""
        config_text = FileUtil.read_text_file(config_file)
        config_data = FileUtil.parse_datatree(config_text)
        if outside_config:
            for key in outside_config:
                config_data[key] = copy.deepcopy(outside_config[key])

        base_path = os.path.dirname(os.path.realpath(config_file))
        cfg = self.add_config(base_path, config_data)
        return cfg

    def add_config(self, base_path, config_data):
        """for I18N"""
        if not config_data:
            return
        cfg = SingletonConfig(base_path, config_data)
        release_obj = self.get_release(cfg.product, cfg.version)
        if release_obj is None:
            self._create_release(cfg)
        return cfg

    def set_current_locale(self, locale):
        """for I18N"""
        pybase.set_current_locale(locale)

    def get_current_locale(self):
        """for I18N"""
        locale = pybase.get_current_locale()
        if locale is None:
            locale = LOCALE_DEFAULT
        return locale

    def get_release(self, product, version):
        """for I18N"""
        if not product or not version:
            return None

        releases = self._products.get(product)
        if releases is None:
            return None

        return releases.get(version)

    def _init(self):
        self._products = {}

    def _create_release(self, cfg):
        if not cfg or not cfg.product or not cfg.version:
            return

        releases = self._products.get(cfg.product)
        if releases is None:
            self._products[cfg.product] = {}
            releases = self._products.get(cfg.product)

        release_obj = releases.get(cfg.version)
        if release_obj is None:
            release_obj = SingletonRelease()
            release_obj.set_config(cfg)
            releases[cfg.version] = release_obj
