from pathlib import Path

import yaml
import pytest
from sgtnclient import I18N

_CONFIG_ = Path(__file__).parent.joinpath('config')

PRODUCT = "PythonClient"
VERSION = "1.0.0"
COMPONENT = "about"
CONFIG_FILE = "config_default_locale.yml"


class TestDefaultConfig:

    def test_load_config_from_yaml(self):
        """config load from yaml"""
        file = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        conf = rel.get_config()
        assert conf.get_config_data() == yaml.safe_load(file.read_bytes())

    def test_update_from_extra(self):
        """config update from extra config"""
        extra_config: dict = {
            'product': 'PythonClient',
            'l10n_version': '2.0.0',
            'online_service_url': 'http://localhost:8091',
            'try_delay': 10,
            'cache_expired_time': 600
        }
        file = _CONFIG_.joinpath(CONFIG_FILE)
        singleton_config = I18N.add_config(file, extra_config)
        con: dict = singleton_config.get_config_data()
        assert con == extra_config

    def test_set_current_locale(self):
        """set or get current locale"""
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale("fr")
        I18N.get_release(PRODUCT, VERSION)

        assert I18N.get_current_locale() == "fr"

    def test_config_default_locale(self):
        """
        default_locale:
        1. default en if not config
        2. priority low then I18N.set_current_locale low then get_string(locale)
        3. when locale not support or incorrect. default_locale will activate
        4. must config with source_locale
        """
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # default_locale: ja
        # get_string(locale=None) and unset I18N.set_current_locale. default locale=en
        message = translation.get_string(COMPONENT, "about.description")
        assert message == "Use this area to provide additional information"

        # default_locale: ja
        # get_string(locale=None) and I18N.set_current_locale = de. locale=de
        I18N.set_current_locale("de")
        message = translation.get_string(COMPONENT, "about.message")
        assert message == "test de key"

        # default_locale: ja
        # get_string(locale=fr) and I18N.set_current_locale = de. default locale=fr
        I18N.set_current_locale("de")
        message = translation.get_string(COMPONENT, "about.message", locale="fr")
        assert message == "test fr key"

        # default_locale: ja
        # get_string(locale=None) and I18N.set_current_locale = dd. default locale=ja
        I18N.set_current_locale("dd")
        message = translation.get_string(COMPONENT, "about.message")
        assert message == "test ja key"

        # default_locale: ja
        # get_string(locale=dd) and I18N.set_current_locale = de. default locale=ja
        I18N.set_current_locale("de")
        message = translation.get_string(COMPONENT, "about.message", locale="dd")
        assert message == "test ja key"

    def test_config_offline_resources(self):
        config_file = "config_offline_resources.yml"
        file: Path = _CONFIG_.joinpath(config_file)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, "9.0.1")
        translation = rel.get_translation()

        # no locales and component_template
        # default latest=messages.properties
        # default locale_translation=messages_de.json
        # can not compare with latest and en
        message = translation.get_string("about", "about.title", locale="de")
        assert message == "About de(Offline)"

        # no locales and component_template
        # default latest=messages.properties
        # default locale_translation=messages_de.json
        # no dd will return messages.properties.
        # if no messages.properties will return "about.title"
        message = translation.get_string("about", "about.title", locale="dd")
        assert message == "About<offline latest>"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestDefaultConfig'])
