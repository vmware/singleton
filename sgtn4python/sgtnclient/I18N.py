# -*-coding:UTF-8 -*-
#
# Copyright 2020-2021 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import sys

_libPath = os.path.dirname(__file__)
if _libPath not in sys.path:
    sys.path.append(_libPath)

from sgtn_util import FileUtil


_client_manager = None
def _get_client_manager():
    global _client_manager
    if _client_manager is None:
        from sgtn_client import SingletonClientManager
        _client_manager = SingletonClientManager()
    return _client_manager


class Config(object):
    """Config interface"""

    def get_config_data(self):
        raise Exception()

    def get_info(self):
        raise Exception(NOT_IMP_EXCEPTION)

class Translation(object):
    """Translation interface"""

    def get_string(self, component, key, **kwargs):
        raise Exception(NOT_IMP_EXCEPTION)

    def get_locale_strings(self, locale):
        raise Exception(NOT_IMP_EXCEPTION)

    def get_locale_supported(self, locale):
        raise Exception(NOT_IMP_EXCEPTION)

class Release(object):
    """Release interface"""

    def get_config(self):
        """get config interface Config"""
        raise Exception(NOT_IMP_EXCEPTION)

    def get_translation(self):
        """get translation interface Translation"""
        raise Exception(NOT_IMP_EXCEPTION)


def add_config_file(config_file, replaceMap = None):
    return _get_client_manager().add_config_file(config_file, replaceMap)

def add_config(base_path, config_data):
    return _get_client_manager().add_config(base_path, config_data)

def set_current_locale(locale):
    _get_client_manager().set_current_locale(locale)

def get_current_locale():
    """get string of locale"""
    return _get_client_manager().get_current_locale()

def get_release(product, version):
    """get release interface Release"""
    return _get_client_manager().get_release(product, version)

