from pathlib import Path

import yaml
import pytest
from sgtnclient import I18N

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestConfig:

    def test_l1(self):
        """config load from yaml"""
        file = _CONFIG_.joinpath("config_only.yml")
        I18N.add_config_file(file)
        rel = I18N.get_release("PythonClient", "1.0.0")
        conf = rel.get_config()
        assert conf.get_config_data() == yaml.safe_load(file.read_bytes())

    def test_l2(self):
        """config update success from extra info"""
        extra_config: dict = {'product': 'PythonClient', 'l10n_version': '2.0.0',
                              'online_service_url': 'http://localhost:8091', 'try_delay': 10,
                              'cache_expired_time': 600}
        file: Path = _CONFIG_.joinpath('only_online.yml')
        singleton_config = I18N.add_config(file, extra_config)
        con: dict = singleton_config.get_config_data()
        assert con == extra_config

    def test_l3(self):
        file: Path = _CONFIG_.joinpath("config_only.yml")
        I18N.add_config_file(file)
        I18N.set_current_locale("fr")
        I18N.get_release("PythonClient", "1.0.0")

        assert I18N.get_current_locale() == "fr"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestConfig'])
