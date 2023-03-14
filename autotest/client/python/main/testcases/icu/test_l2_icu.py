import ast
import csv
from pathlib import Path

import pytest
from sgtnclient import I18N

PRODUCT = 'ICU'
VERSION = '1.0.1'
COMPONENT = 'plural'
CONFIG_YAML = 'support_icu.yml'

_TESTCASES_: list = []

_RESOURCES_ = Path(__file__).parent.joinpath("resources")

with open(_RESOURCES_.joinpath("plural.csv"), mode="r", encoding='utf-8') as f:
    file = csv.reader(f)
    next(file)
    for item in file:
        try:
            format_items = ast.literal_eval(item[9])
        except Exception:
            format_items = None
        _TESTCASES_.append(
            {"CONFIG": item[2], "PRODUCT": item[3], "VERSION": item[4], "COMPONENT": item[5],
             "LOCALE": item[6], "KEY": item[7], "VALUE": item[8], "FORMAT_ITEMS": format_items, "ASSERTION": item[10]})

_CONFIG_ = Path(__file__).parent.joinpath('config')


class TestL2IcuFormat:

    def test_get_locale(self):
        file = _CONFIG_.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        locales = rel.get_locales(display_locale='zh-CN')
        resp = {'ar': {'from': 'remote', 'display': '阿拉伯语'}, 'de': {'from': 'remote', 'display': '德语'},
                'en': {'as': 'source', 'from': 'remote', 'display': '英语'},
                'fr': {'from': 'remote', 'display': '法语'},
                'ja': {'from': 'remote', 'display': '日语'}, 'lv': {'from': 'remote', 'display': '拉脱维亚语'},
                'prg': {'from': 'remote', 'display': '普鲁士语'}, 'pt': {'from': 'remote', 'display': '葡萄牙语'},
                'pt-PT': {'from': 'remote', 'display': '葡萄牙语（葡萄牙）'},
                'zh-Hans': {'from': 'remote', 'display': '中文（简体）'},
                'zh-Hant': {'from': 'remote', 'display': '中文（繁体）'}}
        assert locales == resp

    def test_not_support_str(self):
        file = _CONFIG_.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        with pytest.raises(ValueError):
            rel.get_string(COMPONENT, 'pluralName', locale="en", format_items=['12', '12'])

    def test_plurals_default_locale(self):
        """if no locale parameter, default is en"""
        file = _CONFIG_.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        I18N.set_current_locale("en")
        rel = I18N.get_release(PRODUCT, VERSION)
        txt = rel.get_string(COMPONENT, 'pluralName', format_items=[12, "I"])
        assert txt == 'I bought 12 books.'

    def test_plurals_default_source_locale(self):
        """if locale not found, use default_locale"""
        file = _CONFIG_.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        txt = rel.get_string(COMPONENT, 'pluralName', locale='error', format_items=[12, "I"])
        assert txt == 'I gekauft 12 Bücher.'

    # @pytest.mark.skip
    # @pytest.mark.parametrize('tc', _TESTCASES_)
    # def test_plurals_online(self, tc: dict):
    #     file: Path = __RESOURCES__.joinpath(tc['CONFIG'])
    #     I18N.add_config_file(file)
    #     I18N.set_current_locale("en")
    #     rel = I18N.get_release(tc['PRODUCT'], tc['VERSION'])
    #     txt = rel.get_string(tc['COMPONENT'], tc['KEY'], locale=tc['LOCALE'], format_items=tc['FORMAT_ITEMS'])
    #     assert txt == tc['ASSERTION'], txt
    #
    # @pytest.mark.skip
    # @pytest.mark.parametrize('tc', _TESTCASES_)
    # def test_plurals_format(self, tc: dict):
    #     file: Path = __RESOURCES__.joinpath(tc['CONFIG'])
    #     I18N.add_config_file(file)
    #     rel = I18N.get_release(tc['PRODUCT'], tc['VERSION'])
    #     if tc['FORMAT_ITEMS']:
    #         txt = rel.format(tc['LOCALE'], tc['VALUE'], array=tc['FORMAT_ITEMS'])
    #         assert txt == tc['ASSERTION'], txt


if __name__ == '__main__':
    pytest.main(['-s', 'TestL2IcuFormat'])
