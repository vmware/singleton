import time

import pytest
from pathlib import Path
from sgtn4python.sgtnclient import I18N
from utils import ContextStringsDe6

PRODUCT = 'PythonClient'
VERSION = '1.1.1.1.1'
COMPONENT = 'about'
LOCALE = 'fr'
Config_files = 'sgtn_online_offline_before.yml'

# singleton\test\TRANSLATION
__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')


class TestOnlineWithOffline:

    @pytest.mark.ci1
    def test_l1(self):
        print("the key only in offline")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        current = I18N.get_current_locale()
        print(current)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("intest","intest.offline")
        # assert tran1 == "Contact1 offline"
        tran2 = translation.get_string("intest", "intest.offline", locale="ja")
        assert tran2 == "offline12345"

    @pytest.mark.cache1
    def test_cache(self):
        print("cache expired and update")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale("de")
        rel = I18N.get_release(PRODUCT, VERSION)
        with ContextStringsDe6(base_url="http://localhost:8091"):
            translation = rel.get_translation()
            tran1 = translation.get_string("about", "about.message")
            assert tran1 == "test__de_value"

        time.sleep(35)
        tran2 = translation.get_string("about", "about.message")
        assert tran2 == "test__de_value"
        time.sleep(1)
        tran2 = translation.get_string("about", "about.message")
        assert tran2 == "test__de_value"
        time.sleep(1)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test__de_value_change"

    # @pytest.mark.citest
    # def test_l4(self):
    #     print("the key both exist in online and offline: source error")
    #     I18N.set_current_locale(LOCALE)
    #     self.rel = I18N.get_release(PRODUCT, VERSION)
    #     translation = self.rel.get_translation()
    #     trans2 = translation.get_string("intest","intest.home", locale = "ja")
    #     # assert trans2 == "Homexxxx1"
    #     print(trans2)
    #     time.sleep(3)
    #     trans3 = translation.get_string("intest","intest.home", locale = "ja")
    #     ## assert trans3 == "Homexxxx"
    #     print(trans3)
    #
    @pytest.mark.ci1
    def test_l2(self):
        print("the key only in online")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale("fr")
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("intest","intest.online",source = "Contact1 online")
        tran1 = translation.get_string("intest", "intest.online")
        assert tran1 == "Contact1 fr online"

    @pytest.mark.ci1
    def test_l3(self):
        print("the key both exist in online and offline ")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)

        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # trans1 = translation1.get_string("about", "about.message")
        # assert trans1 == "test fr key"
        # trans3 = translation1.get_string("about", "about.message", locale ='en')
        # assert trans3 == "Your application description page."
        tran2 = translation1.get_string("about", "about.message", locale='fr-CA')
        print(tran2)
        assert tran2 == "test fr key"
        # time.sleep(3)
        # assert tran2 == "test ja key"
        # trans3 = translation1.get_string("about", "about.message", locale = 'de')
        # print(trans3)
        # assert trans1 == "About1 fr"

    @pytest.mark.citest
    def test_l4(self):
        print("the key both exist in online and offline: source error")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # trans1 = translation1.get_string("intest","intest.about",source = "About123", locale = "ja")
        # assert trans1 == "About1"
        # tran1 = translation1.get_string("intest","intest.offline",locale = "ja")
        # assert tran1 == "Contact1 offline"
        # trans2 = translation1.get_string("intest","intest.home", locale = "fr")
        trans2 = translation1.get_string("intest", "intest.home", locale="fr")
        assert trans2 == "Home12"
        # time.sleep(0.1)
        # trans3 = translation1.get_string("intest","intest.home", locale = "ja")
        # assert trans3 == "Homexx"
        # print(trans3)

    @pytest.mark.bug
    def test_l5(self):
        print("the key both exist in online and offline ")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # trans1 = translation1.get_string("intest","intest.about",source = "About12")
        # trans1 = translation1.get_string("intest", "intest.about")
        # assert trans1 == "About1 fr"
        trans2 = translation1.get_string("intest", "intest.about", locale="ja")
        assert trans2 == "About1 ja"
        trans3 = translation1.get_string("intest", "intest.about", source="About1")
        assert trans3 == "About1 fr"
        trans4 = translation1.get_string("intest", "intest.about", source="About1", locale="ja")
        assert trans4 == "About1 ja"

    @pytest.mark.ci1
    def test_component_only_in_offline(self):
        """
        mix mode: component only in offline , server return 500, use offline
        """
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = rel.get_translation()
        trans1 = translation1.get_string("common", "common.about")
        assert trans1 == "about1"

    @pytest.mark.ci1
    def test_l7(self):
        print("the component only in online")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("index", "index.title")
        assert trans1 == "Sample fr Application"

    @pytest.mark.ci1
    def test_l8(self):
        print("the locale only in online")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("contact", "contact.support", source="Support:", locale="ko")
        assert trans1 == "サポート：ko"
        trans1 = translation1.get_string("contact", "contact.support", locale="ko")
        assert trans1 == "サポート：ko"

    @pytest.mark.ci1
    def test_l9(self):
        print("the locale only in online")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # data1 = translation1.get_locale_supported("ko")
        # print(data1)
        trans1 = translation1.get_string("contact", "contact.support", source="Support:", locale="ja")
        # print(trans1)
        assert trans1 == "サポート："
        trans2 = translation1.get_string("contact", "contact.support", locale="ja")
        assert trans2 == "サポート："

    @pytest.mark.ci1
    def test_l10(self):
        print("the locale only in offline")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("contact", "contact.title", locale="pt")
        # source= "Contact",
        assert trans1 == "Contact"

    @pytest.mark.ci2
    def test_mix_get_locale_strings(self):
        print("online:get_locale_strings")
        """
        mix mode: get locale strings
        """
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        translation.get_string("about", "about.message")
        data = {
            "about": {
                "about.description": "Utilisez cette zone pour fournir des informations supplémentaires",
                "about.message": "test fr key",
                "about.title": "Sur",
                "about.test": "test fr the {1} to {0} and {2}"
            }
        }
        trans1 = translation.get_locale_strings(LOCALE, False)
        assert trans1["about"] == data["about"]

        trans1 = translation.get_locale_strings("de", False)
        assert "about" not in trans1.keys()

        trans1 = translation.get_locale_strings("da", False)
        assert "about" not in trans1.keys()

        trans1 = translation.get_locale_strings("zh-Hans", False)
        assert "about" not in trans1.keys()

        trans1 = translation.get_locale_strings("pt", False)
        assert "about" not in trans1.keys()

    @pytest.mark.ci1
    def test_mix_format_items(self):
        """
        mix mode: format items
        """
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("about", "about.test", x="1", y="2")
        assert trans1 == "test fr the {1} to {0} and {2}"
        trans1 = translation1.get_string("about", "about.test", format_items=["11", "22", "33"])
        assert trans1 == "test fr the 22 to 11 and 33"
        trans1 = translation1.get_string("about", "about.test", format_items=["11", "22", "33", "44"])
        assert trans1 == "test fr the 22 to 11 and 33"
        trans2 = translation1.get_string("contact", "contact.title", format_items={'x': '11', 'y': '22', 'z': '33'},
                                         locale='ko')
        assert trans2 == "연락처change 22 add 11 and 33"
        trans1 = translation1.get_string("about", "about.test", format_items=["11", None, "33"])
        assert trans1 == "test fr the None to 11 and 33"

    @pytest.mark.ci1
    def test_l13(self):
        print("online_with_local:component and key and nolocale or nosource------bug: 1165")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        trans1 = translation1.get_string("about", "about.message")
        # assert trans1 == "123"
        print(trans1)
        trans2 = translation1.get_string("contact", "about.message")
        # assert trans2 == "test fr key"
        print(trans2)






if __name__ == '__main__':
    # pytest.main(['-s', '-k TestOnlineWithOffline'])
    pytest.main(['-s', '-k test_cache'])

"""

FAILED test_online_with_offline.py::TestOnlineWithOffline::test_l11 - Asserti...
FAILED test_online_with_offline.py::TestOnlineWithOffline::test_lcache - Asse...
"""
