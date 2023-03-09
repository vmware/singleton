import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.10.251'
COMPONENT = 'about'
LOCALE = 'fr'
Config_files = 'sample_online_localsource.yml'

# singleton\test\TRANSLATION
__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')


class TestOnlineLocaleSource:

    @pytest.mark.ci1
    def test_l1(self):
        print("the key only in offline")
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file:///../resources/l10n/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        tran1 = translation.get_string("about", "about.offline")
        print(tran1)

        tran2 = translation.get_string("about", "about.message", source="Your application description page. parameter",
                                       locale="en")
        assert tran2 == "Your application description page. parameter"

    def test_func_l2(self):
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file:///../resources/l10n/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        current = I18N.get_current_locale()
        print(current)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        tran2 = translation.get_string("about", "about.message", locale="en")
        assert tran2 == "Your application description page. source"

    @pytest.mark.ci1
    def test_l3(self):
        print("the key only in offline")
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file://d:/D1/l10ntest/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        current = I18N.get_current_locale()
        print(current)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("intest","intest.offline")
        # assert tran1 == "Contact1 offline"
        tran2 = translation.get_string("about", "about.message.notexist",
                                       source="Your application description page. parameter", locale="en")
        assert tran2 == "Your application description page. parameter"

    @pytest.mark.ci1
    def test_l4(self):
        print("the key only in online")
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file://d:/D1/l10ntest/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale("fr")
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("intest","intest.online",source = "Contact1 online")
        tran1 = translation.get_string("about", "about.message.notexist1", locale="en")
        assert tran1 == "about.message.notexist1"

    @pytest.mark.ci1
    def test_l5(self):
        print("the key both exist in online and offline ")
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file://d:/D1/l10ntest/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)

        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # trans1 = translation1.get_string("about", "about.message")
        # assert trans1 == "test fr key"
        # trans3 = translation1.get_string("about", "about.message", locale ='en')
        # assert trans3 == "Your application description page."
        tran2 = translation1.get_string("about", "about.message", source="Your application description page. parameter",
                                        locale='fr')
        assert tran2 == "@@Your application description page. parameter@@"
        # time.sleep(3)
        # assert tran2 == "test ja key"
        # trans3 = translation1.get_string("about", "about.message", locale = 'de')
        # print(trans3)
        # assert trans1 == "About1 fr"

    def test_func_l6(self):
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file://d:/D1/l10ntest/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)

        rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = rel.get_translation()
        # trans1 = translation1.get_string("about", "about.message")
        # assert trans1 == "test fr key"
        # trans3 = translation1.get_string("about", "about.message", locale ='en')
        # assert trans3 == "Your application description page."
        tran2 = translation1.get_string("about", "about.description",
                                        source="Your application description page. parameter", locale='fr')
        assert tran2 == "@@Your application description page. parameter@@"

    @pytest.mark.citest
    def test_l7(self):
        print("the key both exist in online and offline: source error")
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file://d:/D1/l10ntest/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # trans1 = translation1.get_string("intest","intest.about",source = "About123", locale = "ja")
        # assert trans1 == "About1"
        # tran1 = translation1.get_string("intest","intest.offline",locale = "ja")
        # assert tran1 == "Contact1 offline"
        trans2 = translation1.get_string("intest", "intest.home1234",
                                         source="Your application description page. parameter1", locale="fr")
        assert trans2 == "@@Your application description page. parameter1@@"
        # time.sleep(0.1)
        # trans3 = translation1.get_string("intest","intest.home", locale = "ja")
        # assert trans3 == "Homexx"
        # print(trans3)

    @pytest.mark.citest
    def test_l8(self):
        print("the key both exist in online and offline: source error")
        file: Path = __CONFIG__.joinpath('sample_online_localsource.yml')
        outside_config = {"l10n_version": "1.10.251", "pseudo": True,
                          "offline_resources_base_url": "file://d:/D1/l10ntest/PythonClient/1.10.251"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # trans1 = translation1.get_string("intest","intest.about",source = "About123", locale = "ja")
        # assert trans1 == "About1"
        # tran1 = translation1.get_string("intest","intest.offline",locale = "ja")
        # assert tran1 == "Contact1 offline"
        trans2 = translation1.get_string("intest", "intest.home1234", locale="fr")
        assert trans2 == "intest.home1234"


if __name__ == '__main__':
    # pytest.main(['-s', '-k test_l1xzxzxz'])
    pytest.main(['-s', '-k TestOnlineLocaleSource'])
