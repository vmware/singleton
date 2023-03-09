import pytest
from pathlib import Path
from sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '9.0.2'
COMPONENT = 'contact'
LOCALE = 'fr'
CONFIG_FILE = 'mixed__both_has_bundle.yml'

__TRANSLATION__ = Path(__file__).parent
__CONFIG__ = __TRANSLATION__.joinpath('config')
__RESOURCES__ = __TRANSLATION__.joinpath('resources')

"""
9.0.2 bundle
用于验证 本地和服务器的source，translation优先级，是否产生比较
"""


class TestMixedBundleInAll:

    @pytest.mark.ci1
    def test_source_offline_has_high_priority(self):
        """
        Mixed Mode:
        1. 会比较online的messages_en.json和offline的messages.properties
        2. 如果服务端只有latest.json。不会比较，直接返回对应locale的translation。
        3. 如果是online模式，只要对应key的latest和en的source不相等。则默认返回latest的source。
        4. 但是如果在混合模式下，只要保证本地的source和en相等，就可以返回对应locale的translation
        """
        file = __CONFIG__.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        # I18N.set_current_locale("en")
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        tran1 = translation.get_string("contact", "contact.title", locale="ja")
        assert tran1 == "連絡先(Offline)"

    @pytest.mark.ci1
    def test_translation_online_has_high_priority(self):
        """
        Mixed Mode:
        1. 如果比较正常，在返回对应locale的translation时。
        2. 如果online查询到对应的translation，不管本地的translation是否存在，直接使用online的。
        3. 如果online未查询到对应的translation，获取本地的translation。
        """
        file = __CONFIG__.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        # I18N.set_current_locale("en")
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        tran1 = translation.get_string("contact", "contact.message", locale="de")
        assert tran1 == "Ihrer Kontaktseite. (Offline)"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestMixedBundleInAll'])
