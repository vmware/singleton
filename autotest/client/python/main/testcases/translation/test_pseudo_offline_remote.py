import pytest
from pathlib import Path
from sgtn4python.sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '2.0.0'
COMPONENT = 'about'
LOCALE = 'fr'
Config_files = 'sample_offline_remote.yml'

# singleton\test\TRANSLATION
__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')


class TestPseudoOfflineRemote:

    @pytest.mark.ci1
    def test_offline_remote_add_configuration(self):
        """
        offline mode: add config
        """
        file: Path = __CONFIG__.joinpath('sample_offline_remote.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "2.0.0", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale('fr')
        current_locale = I18N.get_current_locale()
        assert current_locale == 'fr'

        rel = I18N.get_release(PRODUCT, VERSION)
        conf = rel.get_config()
        config_info = conf.get_info()

        assert config_info["product"] == "PythonClient"
        assert config_info["version"] == "2.0.0"
        assert config_info["pseudo"] is True

    @pytest.mark.ci1
    def test_offline_remote_pseudo(self):
        """
        offline mode: pseudo True. 通过配置文件的source_resources_path: 找到源文件。返回，并且加上@@，如果找不到源文件，返回key。 如果是en，source direct
        """
        file: Path = __CONFIG__.joinpath('sample_offline_remote.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "2.0.0", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # not en and pseudo, return message.properties source and add @@
        tran1 = translation.get_string("about", "about.message")
        assert tran1 == "@@Your application description page 123.@@"

        # not en and pseudo, if key miss, return key
        trans2 = translation.get_string("about", "about.message123", locale="zh-Hans-CN")
        assert trans2 == "about.message123"

        # if non en and pseudo , the locale and source disable!
        tran2 = translation.get_string("about", "about.message", locale='de', source='active')
        assert tran2 == "@@Your application description page 123.@@"

        # if en pseudo return source direct
        I18N.set_current_locale('en')
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "Your application description page 123."

        # if has source, return source
        tran3 = translation.get_string("about", "about.message", source='application')
        assert tran3 == "application"

        # key no exist
        tran3 = translation.get_string("about", "about.message123")
        assert tran3 == "about.message123"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestPseudoOfflineRemote'])
