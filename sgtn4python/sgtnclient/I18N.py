# -*-coding:UTF-8 -*-
#
# Copyright 2020-2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

import os
import sys

_libPath = os.path.dirname(__file__)
if _libPath not in sys.path:
    sys.path.append(_libPath)


_release_manager = None


def _get_release_manager():
    global _release_manager
    if _release_manager is None:
        from sgtn_client import SingletonReleaseManager
        _release_manager = SingletonReleaseManager()
    return _release_manager


class Interface(object):
    NOT_IMP_EXCEPTION = Exception('NotImplementedException')


class Config(Interface):
    """Config interface"""

    def get_config_data(self):
        raise self.NOT_IMP_EXCEPTION

    def get_info(self):
        raise self.NOT_IMP_EXCEPTION


class Release(Interface):
    """Release interface"""

    def get_config(self):
        """get config interface Config"""
        raise self.NOT_IMP_EXCEPTION

    def get_translation(self):
        """get translation interface Translation"""
        raise self.NOT_IMP_EXCEPTION

    def set_logger(self, logger):
        """Set logger derived from Logger to a release object."""
        raise self.NOT_IMP_EXCEPTION


class Translation(Interface):
    """Translation interface"""

    def get_string(self, component, key, **kwargs):
        raise self.NOT_IMP_EXCEPTION

    def get_locale_strings(self, locale, as_source):
        raise self.NOT_IMP_EXCEPTION

    def get_locale_supported(self, locale):
        raise self.NOT_IMP_EXCEPTION

    def format(self, locale, text, array):
        """Format 'text' with values of 'array' according to locale."""
        raise self.NOT_IMP_EXCEPTION

    def get_locales(self, display_locale=None):
        raise self.NOT_IMP_EXCEPTION


class Logger(Interface):

    def log(self, text, log_type):
        raise self.NOT_IMP_EXCEPTION


def add_config_file(config_file, outside_config=None):
    """add config by filename and an outside config above itself"""
    return _get_release_manager().add_config_file(config_file, outside_config)


def add_config(base_path, config_data):
    """add config data with a base path"""
    return _get_release_manager().add_config(base_path, config_data)


def set_current_locale(locale):
    """set current locale"""
    _get_release_manager().set_current_locale(locale)


def get_current_locale():
    """get current locale"""
    return _get_release_manager().get_current_locale()


def get_release(product, version):
    """get release interface Release"""
    return _get_release_manager().get_release(product, version)
