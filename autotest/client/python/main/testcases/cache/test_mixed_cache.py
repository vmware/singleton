import time
from pathlib import Path

import pytest
from sgtnclient import I18N

from .utils import ContextModifyCacheFr1

PRODUCT = 'Cache'
VERSION = '1.0.2'
COMPONENT = 'about'
LOCALE = 'de'
CONFIG_FILE = 'mixed_online_local.yml'

__CACHE__ = Path(__file__).parent
__CONFIG__ = __CACHE__.joinpath('config')
__RESOURCES__ = __CACHE__.joinpath('resources')


class TestMixedCacheOnlineSuccess:

    @pytest.mark.cache1
    def test_mixed_cache_online_success(self):
        """
        Mixed Mode:
        1. if Online can get component + locale, use online and cached
        """
        file = __CONFIG__.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get_source from server and save in localCache.
        tran1 = translation.get_string("about", "about.message", locale="fr")
        assert tran1 == "test fr key"

        # # return from cache. no request server
        # time.sleep(9)
        # tran1 = translation.get_string("about", "about.message", locale="fr")
        # assert tran1 == "test fr key"


# class TestMixedCacheOnlineFail:
#
#     def test_mixed_cache_online_fail_use_cache(self):
#         """
#         Mixed Mode:
#         1. if Online can get component + locale, use online and cached
#         """
#         outside_config = {"online_service_url": "https://localhost:809011"}
#         file: Path = __CONFIG__.joinpath(CONFIG_FILE)
#         I18N.add_config_file(file, outside_config)
#         I18N.set_current_locale(LOCALE)
#         rel = I18N.get_release(PRODUCT, VERSION)
#         translation = rel.get_translation()
#
#         cache = __CACHE__.joinpath(".cache").joinpath("Cache").joinpath("1.0.2").joinpath("about").joinpath(
#             "messages_fr.json")
#
#         # if online fail ,use cache
#         with ContextModifyCacheFr1(cache):
#             tran1 = translation.get_string("about", "about.message", locale="fr")
#             assert tran1 == "test fr key (CACHED)", cache.read_text()
#
#         time.sleep(2)
#         # use local  ？？ expire then from local？？
#         tran1 = translation.get_string("about", "about.message", locale="fr")
#         assert tran1 == "test fr key (CACHED)"
#
#         # if update
#         tran1 = translation.get_string("about", "about.message", locale="de")
#         assert tran1 == "test de key （Offline Local）"


if __name__ == '__main__':
    pytest.main(['-s', 'test_mixed_cache.py'])
