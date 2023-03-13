import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'fr'
Config_files = 'sample_offline_disk.yml'

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestPseudoOfflineDisk:

    def test_offline_pseudo(self):
        """
        offline mode: pseudo True.
        """
        file: Path = _CONFIG_.joinpath('offlineDiskWithCompare.yml')
        outside_config = {"l10n_version": "1.0.0", "pseudo": True}
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
