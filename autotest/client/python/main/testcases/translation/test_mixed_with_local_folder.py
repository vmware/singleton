import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '9.0.2'
COMPONENT = 'contact'
LOCALE = 'fr'
CONFIG_FILE = 'mixedModeWithLocalFolder.yml'

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestMixedWithLocalFolder:

    @pytest.mark.ci1
    def test_source_offline_has_high_priority(self):
        file = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # compare offline messages.properties == online messages_en.json
        # mixed Mode, not compare online
        # return messages_ja.json online
        message = translation.get_string(COMPONENT, "contact.title", locale="ja")
        assert message == "連絡先(Online)"

        # compare offline messages.properties != online messages_en.json
        # no compare offline messages.properties and messages_en.properties
        # return messages.properties(latest offline)
        message = translation.get_string(COMPONENT, "contact.support", locale="ja")
        assert message == "Support:<offline latest>"

    @pytest.mark.ci1
    def test_translation_online_has_high_priority(self):
        file = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # offline messages.properties == online messages_en.json
        # offline messages.properties == offline messages_en.properties
        # return online messages_xxx.json first
        message = translation.get_string("contact", "contact.message", locale='ja')
        assert message == "お客様の連絡先ページ。"

        # offline messages.properties == online messages_en.json
        # online messages_de.json not exists.
        # offline not compare. return offline messages_de.json
        message = translation.get_string("contact", "contact.message", locale='de')
        assert message == "Ihrer Kontaktseite(Offline)."

        # offline messages.properties == online messages_en.json
        # online messages_ko.json exist but no key-value. return online first. no key-value will return default-locale.
        # if messages_ko.json not exists. return offline
        message = translation.get_string("contact", "contact.message", locale='ko')
        assert message == "お客様の連絡先ページ。"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestMixedWithLocalFolder'])
