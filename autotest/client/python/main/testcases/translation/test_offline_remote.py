import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '9.0.1'
COMPONENT = 'about'
LOCALE = 'fr'

CONFIG_FILE = "offlineRemoteWithCompare.yml"

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestTranslationOfflineRemote:

    @pytest.mark.ci1
    def test_offline_with_compare(self):
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # offline模式，指定了latest和en
        # 如果latest.title ！= en.title
        # 返回messages.properties的值
        message = translation.get_string("about", "about.title", locale="de")
        assert message == "About<offline latest>"

        # offline模式，指定了latest和en
        # 如果latest.title == en.title
        # 返回messages_de.json
        message = translation.get_string("about", "about.message", locale="de")
        assert message == "Your application description de page(Offline)"

        # components no insert and return key.
        message = translation.get_string("about", "about.no.exist")
        assert message == "about.no.exist"

    @pytest.mark.ci1
    def test_offline_without_compare(self):
        file: Path = _CONFIG_.joinpath("offlineRemoteWithOutCompare.yml")
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # offline模式，未指定en, 不比较
        # 返回messages.properties的值
        message = translation.get_string("about", "about.title", locale="de")
        assert message == "About de(Offline)"

        # offline模式，未指定en
        # 返回messages_default_locale.json翻译
        message = translation.get_string("about", "about.message", locale="da")
        assert message == "test ja offline key"

    @pytest.mark.ci1
    def test_config_update_by_outside_config(self):
        """
        config can update by outside_config
        """
        outside_config = {"product": "PythonClient"}
        file = _CONFIG_.joinpath(CONFIG_FILE)

        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)

        current_locale = I18N.get_current_locale()
        assert current_locale == 'fr'

        rel = I18N.get_release(PRODUCT, VERSION)
        conf = rel.get_config()

        config_info = conf.get_info()
        assert config_info["product"] == "PythonClient"

    @pytest.mark.ci1
    def test_offline_no_translation_return(self):
        """
        Offline Mode:
        component_miss and key_exist.
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)

        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # "" component and None component return key direct
        tran1 = translation.get_string("", "about.message")
        assert tran1 == "about.message"

        tran2 = translation.get_string(None, "about.message")
        assert tran2 == "about.message"

        # no component and no special component return key direct
        tran3 = translation.get_string("123", "about.message")
        assert tran3 == "about.message"

        tran4 = translation.get_string("insert", "about.message")
        assert tran4 == "about.message"

        # component exist and no key
        tran5 = translation.get_string("about", "")
        assert tran5 == ""

        tran6 = translation.get_string("about", "123")
        assert tran6 == "123"

        tran7 = translation.get_string("about", None)
        assert tran7 is None

    @pytest.mark.ci1
    def test_get_string_with_diff_param(self):
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale('de')

        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get_string(locale) cover I18N.set_current_locale(locale)
        trans1 = translation.get_string("about", "about.message", locale="en")
        assert trans1 == "Your application description page."

        trans1 = translation.get_string("about", "about.message", locale="en-US")
        assert trans1 == "Your application description page."

        # I18N.set_current_locale(locale) has low priority then get_string(locale)
        # if no locale config. default is "en"
        trans2 = translation.get_string("about", "about.message", locale=None)
        assert trans2 == "Your application description de page(Offline)"

        # if locale="da" not support. the .yaml=>default_locale:ja config will be active.
        trans3 = translation.get_string("about", "about.message", locale="da")
        assert trans3 == "test ja offline key"

        # if source=None or not pass. source will not be compared with message.properties
        trans4 = translation.get_string("about", "about.message", locale="fr", source=None)
        assert trans4 == "test fr offline key"

        # if locale="en" and pass source. whatever source pass. return direct.
        trans6 = translation.get_string("about", "about.message", locale="en",
                                        source="Your application description page.")
        assert trans6 == "Your application description page."

        # if source="123", "123" as source to compared with message.properties.values
        # if message.properties value can not find "123", means the source update. and return source direct.
        trans5 = translation.get_string("about", "about.message", locale="fr", source="123")
        assert trans5 == "123"

        # if source="Your xxx" is same with message.properties "about.message",
        # means source not update ,return translation
        trans7 = translation.get_string("about", "about.description", locale="fr",
                                        source="Use this area to provide additional information")
        assert trans7 == "Utilisez cette zone pour fournir des informations supplémentaires"

    @pytest.mark.ci1
    def test_format_items(self):
        """
        offline mode: get_string param format_items
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        outside_config = {"product": "PythonClient"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        with pytest.raises(IndexError):
            translation.get_string("about", "about.test", format_items=["11", "22"])

        trans1 = translation.get_string("about", "about.test")
        assert trans1 == "test fr the {1} to {0} and {2}"

        trans2 = translation.get_string("about", "about.test", format_items=None)
        assert trans2 == "test fr the {1} to {0} and {2}"

        trans3 = translation.get_string("about", "about.test", format_items=["11", "22", "33"])
        assert trans3 == "test fr the 22 to 11 and 33"

        trans4 = translation.get_string("about", "about.test", format_items=[True, None, 33])
        assert trans4 == "test fr the None to True and 33"

        trans5 = translation.get_string("about", "about.test", format_items=["11", "22", "33", "44"])
        assert trans5 == "test fr the 22 to 11 and 33"

        # "연락처change {y} add {x} and {z}"
        trans6 = translation.get_string(
            "contact", "contact.title", format_items={'x': '11', 'y': '22', 'z': '33'}, locale='ko')
        assert trans6 == "연락처change 22 add 11 and 33"

    @pytest.mark.ci1
    def test_get_locale_support(self):
        """
        offline mode: get_locale_supported
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        outside_config = {"product": "PythonClient"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        data1 = translation.get_locale_supported("da")
        assert data1 == "da"
        data1 = translation.get_locale_supported("zh-Hans")
        assert data1 == "zh-Hans"
        data1 = translation.get_locale_supported("fr-CA")
        assert data1 == "fr"
        data1 = translation.get_locale_supported("en")
        assert data1 == "en"
        data1 = translation.get_locale_supported("123")
        assert data1 == "123"
        data1 = translation.get_locale_supported("zh-tw")
        assert data1 == "zh-Hant"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestTranslationOfflineRemote'])
