from pathlib import Path

import pytest
from sgtnclient import I18N

from .utils import ModifyFileContext

PRODUCT = 'Cache'
VERSION = '1.0.1'
COMPONENT = 'about'
LOCALE = 'de'
CONFIG_FILE = 'offline_local.yml'

__CACHE__ = Path(__file__).parent
__CONFIG__ = __CACHE__.joinpath('config')
__RESOURCES__ = __CACHE__.joinpath('resources')


class TestCacheOfflineLocal:

    def test_offline_local_no_support_cache(self):
        """
        Offline Mode:
        1. no support cache_path
        2. memory cache still active. run as script can clear memory  cache.
        """
        file = __CONFIG__.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get local messages.properties
        tran1 = translation.get_string("about", "about.title", locale="en")
        assert tran1 == "About (Offline Source)"

        tran2 = translation.get_string("about", "about.message", locale="de")
        assert tran2 == "test de key"

        # modify and get_string will get new source.
        file_path = __RESOURCES__.joinpath("l10n", "Cache", "1.0.1", "about", "messages_fr.json")
        with ModifyFileContext(file_path):
            tran2 = translation.get_string("about", "about.message", locale="fr")
            assert tran2 == "1234"

        # memory cache, even modify source will get old value.
        file_path = __RESOURCES__.joinpath("l10n", "Cache", "1.0.1", "about", "messages_de.json")
        with ModifyFileContext(file_path):
            tran2 = translation.get_string("about", "about.message", locale="de")
            assert tran2 == "test de key"


if __name__ == '__main__':
    pytest.main(['-s', 'test_offline'])
