import pytest
from pathlib import Path
from multiprocessing import Process
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'de'
CONFIG_FILE = 'only_online.yml'

__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')


class TestOnLine:

    @staticmethod
    def online_default_locale():
        file = __CONFIG__.joinpath(CONFIG_FILE)
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        tran1 = translation.get_string("contact", "contact.title")
        assert tran1 == "Contact"

        tran1 = translation.get_string("contact", "contact.title", locale="")
        assert tran1 == "Contact"

        tran1 = translation.get_string("contact", "contact.title", locale="en")
        assert tran1 == "Contact"

        # latest，en(source_locale)，de(locale)，ko(default_locale)
        tran1 = translation.get_string("contact", "contact.title", locale="de")
        assert tran1 == "Kontakt"

        tran1 = translation.get_string("contact", "contact.title", locale="da")
        assert tran1 == "연락처change {y} add {x} and {z}"

    @pytest.mark.ci1
    def test_online_default_locale(self):
        task = Process(target=self.online_default_locale)
        task.daemon = True
        task.start()
        task.join()

    @pytest.mark.ci1
    def test_online_compare_with_latest(self):
        """
        Online Mode:
        1. 如果配置了I18N.set_current_locale. 会覆盖default_locale配置。
        2. 如果配置了get_string(locale)，会覆盖I18N.set_current_locale配置。
        3. 如果message_$locale.json和latest.json的key-value不相等。直接返回latest.json的source

        """
        file = __CONFIG__.joinpath(CONFIG_FILE)
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # I18N.set_current_locale(LOCALE)
        tran1 = translation.get_string("contact", "contact.title")
        assert tran1 == "Kontakt"

        # get_string(locale="zh-Hant")
        tran2 = translation.get_string("contact", "contact.title", locale="zh-Hant")
        assert tran2 == "聯繫"

        # message_latest.json,message_en.json(source_locale) "about.message" not same, return latest.json
        tran3 = translation.get_string("about", "about.message", locale='ja')
        assert tran3 == "Your application description page.xxx"

    @pytest.mark.ci1
    def test_online_no_source_found(self):
        """
        Online Mode:
        1. no component return key direct
        2. no key  return key direct
        """
        file: Path = __CONFIG__.joinpath(CONFIG_FILE)
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # no component
        tran1 = translation.get_string("", "about.message")
        assert tran1 == "about.message"
        tran2 = translation.get_string("123", "about.message")
        assert tran2 == "about.message"
        tran3 = translation.get_string(None, "about.message")
        assert tran3 == "about.message"

        # no key
        tran1 = translation.get_string("about", "")
        assert tran1 == ""
        tran2 = translation.get_string("about", "123")
        assert tran2 == "123"
        tran3 = translation.get_string("about", None)
        assert tran3 is None

        # no component and key
        tran1 = translation.get_string("", "")
        assert tran1 == ""
        tran2 = translation.get_string("456", "123")
        assert tran2 == "123"
        tran3 = translation.get_string(None, None)
        assert tran3 is None

    @pytest.mark.ci1
    def test_online_locale_compatible(self):
        """
        online mode: has both message_fr.json and message_fr-CA.json。always return fr.json
        """
        file: Path = __CONFIG__.joinpath(CONFIG_FILE)
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # if online has message_fr-CA.json and message_fr.json. always return fr
        trans1 = translation.get_string("about", "about.title", locale="fr-CA")
        assert trans1 == "Sur"

        trans1 = translation.get_string("about", "about.title", locale="fr_CA")
        assert trans1 == "Sur"

        trans1 = translation.get_string("about", "about.title", locale="fr")
        assert trans1 == "Sur"

        trans1 = translation.get_string("about", "about.title", locale="ar")
        assert trans1 == "حول"

        trans1 = translation.get_string("about", "about.title", locale="ar_AE")
        assert trans1 == "حول"

        trans1 = translation.get_string("about", "about.title", locale="ar-AE")
        assert trans1 == "حول"

        trans1 = translation.get_string("about", "about.title", locale="zh_Hant")
        assert trans1 == "關於"

        trans1 = translation.get_string("about", "about.title", locale="zh_hant")
        assert trans1 == "關於"

        trans1 = translation.get_string("about", "about.title", locale="zh-hant")
        assert trans1 == "關於"

    @pytest.mark.ci1
    def test_online_latest_compare_with_source(self):
        """
        Online Mode:
        1. 不传递source情况下，Online模式默认比较latest和en。
        2. 如果传递了source。则用source和en比较，
            2.1 如果latest和en不相等，不管传递的source是否和latest相等，都直接返回source的值，不会返回locale的翻译结果
            2.2 如果latest和en相等，传递的source也相等，则会返回locale的翻译结果，否则直接返回传递的source。
        """

        """
        about 的 about.message是不相同的，about.title是相同的。
        """
        file: Path = __CONFIG__.joinpath(CONFIG_FILE)
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale("ar")
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # about.message different between latest and en, pass source==en
        trans1 = translation.get_string("about", "about.message", source='Your application description page.')
        assert trans1 == "Your application description page."

        # about.message different between latest and en, pass source==latest
        trans1 = translation.get_string("about", "about.message", source='Your application description page.xxx')
        assert trans1 == "Your application description page.xxx"

        # about.title same between latest and en, pass source==latest. return locale translation
        trans1 = translation.get_string("about", "about.title", source='About', locale="ar")
        assert trans1 == "حول"

        # about.title same between latest and en, pass source!=latest. return source.
        trans1 = translation.get_string("about", "about.title", source='About111', locale="ar")
        assert trans1 == "About111"

    @pytest.mark.ci1
    def test_error_format(self):
        """
        online mode: format error
        """
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = rel.get_translation()
        with pytest.raises(IndexError):
            translation1.get_string("about", "about.test", format_items=["11", "22"])

    @pytest.mark.ci1
    def test_l10(self):
        print("online:format_items")
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        # trans1 = translation1.get_string("about", "about.test","1","2")
        #  print(trans1)
        trans1 = translation1.get_string("about", "about.test", x="1", y="2")
        assert trans1 == "test de the {1} to {0} and {2}"
        trans1 = translation1.get_string("about", "about.test", format_items=["11", "22", "33"])
        assert trans1 == "test de the 22 to 11 and 33"
        trans1 = translation1.get_string("about", "about.test", format_items=["11", "22", "33", "44"])
        assert trans1 == "test de the 22 to 11 and 33"
        trans2 = translation1.get_string("contact", "contact.title", format_items={'x': '11', 'y': '22', 'z': '33'},
                                         locale='ko')
        assert trans2 == "연락처change 22 add 11 and 33"
        trans1 = translation1.get_string("about", "about.test", format_items=["11", None, "33"])
        assert trans1 == "test de the None to 11 and 33"
        trans1 = translation1.get_string("about", "about.test", format_items=None)
        assert trans1 == "test de the {1} to {0} and {2}"

    @pytest.mark.ci2
    def test_get_locale_strings_by_cache(self):
        """
        online mode: get locale strings by cache
        """
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        translation.get_string("about", "about.message")
        trans1: dict = translation.get_locale_strings(LOCALE, False)

        assert "about" in trans1.keys()
        assert "about.message" in trans1["about"].keys()

    @pytest.mark.ci1
    def test_l12(self):
        print("online:get_locale_supported")
        file: Path = __CONFIG__.joinpath('only_online.yml')
        outside_config = {"product": "PythonClient", "online_service_url": "https://localhost:8090"}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation1 = self.rel.get_translation()
        data1 = translation1.get_locale_supported("da")
        assert data1 == "da"
        data1 = translation1.get_locale_supported("zh-Hans")
        assert data1 == "zh-Hans"
        data1 = translation1.get_locale_supported("fr-FR")
        assert data1 == "fr"
        data1 = translation1.get_locale_supported("fr-CA")
        assert data1 == "fr"
        data1 = translation1.get_locale_supported("en")
        assert data1 == "en"
        data1 = translation1.get_locale_supported("123")
        assert data1 == "123"
        data1 = translation1.get_locale_supported("")
        assert data1 == ""


if __name__ == '__main__':
    pytest.main(['-s', '-k TestOnLine'])
