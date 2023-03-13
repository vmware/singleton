import os
import time
from pathlib import Path

import pytest
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'de'
CONFIG_FILE = 'cacheMixedMode.yml'

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestMixedCacheOnlineSuccess:

    @pytest.mark.cache1
    def test_mixed_cache_online_success(self, update_cache):
        """
        Mixed Mode:
        1. if Online can get component + locale, use online and cached
        """
        t1 = time.time()
        file = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get_source from server and save in localCache.
        # .cache not expire. use cache
        tran1 = translation.get_string("about", "about.message", locale="de")
        assert tran1 == "test de key(Cached)"

        # wait cache expire
        # return old value and cache updated
        time.sleep(21 - int(time.time()) + t1)
        tran1 = translation.get_string("about", "about.message", locale="de")
        assert tran1 == "test de key(Cached)"

        # get memory cache updated
        time.sleep(3)
        tran1 = translation.get_string("about", "about.message", locale="de")
        assert tran1 == "test de key"


class TestMixedCacheOnlineFail:

    def test_mixed_cache_online_fail_use_cache(self, update_cache):
        """
        Mixed Mode:
        1. if Online can get component + locale, use online and cached
        """
        t1 = time.time()
        outside_config = {"online_service_url": "https://localhost:809011"}
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # online 4xx/5xx
        # if .cache exist .use cache
        tran1 = translation.get_string("about", "about.message", locale="de")
        assert tran1 == "test de key(Cached)"

        # wait cache expire
        time.sleep(21 - int(time.time()) + t1)
        tran1 = translation.get_string("about", "about.message", locale="de")
        assert tran1 == "test de key(Cached)"

        # wait cache expire
        time.sleep(5)
        tran1 = translation.get_string("about", "about.message", locale="de")
        # assert tran1 == "test de key(Offline Disk)"
        assert tran1 == "test de key(Offline Disk)"


if __name__ == '__main__':
    pytest.main(['-s', 'test_mixed_cache.py'])
