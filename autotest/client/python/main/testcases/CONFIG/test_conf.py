from pathlib import Path

import pytest

from sgtn4python.sgtnclient import I18N
from sgtn4python.sgtnclient.sgtn_client import SingletonRelease, SingletonConfig
from test.CONFIG.parser import config_reader

PRODUCT = 'PythonClient'
VERSION = '1.0.0'
COMPONENT = 'about'
LOCALE = 'fr'
CONFIG = 'only_online.yml'
__RESOURCES__ = Path(__file__).parent.joinpath('config')


class TestConfig:

    def test_l1(self):
        """config success"""
        config: dict = config_reader(CONFIG)
        file: Path = __RESOURCES__.joinpath(CONFIG)
        I18N.add_config_file(file)

        rel: SingletonRelease = I18N.get_release(PRODUCT, VERSION)
        conf = rel.get_config()
        assert conf.get_config_data() == config

    def test_l2(self):
        """config update success"""
        extra_config: dict = {'product': 'PythonClient', 'l10n_version': '2.0.0',
                              'online_service_url': 'http://localhost:8091',
                              'log_path': 'logs', 'try_delay': 10,
                              'cache_expired_time': 600}
        file: Path = __RESOURCES__.joinpath('only_online.yml')
        slc: SingletonConfig = I18N.add_config(file, extra_config)
        con = slc.get_config_data()
        assert con == extra_config

    def test_l3(self):
        """set_locale"""
        file: Path = __RESOURCES__.joinpath(CONFIG)
        I18N.add_config_file(file)

        I18N.set_current_locale(LOCALE)
        rel: SingletonRelease = I18N.get_release(PRODUCT, VERSION)
        conf = rel.get_config()
        conf.get_info()
        conf.get_config_data()


if __name__ == '__main__':
    pytest.main(['-s'])
