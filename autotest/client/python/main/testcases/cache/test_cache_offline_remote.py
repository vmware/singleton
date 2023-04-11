from pathlib import Path

import pytest
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'de'
CONFIG_FILE = 'offline_remote.yml'

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestCacheOfflineRemote:

    def test_offline_remote_no_support_cache_path(self, update_cache):
        """
        Offline Remote Mode:
        1. no support cache_path
        2. memory cache still active. run as script can clear memory  cache.
        """
        file = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get remote messages_de.json
        message = translation.get_string("about", "about.message", locale="de")
        assert message == "test de key(Offline Disk)"


if __name__ == '__main__':
    pytest.main(['-s', 'TestCacheOfflineRemote'])
