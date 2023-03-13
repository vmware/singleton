import os
import shutil
import time
from pathlib import Path

import pytest
from sgtnclient import I18N

PRODUCT = "PythonClient"
VERSION = "1.0.0"
COMPONENT = "about"
LOCALE = "de"

_CONFIG_ = Path(__file__).parent.joinpath("config")


class TestCacheOnline:

    def test_online_cache(self, delete_cache):
        """no .cache"""
        config_file = "cacheOnlineWithoutCacheControl.yml"
        file: Path = _CONFIG_.joinpath(config_file)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)

        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get old value and request server. a real request come in server
        message = translation.get_string("about", "about.message")
        assert message == "test de key"

    def test_online_cache_in_effective(self, update_cache):
        config_file = "cacheOnlineWithoutCacheControl.yml"
        file: Path = _CONFIG_.joinpath(config_file)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)

        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # read form cache
        message = translation.get_string("about", "about.message")
        assert message == "test de key(Cached)"
        time.sleep(4)

        # cache not expire
        # get old value and not update cache
        message = translation.get_string("about", "about.message")
        assert message == "test de key(Cached)"
        time.sleep(2)

        # cache expire
        # get old value and update cache
        message = translation.get_string("about", "about.message")
        assert message == "test de key(Cached)"
        time.sleep(3)

        # get memory cache updated
        message = translation.get_string("about", "about.message")
        assert message == "test de key"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestCacheOnline'])
