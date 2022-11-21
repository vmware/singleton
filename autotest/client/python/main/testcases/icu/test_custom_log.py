# @Time 2022/11/21 15:14
# Author: beijingm

import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'ICU'
VERSION = '1.0.1'
COMPONENT = 'plural'
LOCALE = 'en'
CONFIG_YAML = 'support_icu.yml'

__RESOURCES__ = Path(__file__).parent.joinpath('config')


class TestL2CustomLogger:

    @pytest.mark.skip
    def test_custom_logger(self):
        from .custom_logger import MyLogger
        logger = MyLogger()
        file: Path = __RESOURCES__.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        rel.get_string(COMPONENT, "pluralName", locale=LOCALE)
        rel.set_logger(logger)
