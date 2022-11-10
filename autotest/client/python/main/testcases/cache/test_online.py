from sgtn4python.sgtnclient import I18N
import os
from pathlib import Path
import time
import pytest

from utils import ContextStringsDe1, ContextStringsFr1

PRODUCT = 'PythonClient'
VERSION = '5.0.0'
COMPONENT = 'about'
LOCALE = 'de'
Config_files = 'only_online.yml'

__CACHE__ = Path(__file__).parent
__CONFIG__ = __CACHE__.joinpath('config')
__RESOURCES__ = __CACHE__.joinpath('resources')

BASE_URL = "http://localhost:8091"


class TestOnlineCache:

    @pytest.mark.cache1
    def test_cache_control_expired_with_20_seconds(self):
        """
        online mode: if server cache-control.value=max-age=83600, public.
                    local cache_expired_time: 4 inactive.
        """
        file: Path = __CONFIG__.joinpath(Config_files)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        tran1 = translation.get_string("about", "about.message")
        assert tran1 == "test__de_value"

        with ContextStringsDe1(base_url=BASE_URL):
            # Cache-Control = 20 active, get local cache
            time.sleep(7)
            tran2 = translation.get_string("about", "about.message")
            assert tran2 == "test__de_value"

            # Cache-Control = 20 active, get local cache
            time.sleep(7)
            tran3 = translation.get_string("about", "about.message")
            assert tran3 == "test__de_value"

            # Cache-Control = 20 inactive, get local cache and request server and refresh cache
            time.sleep(7)
            tran3 = translation.get_string("about", "about.message")
            assert tran3 == "test__de_value"

            # get local cache
            time.sleep(5)
            tran3 = translation.get_string("about", "about.message")
            assert tran3 == "test__de_value_change"

    @pytest.mark.cache1
    def test_no_cache_control_and_cache_expired_time_with_4_seconds(self):
        """
        online mode: if server cache-control.value=max-age=0, public.
                     local cache_expired_time: 4 active.
        """
        file: Path = __CONFIG__.joinpath(Config_files)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        tran1 = translation.get_string("about", "about.message")
        assert tran1 == "test__de_value"

        with ContextStringsDe1(base_url=BASE_URL):
            # Cache-Control = 0 inactive, get local cache
            time.sleep(3)
            tran2 = translation.get_string("about", "about.message")
            assert tran2 == "test__de_value"

            # Cache-Control = 0 inactive, get local cache and request server and refresh cache
            time.sleep(3)
            tran3 = translation.get_string("about", "about.message")
            assert tran3 == "test__de_value"

            # Cache-Control = 0 inactive, get local cache
            time.sleep(1)
            tran3 = translation.get_string("about", "about.message")
            assert tran3 == "test__de_value_change"

        # Cache-Control = 0 inactive, get local cache, cache_expired_time: 3 active
        time.sleep(1)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test__de_value_change"

        # Cache-Control = 0 inactive, get local cache, cache_expired_time: 2 active
        time.sleep(1)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test__de_value_change"

        # Cache-Control = 0 inactive, get local cache, cache_expired_time: 1 active
        time.sleep(1)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test__de_value_change"

        # Cache-Control = 0 inactive, get local cache, cache_expired_time: 0, and request server
        time.sleep(1)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test__de_value"

        # Cache-Control = 0 inactive, get local cache, cache_expired_time: 3 active
        time.sleep(1)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test__de_value"

    @pytest.mark.cache1
    def test_online_cache_no_cache_control_and_no_cache_expired_time(self):
        """
        online mode: Cache-Control = 0 and cache_expired_time: 0, never expired!
        """
        file: Path = __CONFIG__.joinpath(Config_files)
        I18N.add_config_file(file)
        I18N.set_current_locale("fr")
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()
        tran1 = translation.get_string("about", "about.message")
        assert tran1 == "test_fr_value"

        with ContextStringsFr1(base_url=BASE_URL):
            time.sleep(5)
            tran2 = translation.get_string("about", "about.message")
            assert tran2 == "test_fr_value"

            time.sleep(5)
            tran3 = translation.get_string("about", "about.message")
            assert tran3 == "test_fr_value"

        time.sleep(5)
        tran2 = translation.get_string("about", "about.message")
        assert tran2 == "test_fr_value"

        time.sleep(5)
        tran3 = translation.get_string("about", "about.message")
        assert tran3 == "test_fr_value"

    @pytest.mark.cache2
    @pytest.mark.skip
    def test_l6(self):
        print("cache expired and add key")
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation = self.rel.get_translation()
        tran1 = translation.get_string("about", "about.addkey", locale="de")
        print("translation %s" % tran1)
        # os.system(r'E:\python_client\test_pythoncode\online_bat\addkey_en.bat')
        os.system(r'E:\E2\python_client\test_pythoncode\online_bat\addkey_en.bat')
        os.system(r'E:\E2\python_client\test_pythoncode\online_bat\addkey_de.bat')
        time.sleep(40)
        tran2 = translation.get_string("about", "about.addkey", locale="de")
        tran3 = translation.get_string("about", "about.addkey", locale="en")
        print("translationen 1 %s" % tran3)
        print("translationde 1 %s" % tran2)
        time.sleep(3)
        tran3 = translation.get_string("about", "about.addkey", locale="de")
        print("translationde 2 %s" % tran3)
        tran3 = translation.get_string("about", "about.addkey", locale="en")
        print("translationen 2 %s" % tran3)
        # time.sleep(31)
        # tran4 = translation.get_string("about", "about.addkey",locale= "de")
        # print("translationde 3 %s" % tran4)
        # time.sleep(3)
        # tran5 = translation.get_string("about", "about.addkey",locale= "en")
        # print("translation en %s" % tran5)
        # os.system(r'E:\python_client\test_pythoncode\online_bat\addkey_de.bat')
        # assert tran3 == "Your application description page."
        # time.sleep(3)
        # tran3 = translation.get_string("about", "about.addkey")
        # print("translation %s" % tran3)
        # time.sleep(30)
        # tran4 = translation.get_string("about", "about.addkey")
        # print("translation %s" % tran4)
        # time.sleep(3)
        # tran5 = translation.get_string("about", "about.addkey")
        # print("translation %s" % tran5)
        # tran4 = translation.get_string("about", "about.message")
        # #assert tran4 == "test_pl_value_change"
        # print("translation %s" % tran4)
        # time.sleep(10)
        # tran5 = translation.get_string("about", "about.message")
        # #assert tran4 == "test_pl_value_change"
        # print("translation5 %s" % tran5)

    @pytest.mark.cache2
    @pytest.mark.skip
    def test_l5(self):
        print("cache expired and update conpomtent -- bug 1019")
        I18N.set_current_locale("fr")
        # tran = I18N.get_release(PRODUCT, VERSION).get_translation().get_string("aadcomponent","about.message")
        # print(tran)
        self.rel = I18N.get_release(PRODUCT, VERSION)
        translation = self.rel.get_translation()
        # tran1 = translation.get_string("about","about.message")
        # print(tran1)
        # assert tran1 == "about.message"
        tran2 = translation.get_string("aadcomponent", "about.message")
        # assert tran2 == "about.message"
        print(tran2)
        # tran3 = translation.get_string("contact","contact.message")
        # #assert tran2 == "about.message"
        # print(tran3)
        # os.system(r'E:\python_client\test_pythoncode\online_bat\Addcomponenten.bat')
        # os.system(r'E:\test_pythoncode\online_bat\Addcomponent.bat')
        os.system(r'E:\E2\python_client\test_pythoncode\online_bat\Addcomponenten.bat')
        os.system(r'E:\E2\python_client\test_pythoncode\online_bat\Addcomponent.bat')
        time.sleep(15)
        tran2 = translation.get_string("addcomponent", "about.message")
        assert tran2 == "about.message"
        time.sleep(10)
        tran3 = translation.get_string("addcomponent", "about.message")
        print(tran3)
        time.sleep(10)
        tran3 = translation.get_string("addcomponent", "about.message")
        print(tran3)
        # assert tran3 =="test_value_change123"
        time.sleep(10)
        tran4 = translation.get_string("addcomponent", "about.message")
        assert tran4 == "test_value_change123"


if __name__ == '__main__':
    pytest.main(['-s', '-k test_online_cache_no_cache_control_and_no_cache_expired_time'])
