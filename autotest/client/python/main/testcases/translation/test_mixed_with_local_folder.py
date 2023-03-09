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

        # 如果比较本地properties和online messages_en比较一致
        # 但是 online messages_latest  和 en 不一致。 mixed模式下，不会比较online的latest和en
        # 返回 online 翻译
        message = translation.get_string(COMPONENT, "contact.title", locale="ja")
        assert message == "連絡先(Online)"

        # 本地properties和online的en不一致。 优先返回本地
        # 无论是否指定本地的properties和en.properties, 都返回properties
        message = translation.get_string(COMPONENT, "contact.support", locale="ja")
        assert message == "Support:<offline latest>"

    @pytest.mark.ci1
    def test_translation_online_has_high_priority(self):
        file = _CONFIG_.joinpath(CONFIG_FILE)
        I18N.add_config_file(file)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # 本地properties和online en一样
        # 本地properties和本地en.properties一样，并且本地翻译存在
        # 线上翻译存在，优先线上翻译
        message = translation.get_string("contact", "contact.message", locale='ja')
        assert message == "お客様の連絡先ページ。"

        # 本地properties和online en一样
        # 线上翻译messages_de.json不存在，
        # 无论本地properties和本地en.properties是否一样[本地不比较]，只要本地翻译存在，使用本地
        message = translation.get_string("contact", "contact.message", locale='de')
        assert message == "Ihrer Kontaktseite(Offline)."

        # 本地properties和online en一样
        # 线上翻译messages_ko.json存在，但是对应的key-value不存在，优先使用online的，找不到返回default_locale的翻译
        # 配置项指定default_locale时。不配置是en。配置ko的，读不到值，默认en，配置其他的，就可以拿到其他的locale线上翻译
        message = translation.get_string("contact", "contact.message", locale='ko')
        assert message == "お客様の連絡先ページ。"


if __name__ == '__main__':
    pytest.main(['-s', '-k TestMixedWithLocalFolder'])
