import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.10.251'
COMPONENT = 'about'
LOCALE = 'fr'
Config_files = 'only_online.yml'

# singleton\test\TRANSLATION
__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')


class TestOnlinePseudo:

    # @pytest.mark.ci1
    # def test_l1xxxxxxxx(self):
    #     """
    #     """
    #     file: Path = __CONFIG__.joinpath('only_online.yml')
    #     outside_config = {"product": "PythonClient", "l10n_version": "1.10.251",
    #                       "online_service_url": "https://localhost:8090", "pseudo": True}
    #     I18N.add_config_file(file, outside_config)
    #     I18N.set_current_locale(LOCALE)
    #     rel = I18N.get_release(PRODUCT, VERSION)
    #     translation = rel.get_translation()
    #
    #     # not en and pseudo, return online latest.json ，if no latest.json, return key. latest must exist.
    #     tran2 = translation.get_string("about", "about.message", locale='fr')
    #     assert tran2 == "#@Your application description page. latest#@"
    #
    #     # en and pseudo, return latest.json. no add #@
    #     tran2 = translation.get_string("about", "about.message", locale='en')
    #     assert tran2 == "Your application description page. latest"
    #
    #     # if has source ,return source direct
    #     tran2 = translation.get_string("about", "about.message", source="Your application description page. parameter",
    #                                    locale='en')
    #     assert tran2 == "Your application description page. parameter"


    @pytest.mark.ci1
    def test_l3(self):
        print("online:component and key")
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "1.10.251",
                          "online_service_url": "https://localhost:8090", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        # I18N.set_current_locale(LOCALE)
        # current = I18N.get_current_locale()
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        # conf =self.rel.get_config()
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("about","about.message")
        # print(tran1)
        # assert tran1 == "test fr key"
        tran2 = translation.get_string("about", "about.message.noexistkey",
                                       source="Your application description page. parameter", locale='en')
        assert tran2 == "Your application description page. parameter"

    @pytest.mark.ci1
    def test_l4(self):
        print("online:component and key")
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "1.10.251",
                          "online_service_url": "https://localhost:8090", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        # I18N.set_current_locale(LOCALE)
        # current = I18N.get_current_locale()
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        # conf =self.rel.get_config()
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("about","about.message")
        # print(tran1)
        # assert tran1 == "test fr key"
        tran2 = translation.get_string("about", "about.message.noexistkey", locale='en')
        assert tran2 == "about.message.noexistkey"

    @pytest.mark.ci1
    def test_l5(self):
        print("online:component and key and source and locale ")
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "1.10.251",
                          "online_service_url": "https://localhost:8090", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("about", "about.message", source="Your application description page.45678910",
                                         locale="ja")
        # "Your application description page."
        assert trans1 == "@@Your application description page.45678910@@"
    #
    # @pytest.mark.ci1
    # def test_l6(self):
    #     print("online:component and key and source")
    #     file: Path = __CONFIG__.joinpath('only_online.yml')
    #     outside_config = {"product": "PythonClient", "l10n_version": "1.10.251",
    #                       "online_service_url": "https://localhost:8090", "pseudo": True}
    #     I18N.add_config_file(file, outside_config)
    #     I18N.set_current_locale(LOCALE)
    #     self.rel = I18N.get_release(PRODUCT, VERSION)
    #     translation1 = self.rel.get_translation()
    #     trans1 = translation1.get_string("about", "about.message", locale='en')
    #     assert trans1 == "#@Your application description page. latest#@"

    @pytest.mark.ci1
    def test_l7(self):
        print("online:component and key and source")
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "1.10.251",
                          "online_service_url": "https://localhost:8090", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("about", "about.message12345", source="Your application description page.abc")
        assert trans1 == "@@Your application description page.abc@@"

    @pytest.mark.ci1
    def test_l8(self):
        print("online:component and key and locale")
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "l10n_version": "1.10.251",
                          "online_service_url": "https://localhost:8090", "pseudo": True}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("about", "about.message34567", locale="ar")
        assert trans1 == "about.message34567"


if __name__ == '__main__':
    # pytest.main(['-s', '-k test_l1xxxxxxxx'])
    pytest.main(['-s', '-k TestOnlinePseudo'])
