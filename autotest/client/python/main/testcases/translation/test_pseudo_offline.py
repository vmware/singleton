import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '2.0.0'
COMPONENT = 'about'
LOCALE = 'fr'
Config_files = 'sample_offline_disk.yml'

__CONFIG__ = Path(__file__).parent.joinpath('config')


class TestPseudoOffline:

    def test_offline_pseudo(self):
        """
        offline mode: pseudo True.
        """
        file: Path = __CONFIG__.joinpath('offlineDiskWithCompare.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "2.0.0", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # not en and pseudo, return message.properties source and add @@
        tran1 = translation.get_string("about", "about.message")
        assert tran1 == "@@Your application description page.@@"

        # not en and pseudo, if key miss, return key
        trans2 = translation.get_string("about", "about.message123", locale="zh-Hans-CN")
        assert trans2 == "about.message123"

        # if non en and pseudo , the locale and source disable!
        tran2 = translation.get_string("about", "about.message", locale='de', source='active')
        assert tran2 == "@@Your application description page.@@"

        # if en pseudo return source direct
        I18N.set_current_locale('en')
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "Your application description page."

        # if has source, return source
        tran3 = translation.get_string("about", "about.message", source='application')
        assert tran3 == "application"

        # key no exist
        tran3 = translation.get_string("about", "about.message123")
        assert tran3 == "about.message123"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestPseudoOffline'])

"""
__all__ = ['update_wrapper', 'wraps', 'WRAPPER_ASSIGNMENTS', 'WRAPPER_UPDATES',
           'total_ordering', 'cache', 'cmp_to_key', 'lru_cache', 'reduce',
           'partial', 'partialmethod', 'singledispatch', 'singledispatchmethod',
           'cached_property']
"""
