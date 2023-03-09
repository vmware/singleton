import pytest
from pathlib import Path
from sgtnclient import I18N
from multiprocessing import Process
from main.testcases.icu.parser import _TESTCASES_

PRODUCT = 'ICU'
VERSION = '1.0.1'
COMPONENT = 'plural'
CONFIG_YAML = 'support_icu.yml'

__RESOURCES__ = Path(__file__).parent.joinpath('config')


class TestL2IcuFormat:

    def test_get_locale(self):
        file: Path = __RESOURCES__.joinpath(CONFIG_YAML)
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
        file: Path = __RESOURCES__.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        with pytest.raises(ValueError):
            rel.get_string(COMPONENT, 'pluralName', locale="en", format_items=['12', '12'])

    def test_plurals_default_locale(self):
        """if no locale parameter, default is en"""
        file: Path = __RESOURCES__.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        I18N.set_current_locale("en")
        rel = I18N.get_release(PRODUCT, VERSION)
        txt = rel.get_string(COMPONENT, 'pluralName', format_items=[12, "I"])
        assert txt == 'I bought 12 books.'

    @staticmethod
    def plurals_default_source_locale():
        """if locale not found, use default_locale"""
        file: Path = __RESOURCES__.joinpath(CONFIG_YAML)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        txt = rel.get_string(COMPONENT, 'pluralName', locale='error', format_items=[12, "I"])
        assert txt == 'I gekauft 12 Bücher.'

    def test_plurals_default_source_locale(self):
        task = Process(target=self.plurals_default_source_locale)
        task.daemon = True
        task.start()
        task.join()

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
