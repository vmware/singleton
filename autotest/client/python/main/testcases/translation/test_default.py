import pytest
from pathlib import Path
from sgtnclient import I18N

COMPONENT = 'about'
LOCALE = 'da'

__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')


class TestTranslationDefault:
    """
    check points:
    1. online default_locale en. can modify in yml.
    2. offline from localhost.
    3. offline from httpd file.
    4. mixed mode, if online return 4xx/5xx, offline is active.
    """

    @pytest.mark.ci1
    def test_online__only(self):
        """
        Online Mode:
        1. default_locale: de if default if no config
        2. locale = "da" not in locale support, will use default_locale
        """
        file = __CONFIG__.joinpath("online__only.yml")
        I18N.add_config_file(file)
        rel = I18N.get_release("PythonClient", "1.0.1")
        translation = rel.get_translation()

        # if locale in bundle, return locale
        message = translation.get_string("about", "about.message", locale='fr')
        assert message == "test fr key", message

        # if # default_locale: de not active, default_locale : en actual is latest.
        message = translation.get_string("about", "about.message", locale='da')
        assert message == "Your application description page.", message

    @pytest.mark.ci1
    def test_offline__only_local_folder(self):
        """
        offline mode:
        1. no latest.json bundle
        """
        file = __CONFIG__.joinpath('offline__only_local_folder.yml')
        I18N.add_config_file(file)

        rel = I18N.get_release("PythonClient", '2.0.1')
        translation = rel.get_translation()

        # if default_locale: de is active, return de locale when locale not in bundle.
        trans1 = translation.get_string("about", "messages.about.message", locale="da")
        assert trans1 == "test de key"

    @pytest.mark.ci1
    def test_offline__only_remote_folder(self):
        """
        offline mode: bundle from httpd
        """
        file = __CONFIG__.joinpath('offline__only_remote_folder.yml')
        I18N.add_config_file(file)

        rel = I18N.get_release("PythonClient", '4.0.0')
        translation = rel.get_translation()
        trans1 = translation.get_string("about", "about.message", locale='da')
        assert trans1 == "应用程序说明页。"

    @pytest.mark.ci1
    def test_mixed__only_offline_has_component(self):
        """
        Mixed Mode:
        1. online and offline both has product. and only offline has component. use offline. online maybe 4xx.
        """
        file = __CONFIG__.joinpath('mixed__only_offline_has_component.yml')
        I18N.add_config_file(file)
        rel = I18N.get_release("PythonClient", '4.0.1')
        translation = rel.get_translation()

        # both exist
        trans1 = translation.get_string("about", "about.message", locale="da")
        assert trans1 == "test ja key"

        # online exist
        trans1 = translation.get_string("about", "about.message", locale="da")
        assert trans1 == "test ja key"

        # offline exist
        trans1 = translation.get_string("insert", "insert.contact", locale="de")
        assert trans1 == "Contact1"

    @pytest.mark.ci1
    def test_mixed__only_offline_has_product(self):
        """
        Mixed Mode:
        1. only offline has product. use offline mode, online maybe 5xx.
        """
        file = __CONFIG__.joinpath('mixed__only_offline_has_product.yml')
        I18N.add_config_file(file)
        rel = I18N.get_release("dtestproduct", '1.3')
        translation = rel.get_translation()
        trans1 = translation.get_string("testcomponent1", "about.title", locale="en")
        assert trans1 == "About"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestTranslationDefault'])
