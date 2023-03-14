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

        # offline Mode
        # messages.properties(latest) != messages_en.properties(en)
        # return messages.properties
        message = translation.get_string("about", "about.title", locale="de")
        assert message == "About<offline latest>"

        # offline Mode
        # messages.properties(latest) == messages_en.properties(en)
        # return messages_de.json
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

        # offline Mode
        # no messages_en.properties
        # no compare and return messages_de.json direct
        message = translation.get_string("about", "about.title", locale="de")
        assert message == "About de(Offline)"

        # offline Mode
        # no messages_en.properties
        # no compare and da un support and return messages_default_locale.json
        message = translation.get_string("about", "about.message", locale="da")
        assert message == "test ja offline key"

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

        message = translation.get_locale_supported("da")
        assert message == "da"
        message = translation.get_locale_supported("fr-CA")
        assert message == "fr"
        message = translation.get_locale_supported("zh-tw")
        assert message == "zh-Hant"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestTranslationOfflineRemote'])
