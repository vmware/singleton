import pytest
from pathlib import Path
from sgtnclient import I18N

_CONFIG_ = Path(__file__).parent.joinpath('config')
PRODUCT = "PythonClient"
VERSION = "1.0.0"
COMPONENT = "contact"
CONFIG_FILE = "onlineWithCompare.yml"


class TestOnLine:

    @pytest.mark.ci1
    def test_online_compare_with_latest(self):
        """
        Online Mode:
        1. compare messages_en.json and messages_latest.json with key-value
            key-value not same return messages_latest.json
            key-value same. return messages_locale.json
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)

        translation = rel.get_translation()

        # not set locale, if messages_en.key != messages_latest.key, return messages_latest.json
        message = translation.get_string(COMPONENT, "contact.message")
        assert message == "Your contact page.<latest>"

        # set locale="en", if messages_en.key != messages_latest.key, return messages_latest.json
        message = translation.get_string(COMPONENT, "contact.message", locale="en")
        assert message == "Your contact page.<latest>"

        # messages_en.key == messages_latest.key, return messages_zh-Hant.json
        message = translation.get_string(COMPONENT, "contact.support", locale="zh-Hant")
        assert message == "支援："

    @pytest.mark.ci1
    def test_online_no_source_found(self):
        """
        Online Mode:
        1. no component return key direct
        2. no key  return key direct
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)

        translation = rel.get_translation()

        # no component
        message = translation.get_string("", "key.star-laugh.title")
        assert message == "key.star-laugh.title"
        message = translation.get_string("123", "key.star-laugh.title")
        assert message == "key.star-laugh.title"
        message = translation.get_string(None, "key.star-laugh.title")
        assert message == "key.star-laugh.title"

        # no key
        message = translation.get_string("android", "")
        assert message == ""
        message = translation.get_string("android", "123")
        assert message == "123"
        message = translation.get_string("android", None)
        assert message is None

        # no component and key
        message = translation.get_string("", "")
        assert message == ""
        message = translation.get_string("456", "123")
        assert message == "123"
        message = translation.get_string(None, None)
        assert message is None

    @pytest.mark.ci1
    def test_online_locale_compatible(self):
        """
        online mode: has both message_fr.json and message_fr-CA.json。always return fr.json
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)

        translation = rel.get_translation()

        message = translation.get_string(COMPONENT, "contact.title", locale="zh_hant")
        assert message == "聯繫"

        message = translation.get_string(COMPONENT, "contact.title", locale="zh-Hant")
        assert message == "聯繫"

    @pytest.mark.skip("DeprecationWarning")
    def test_online_latest_compare_with_source(self):
        """
        Online Mode:
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale("ja")
        rel = I18N.get_release("FakerSample1", "1.0.0")
        translation = rel.get_translation()

        # messages_latest.key != messages_en.key, source=latest, return source direct
        message = translation.get_string("JavaSDK", "key.star-laugh.title",
                                         source='@@translation not same with messages_en.json@@')
        assert message == "@@translation not same with messages_en.json@@"

        # messages_latest.key != messages_en.key, source=en, return source direct
        message = translation.get_string("JavaSDK", "key.star-laugh.title",
                                         source='Guess back most enough same enter during per thought its can remain.')
        assert message == "Guess back most enough same enter during per thought its can remain."

        # messages_latest.key == messages_en.key, source=en=latest, return messages_ja.json
        trans1 = translation.get_string("JavaSDK", "key.star-laugh.name",
                                        source='Know question follow happy population friend kind structure one none national energy necessary.')
        assert trans1 == "持つ柔らかい隠す陶器コーラス敵細かい追放する分割。"

        # messages_latest.key == messages_en.key, source=en=latest, return messages_de.json
        trans1 = translation.get_string("JavaSDK", "key.star-laugh.name",
                                        source='Know question follow happy population friend kind structure one none national energy necessary.',
                                        locale="de")
        assert trans1 == "Zu wohnen mit kann Haare Finger ab unten."

        # messages_latest.key == messages_en.key, source!=en==latest, return source direct
        trans1 = translation.get_string("JavaSDK", "key.star-laugh.name",
                                        source='@Know question follow happy population friend kind structure one none national energy necessary.@')
        assert trans1 == "@Know question follow happy population friend kind structure one none national energy necessary.@"

    @pytest.mark.ci1
    def test_format_items(self):
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale("de")
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # no format_items
        message = translation.get_string("plural", "plural.placeholder.special")
        assert message == "test de the {x} to {y} and {z}"  # noqa

        # format_item is None same ad no format_items
        message = translation.get_string("plural", "plural.placeholder", format_items=None)
        assert message == "test de the {1} to {0} and {2}"

        # index match
        message = translation.get_string("plural", "plural.placeholder", format_items=["11", "22", "33"])
        assert message == "test de the 22 to 11 and 33"

        # more data to match index
        message = translation.get_string("plural", "plural.placeholder", format_items=["11", "22", "33", "44"])
        assert message == "test de the 22 to 11 and 33"

        # less data to match index will raise IndexError
        with pytest.raises(IndexError):
            translation.get_string("plural", "plural.placeholder", format_items=["11", None])

        # incorrect key-value format_items will ignore
        message = translation.get_string("plural", "plural.placeholder.special", x=1, y=2, z=3)
        assert message == "test de the {x} to {y} and {z}"  # noqa

        # correct  key-value format_items
        message = translation.get_string("plural", "plural.placeholder.special",
                                         format_items={'x': 1, 'y': 2, 'z': '3'})
        assert message == "test de the 1 to 2 and 3"  # noqa

        # more data for correct key-value format_items
        message = translation.get_string("plural", "plural.placeholder.special",
                                         format_items={'x': 1, 'y': 2, 'z': '3', 'a': 222})
        assert message == "test de the 1 to 2 and 3"  # noqa

        # less data for correct key-value format_items will raise KeyError
        with pytest.raises(KeyError):
            translation.get_string("plural", "plural.placeholder.special", format_items={'x': 1, 'y': 2})

    @pytest.mark.ci1
    def test_get_locale_strings_by_cache(self):
        """
        online mode: get locale strings by cache
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale("de")
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        translation.get_string(COMPONENT, "contact.support", locale="zh-Hant")
        message: dict = translation.get_locale_strings("zh-Hant", False)
        assert COMPONENT in message.keys()
        assert "contact.support" in message[COMPONENT].keys()

    @pytest.mark.ci1
    def test_format_locale_strings(self):
        file = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        locale = translation.get_locale_supported("de-de")
        assert locale == "de"

        locale = translation.get_locale_supported("zh-Hans")
        assert locale == "zh-Hans"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestOnLine'])
