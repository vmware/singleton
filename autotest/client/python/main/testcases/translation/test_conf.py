from pathlib import Path
import yaml
import pytest

from sgtnclient import I18N


PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'fr'
CONFIG_FILE = 'config_only.yml'
_RESOURCES_ = Path(__file__).parent.joinpath('config')


class TestConfig:

    def test_l1(self):
        """config success"""

        file: Path = _RESOURCES_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        conf = rel.get_config()

        assert conf.get_config_data() == yaml.safe_load(file.read_bytes())

    def test_l2(self):
        """config update success"""
        extra_config: dict = {'product': 'PythonClient', 'l10n_version': '2.0.0',
                              'online_service_url': 'http://localhost:8091', 'try_delay': 10,
                              'cache_expired_time': 600}
        file: Path = _RESOURCES_.joinpath('only_online.yml')
        slc = I18N.add_config(file, extra_config)
        con = slc.get_config_data()
        assert con == extra_config

    def test_l3(self):
        """set_locale"""
        file: Path = _RESOURCES_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)

        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        conf = rel.get_config()
        conf.get_info()
        conf.get_config_data()


if __name__ == '__main__':
    pytest.main(['-s'])
