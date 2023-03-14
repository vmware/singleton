import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'contact'
LOCALE = 'fr'
CONFIG_FILE = 'pseudoMixedFromRemote.yml'

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestPseudoMixedFromRemote:

    @pytest.mark.ci1
    def test_mixed_pseudo_from_remote(self):
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # online return messages_en.json direct.
        message = translation.get_string(COMPONENT, "contact.support", locale="ja")
        assert message == "#@Support:#@"

        # offline return messages.properties direct
        message = translation.get_string("intest", "intest.contact", locale="ja")
        assert message == "#@Contact<latest>#@"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestPseudoMixedFromRemote'])
