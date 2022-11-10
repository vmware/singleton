from sgtn4python.sgtnclient import I18N
import time

import pytest

PRODUCT = 'PythonClient'
VERSION = '8.0.0'
COMPONENT = 'about'
LOCALE = 'de'
Config_files = 'sample_offline_remote.yml'


class TestOfflineRemoteCache:

    @pytest.mark.cache2
    def test_l1(self):
        print("test local 1")
        I18N.add_config_file('sample_offline_remote.yml')
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        tran1 = translation.get_string("about", "about.message")
        print(tran1)

        assert tran1 == "test de key"
        # os.system(r'E:\E3\test_pythoncode\offline_remote_bat\test.bat')
        time.sleep(35)
        tran2 = translation.get_string("about", "about.message")
        assert tran2 == "test de key"
        time.sleep(10)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test de key"
        # os.system(r'E:\E3\test_pythoncode\offline_remote_bat\test_re.bat')


if __name__ == '__main__':
    pytest.main(['-s', '-k'])
