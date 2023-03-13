import pytest
from pathlib import Path
from sgtnclient import I18N

_CONFIG_ = Path(__file__).parent.joinpath('config')
PRODUCT = "PythonClient"
VERSION = "1.0.0"
COMPONENT = "about"


class TestOnLine:
    """
    product: FakerSample1
    version: 1.0.0
    component: android latest == en
    component: JavaSDK latest != en
    """

    @pytest.mark.ci1
    def test_online_default_locale(self):
        """
        update config from outside_config
        :return:
        """
        file: Path = _CONFIG_.joinpath("only_online_with_incorrect_product.yml")
        outside_config: dict = {
            "product": "FakerSample1", "online_service_url": "https://localhost:8090", "default_locale": "ja"
        }
        I18N.add_config_file(file, outside_config)
        rel = I18N.get_release("FakerSample1", "1.0.0")

        translation = rel.get_translation()

        # set locale=da not in supportLocales and update default_locale:"ja", return messages_ja.json
        message = translation.get_string("android", "key.star-laugh.title", locale="da")
        assert message == "ストレージ私今日欠乏電池器官。"

    @pytest.mark.ci1
    def test_online_compare_with_latest(self):
        """
        Online Mode:
        1. compare messages_en.json and messages_latest.json with key-value
            key-value not same return messages_latest.json
            key-value same. return messages_locale.json
        """
        file: Path = _CONFIG_.joinpath("only_online.yml")
        I18N.add_config_file(file)
        rel = I18N.get_release("FakerSample1", "1.0.0")

        translation = rel.get_translation()

        # not set locale, if messages_en.key != messages_latest.key, return messages_latest.json
        message = translation.get_string("JavaSDK", "key.star-laugh.title")
        assert message == "@@translation not same with messages_en.json@@"

        # set locale="en", if messages_en.key != messages_latest.key, return messages_latest.json
        message = translation.get_string("JavaSDK", "key.star-laugh.title", locale="en")
        assert message == "@@translation not same with messages_en.json@@"

        # set locale="de", if messages_en.key != messages_latest.key, return messages_latest.json
        message = translation.get_string("JavaSDK", "key.star-laugh.title", locale="de")
        assert message == "@@translation not same with messages_en.json@@"

        # messages_en.key == messages_latest.key, return messages_zh-Hant.json
        message = translation.get_string("JavaSDK", "key.star-laugh.name", locale="zh-Hant")
        assert message == "以上一起精華專業開發可是為了不會控制情況教育."

    @pytest.mark.ci1
    def test_online_no_source_found(self):
        """
        Online Mode:
        1. no component return key direct
        2. no key  return key direct
        """
        file: Path = _CONFIG_.joinpath("only_online.yml")
        I18N.add_config_file(file)
        # I18N.set_current_locale("de")
        rel = I18N.get_release("FakerSample1", "1.0.0")

        translation = rel.get_translation()

        # no component
        message = translation.get_string("", "key.star-laugh.title")
        assert message == "key.star-laugh.title"
        message = translation.get_string("123", "key.star-laugh.title")
        assert message == "key.star-laugh.title"
        message = translation.get_string(None, "key.star-laugh.title")
        assert message == "key.star-laugh.title"
        message = translation.get_string("android", "key.star-laugh.title", locale="de")
        assert message == "Hinein bei draußen zu Junge Fahrrad Feuer."  # noqa

        # no key
        message = translation.get_string("android", "")
        assert message == ""
        message = translation.get_string("android", "123")
        assert message == "123"
        message = translation.get_string("android", None)
        assert message is None
        message = translation.get_string("android", "key.star-laugh.title", locale="fr")
        assert message == "Attaquer discours recherche ami ramener déjà."  # noqa

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
        file: Path = _CONFIG_.joinpath("only_online.yml")
        I18N.add_config_file(file)
        rel = I18N.get_release("FakerSample1", "1.0.0")

        translation = rel.get_translation()

        message = translation.get_string("android", "key.star-laugh.title", locale="fr-CA")
        assert message == "Attaquer discours recherche ami ramener déjà."

        message = translation.get_string("android", "key.star-laugh.title", locale="fr_CA")
        assert message == "Attaquer discours recherche ami ramener déjà."

        message = translation.get_string("android", "key.star-laugh.title", locale="zh_Hant")
        assert message == "責任一起最后最大教育評論本站由於科技大學人員部分來源都是."

        message = translation.get_string("android", "key.star-laugh.title", locale="zh_hant")
        assert message == "責任一起最后最大教育評論本站由於科技大學人員部分來源都是."

        message = translation.get_string("android", "key.star-laugh.title", locale="zh-hant")
        assert message == "責任一起最后最大教育評論本站由於科技大學人員部分來源都是."

    @pytest.mark.ci1
    def test_online_latest_compare_with_source(self):
        """
        Online Mode:
        """
        file: Path = _CONFIG_.joinpath("only_online.yml")
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
        print("online:format_items")
        file: Path = _CONFIG_.joinpath('only_online.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale("de")
        rel = I18N.get_release("FakerSample1", "1.0.0")
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
        file: Path = _CONFIG_.joinpath('only_online.yml')
        I18N.add_config_file(file)
        I18N.set_current_locale("de")
        rel = I18N.get_release("FakerSample1", "1.0.0")
        translation = rel.get_translation()

        translation.get_string("android", "key.star-laugh.title")
        message: dict = translation.get_locale_strings("de", False)
        assert "android" in message.keys()
        assert "key.star-laugh.title" in message["android"].keys()

    @pytest.mark.ci1
    def test_format_locale_strings(self):
        file = _CONFIG_.joinpath('only_online.yml')
        I18N.add_config_file(file)
        rel = I18N.get_release("FakerSample1", "1.0.0")
        translation = rel.get_translation()

        locale = translation.get_locale_supported("de-de")
        assert locale == "de"

        locale = translation.get_locale_supported("zh-Hans")
        assert locale == "zh-Hans"

        locale = translation.get_locale_supported("fr-FR")
        assert locale == "fr"

        locale = translation.get_locale_supported("fr-CA")
        assert locale == "fr"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestOnLine'])
