import time
from pathlib import Path

import pytest

from sgtn4python.sgtnclient import I18N
from utils import ModifyFileContext

PRODUCT = 'PythonClient'
VERSION = '8.0.0'
COMPONENT = 'about'
LOCALE = 'de'
CONFIG_FILE = 'sample_offline_disk.yml'


__CACHE__ = Path(__file__).parent
__CONFIG__ = __CACHE__.joinpath('config')
__RESOURCES__ = __CACHE__.joinpath('resources')


class TestCacheOffline:

    def test_offline_cache_no_change_always(self):
        """
        offline mode: cache can not refresh.
        """
        file = __CONFIG__.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        tran1 = translation.get_string("about", "about.message")
        assert tran1 == "test de key"

        file_path = __RESOURCES__.joinpath("l10n", "PythonClient", "8.0.0", "about", "messages_de.json")
        with ModifyFileContext(file_path):
            tran2 = translation.get_string("about", "about.message", locale='fr')
            assert tran2 == "test fr 123 key"

            time.sleep(11)
            tran3 = translation.get_string("about", "about.message")
            assert tran3 == "test de key"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestCacheOffline'])
