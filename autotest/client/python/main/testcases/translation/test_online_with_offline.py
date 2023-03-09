import pytest
from pathlib import Path
from sgtnclient import I18N
from .utils import ContextStringsDe6

PRODUCT = 'PythonClient'
VERSION = '1.1.1.1.1'
COMPONENT = 'about'
LOCALE = 'fr'
Config_files = 'sgtn_online_offline_before.yml'

__CONFIG__ = Path(__file__).parent.joinpath('config')


class TestOnlineWithOffline:

    @pytest.mark.ci1
    def test_l1(self):
        print("the key only in offline")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # tran1 = translation.get_string("intest","intest.offline")
        # assert tran1 == "Contact1 offline"

        tran2 = translation.get_string("intest", "intest.offline", locale="ja")
        assert tran2 == "offline12345"

    @pytest.mark.skip
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

        # time.sleep(5)
        tran2 = translation.get_string("about", "about.message")
        assert tran2 == "test__de_value"
        # time.sleep(1)
        tran2 = translation.get_string("about", "about.message")
        assert tran2 == "test__de_value"

    @pytest.mark.ci1
    def test_l2(self):
        print("the key only in online")
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # only online key 不比较
        tran1 = translation.get_string("intest", "intest.online", locale='fr')
        assert tran1 == "Contact1 fr online"

        # 仅本地key，直接返回source
        tran1 = translation.get_string("intest", "intest.offline", locale='de')
        assert tran1 == "offline12345"

    @pytest.mark.ci1
    def test_l3(self):
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)

        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        tran2 = translation.get_string("about", "about.message", locale='fr-CA')
        assert tran2 == "test fr key"

    @pytest.mark.citest
    def test_l4(self):
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        trans2 = translation.get_string("intest", "intest.home", locale="fr")
        assert trans2 == "Home12"

    def test_l5(self):
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        trans2 = translation.get_string("intest", "intest.about", locale="ja")
        assert trans2 == "About1 ja"
        trans3 = translation.get_string("intest", "intest.about", source="About1")
        assert trans3 == "About1 fr"
        trans4 = translation.get_string("intest", "intest.about", source="About1", locale="ja")
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
        translation = rel.get_translation()
        trans1 = translation.get_string("common", "common.about")
        assert trans1 == "test fr key contact online with localsource"

    @pytest.mark.skip
    def test_l7(self):
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        trans1 = translation.get_string("index", "index.title")
        assert trans1 == "Sample fr Application"

    # @pytest.mark.ci1
    # def test_l8(self):
    #     file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
    #     I18N.add_config_file(file)
    #     I18N.set_current_locale(LOCALE)
    #     rel = I18N.get_release(PRODUCT, VERSION)
    #     translation = rel.get_translation()
    #     trans1 = translation.get_string("contact", "contact.support", source="Support:", locale="ko")
    #     assert trans1 == "サポート：ko"
    #     trans1 = translation.get_string("contact", "contact.support", locale="ko")
    #     assert trans1 == "サポート：ko"

    @pytest.mark.ci1
    def test_l9(self):
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        trans1 = translation.get_string("contact", "contact.support", source="Support:", locale="ja")
        assert trans1 == "サポート："
        trans2 = translation.get_string("contact", "contact.support", locale="ja")
        assert trans2 == "サポート："

    @pytest.mark.ci1
    def test_l10(self):
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        trans1 = translation.get_string("contact", "contact.title", locale="pt")
        # source= "Contact",
        assert trans1 == "Contact"

    def test_mix_get_locale_strings(self):
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
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        trans1 = translation.get_string("about", "about.test", x="1", y="2")
        assert trans1 == "test fr the {1} to {0} and {2}"
        trans1 = translation.get_string("about", "about.test", format_items=["11", "22", "33"])
        assert trans1 == "test fr the 22 to 11 and 33"
        trans1 = translation.get_string("about", "about.test", format_items=["11", "22", "33", "44"])
        assert trans1 == "test fr the 22 to 11 and 33"
        # trans2 = translation.get_string("contact", "contact.title", format_items={'x': '11', 'y': '22', 'z': '33'},
        #                                 locale='ko')
        # assert trans2 == "연락처change 22 add 11 and 33"
        trans1 = translation.get_string("about", "about.test", format_items=["11", None, "33"])
        assert trans1 == "test fr the None to 11 and 33"

    @pytest.mark.ci1
    def test_l13(self):
        file: Path = __CONFIG__.joinpath('sgtn_online_offline_before.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        trans1 = translation.get_string("about", "about.message")
        print(trans1)
        trans2 = translation.get_string("contact", "about.message")
        print(trans2)


if __name__ == '__main__':
    pytest.main(['-s', '-k TestOnlineWithOffline'])
