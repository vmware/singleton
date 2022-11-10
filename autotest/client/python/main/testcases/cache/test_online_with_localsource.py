import time
from pathlib import Path
import multiprocessing

import pytest

from utils import ContextModifyCacheDe1
from sgtn4python.sgtnclient import I18N

PRODUCT = 'PythonClient'
VERSION = '6.0.0'
COMPONENT = 'about'
LOCALE = 'de'
Config_files = 'sample_online_localsource.yml'

__CACHE__ = Path(__file__).parent
__CONFIG__ = __CACHE__.joinpath('config')
__RESOURCES__ = __CACHE__.joinpath('resources')


def process_task(value):
    file: Path = __CONFIG__.joinpath(Config_files)
    I18N.add_config_file(file)
    I18N.set_current_locale(LOCALE)
    rel = I18N.get_release(PRODUCT, VERSION)
    translation = rel.get_translation()

    # get_source from server and save in localCache.
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == value, tran1


class TestOnlineCacheWithLocalSource:

    @pytest.mark.cache1
    def test_cache_path(self):
        print("cache expired and update")
        """
        mix mode: cache only support online mode.
        1. when new i18n instance. it can load cache from cache_path if exist.
        2. no matter cache come from python or cache_path, if not expired,use cache,if expired request server and refresh cache_path if exist
        """
        file: Path = __CONFIG__.joinpath(Config_files)
        I18N.add_config_file(file)
        I18N.set_current_locale(LOCALE)
        rel = I18N.get_release(PRODUCT, VERSION)
        translation = rel.get_translation()

        # get_source from server and save in localCache.
        tran1 = translation.get_string("about", "about.message")
        assert tran1 == "test__de_value"

        # get source from offline and can not use cache
        tran1 = translation.get_string("about", "about.offline")
        assert tran1 == "test en-Us offline value"

        # new process can create new L18N with no cache. it load cache from cache_path.
        # this step can create .cache dir. NOT　DELETE and MOVE.
        task1 = multiprocessing.Process(target=process_task, args=("test__de_value",))
        task1.start()
        task1.join()

        # if modify localCache.
        file_path = __CACHE__.joinpath(".cache", "PythonClient", "6.0.0", "about", "messages_de.json")
        with ContextModifyCacheDe1(file_path):
            # when expired_time is active, read from localCache directly
            task2 = multiprocessing.Process(target=process_task, args=("test__de_value_change_cache",))
            task2.start()
            task2.join()

            # if cache expired , return cache and request server and refresh cache
            time.sleep(10)
            task1 = multiprocessing.Process(target=process_task, args=("test__de_value_change_cache",))
            task1.start()
            task1.join()

            # read in cache with new source
            time.sleep(1)
            task1 = multiprocessing.Process(target=process_task, args=("test__de_value",))
            task1.start()
            task1.join()


if __name__ == '__main__':
    pytest.main(['-s', '-k TestOnlineCacheWithLocalSource'])
