import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'fr'
CONFIG_FILE = 'pseudoOnline.yml'

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestOnlinePseudo:

    @pytest.mark.ci1
    def test_online_pseudo_success(self):
        file: Path = _CONFIG_.joinpath(CONFIG_FILE)
        outside_config = {"pseudo": True}
        I18N.add_config_file(file, outside_config)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # not en and pseudo, return online latest.json ，if no latest.json, return key. latest must exist.
        message = translation.get_string("about", "about.message", locale='de')
        assert message == "#@Your application description page.#@"

        # en and pseudo, return latest.json. no add #@
        tran2 = translation.get_string("about", "about.message", locale='en')
        assert tran2 == "Your application description page."

        # if has source ,return source direct
        tran2 = translation.get_string("about", "about.message", source="Your application description page. parameter",
                                       locale='en')
        assert tran2 == "Your application description page. parameter"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestOnlinePseudo'])
