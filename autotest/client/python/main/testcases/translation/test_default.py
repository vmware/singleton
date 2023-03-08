from pathlib import Path

import pytest

from sgtnclient import I18N

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestTranslationDefault:
    """
    check points:
    1. online default_locale en. can modify in yml.
    2. offline from localhost.
    3. offline from httpd file.
    4. mixed mode, if online return 4xx/5xx, offline is active.
    """

    @pytest.mark.ci1
    def test_online__only_1(self):
        """
        Online Mode:
        1. default_locale: de if default if no config
        2. locale = "da" not in locale support, will use default_locale
        """
        file = _CONFIG_.joinpath("online__only_without_default_locale.yml")
        I18N.add_config_file(file)
        rel = I18N.get_release("FakerSample1", "1.0.0")
        translation = rel.get_translation()

        # if not set I18N.set_current_locale(LOCALE) , default = "en"
        message = translation.get_string("android", "key.star-laugh.title")
        assert message == "Right far work rather station population test attack use enough fear attorney tell."

        # if set locale="de" in supportLocales, return messages_de.json
        message = translation.get_string("android", "key.star-laugh.title", locale='de')
        assert message == "Hinein bei draußen zu Junge Fahrrad Feuer."  # noqa

        # if set locale="de" not in supportLocales, and not set default_locale, default = "en"
        message = translation.get_string("android", "key.star-laugh.title", locale='da')
        assert message == "Right far work rather station population test attack use enough fear attorney tell."

    @pytest.mark.ci1
    def test_online__only_2(self):
        """
        Online Mode:
        1. default_locale: de if default if no config
        2. locale = "da" not in locale support, will use default_locale
        """
        file = _CONFIG_.joinpath("online__only_with_default_locale.yml")
        I18N.add_config_file(file)
        rel = I18N.get_release("FakerSample1", "1.0.0")
        translation = rel.get_translation()

        # default_locale=fr and no set locale and not I18N.set_current_locale(LOCALE), return messages_en.json
        message = translation.get_string("android", "key.star-laugh.title")
        assert message == "Right far work rather station population test attack use enough fear attorney tell."

        # default_locale=fr and set locale=de in supportLocales, return messages_de.json
        message = translation.get_string("android", "key.star-laugh.title", locale='de')
        assert message == "Hinein bei draußen zu Junge Fahrrad Feuer."  # noqa

        # default_locale=fr and set locale=da not in supportLocales, return messages_fr.json
        message = translation.get_string("android", "key.star-laugh.title", locale='da')
        assert message == "Attaquer discours recherche ami ramener déjà."  # noqa

        # default_locale=fr and no set locale and I18N.set_current_locale(ja), return messages_ja.json
        I18N.set_current_locale("ja")
        message = translation.get_string("android", "key.star-laugh.title")
        assert message == "ストレージ私今日欠乏電池器官。"

        # default_locale=fr and set locale=da not in supportLocales, return messages_fr.json
        message = translation.get_string("android", "key.star-laugh.title", locale='da')
        assert message == "Attaquer discours recherche ami ramener déjà."  # noqa

        """
        I18N.set_current_locale("ja") 作用 get_string()不带locale可选参数，会带入默认值，不配置默认en
        yaml中default_locale 作用 如果 可选参数locale配置不在supportLocales中，会读取default_locale，如果不存在，默认en
        """

    @pytest.mark.ci1
    def test_offline__only_local_folder(self):
        """
        offline mode:
        1. no latest.json bundle
        """
        file = _CONFIG_.joinpath('offline__only_local_folder.yml')
        I18N.add_config_file(file)

        rel = I18N.get_release("PythonClient", '2.0.1')
        translation = rel.get_translation()

        # if default_locale: de is active, return de locale when locale not in bundle.
        trans1 = translation.get_string("about", "messages.about.message", locale="da")
        assert trans1 == "test de key"

    # @pytest.mark.ci1
    # def test_offline__only_remote_folder(self):
    #     """
    #     offline mode: bundle from httpd
    #     """
    #     file = _CONFIG_.joinpath('offline__only_remote_folder.yml')
    #     I18N.add_config_file(file)
    #
    #     rel = I18N.get_release("PythonClient", '4.0.0')
    #     translation = rel.get_translation()
    #     trans1 = translation.get_string("about", "about.message", locale='da')
    #     assert trans1 == "应用程序说明页。"
    #
    # @pytest.mark.ci1
    # def test_mixed__only_offline_has_component(self):
    #     """
    #     Mixed Mode:
    #     1. online and offline both has product. and only offline has component. use offline. online maybe 4xx.
    #     """
    #     file = _CONFIG_.joinpath('mixed__only_offline_has_component.yml')
    #     I18N.add_config_file(file)
    #     I18N.set_current_locale('ja')
    #     rel = I18N.get_release("PythonClient", '4.0.1')
    #     translation = rel.get_translation()
    #
    #     # both exist
    #     trans1 = translation.get_string("about", "about.message", locale="da")
    #     assert trans1 == "test ja key"
    #
    #     # online exist
    #     trans1 = translation.get_string("about", "about.message", locale="da")
    #     assert trans1 == "test ja key"
    #
    #     # offline exist
    #     trans1 = translation.get_string("insert", "insert.contact", locale="de")
    #     assert trans1 == "Contact1"

    @pytest.mark.ci1
    def test_mixed__only_offline_has_product(self):
        """
        Mixed Mode:
        1. only offline has product. use offline mode, online maybe 5xx.
        """
        file = _CONFIG_.joinpath('mixed__only_offline_has_product.yml')
        I18N.add_config_file(file)
        rel = I18N.get_release("dtestproduct", '1.3')
        translation = rel.get_translation()
        message = translation.get_string("testcomponent1", "about.title", locale="en")
        assert message == "About"

        message = translation.get_string("testcomponent1", "about.description", locale="de")
        assert message == "Use this area to provide additional information"


if __name__ == '__main__':
    pytest.main(['-s'])
