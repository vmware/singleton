from pathlib import Path

import pytest
from sgtnclient import I18N

PRODUCT = 'Cache'
VERSION = '1.0.1'
COMPONENT = 'about'
LOCALE = 'de'
CONFIG_FILE = 'offline_remote.yml'

__CACHE__ = Path(__file__).parent
__CONFIG__ = __CACHE__.joinpath('config')
__RESOURCES__ = __CACHE__.joinpath('resources')


class TestCacheOfflineRemote:
    CONFIG_FILE = 'sample_offline_disk.yml'

    @pytest.mark.skip
    def test_offline_remote_no_support_cache(self):
        """
        Offline Remote Mode:
        1. no support cache_path
        2. memory cache still active. run as script can clear memory  cache.
        """
        file = __CONFIG__.joinpath(self.CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get local messages.properties
        tran1 = translation.get_string("about", "about.title")
        assert tran1 == "About (Offline Source)"

        tran2 = translation.get_string("about", "about.message", locale="de")
        assert tran2 == "test de key"


if __name__ == '__main__':
    pytest.main(['-s', 'TestCacheOfflineRemote'])
