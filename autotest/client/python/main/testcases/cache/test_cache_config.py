import time
from pathlib import Path

import pytest
from sgtnclient import I18N

PRODUCT = "Cache"
VERSION = "1.0.1"
COMPONENT = "about"
LOCALE = "de"

_CACHE_DIR_ = Path(__file__).parent

_CONFIG_DIR_ = _CACHE_DIR_.joinpath("configs")
_RESOURCES_DIR_ = _CACHE_DIR_.joinpath("resources")

BASE_URL = "http://localhost:8091"

"""
Cache 测试

前置条件：
1. PRODUCT = Cache
2. java的服务需要修改，删除cache-control字段
3. go 服务删除cache-control字段
4. 特殊服务端启动

配置项生效
1. 服务端不配置cache-control。默认读取yaml->cache_expired_time。如果无配置项，默认60秒。
2. 服务端配置cache-control=0
3. 服务端配置cache-control=5，配置项yaml->cache_expired_time=10秒。按照5秒计算

用例1. 服务端和yaml都不配置过期时间。默认60秒
用例2. 服务端未配置过期时间，yaml配置10秒，过期时间10秒
用例3. 服务器配置过期时间，按照服务器为准
"""


def test_online_cache_with_no_server_cache_control():
    """
    Online Mode: java or go
    1. server cache-control.value=max-age=83600, public. disabled
    2. cache_expired_time: 5
    3. cache_path: .cache
    4. .cache directory is existed

    cache_expired_time active
    """
    config_file = "online_without_cache_control.yml"
    file: Path = _CONFIG_DIR_.joinpath(config_file)
    I18N.add_config_file(file)
    I18N.set_current_locale(LOCALE)

    rel = I18N.get_release(PRODUCT, VERSION)
    translation = rel.get_translation()

    # get old value and request server. a real request come in server
    translation.get_string("about", "about.message")
    # assert tran1 == "test de key"
    time.sleep(4.5)

    # get from cache
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"
    time.sleep(5.5)

    # cache expired and request server.
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"


def test_online_cache_control_max_age_0_seconds():
    """
    Online Mode: java or go
    1. server cache-control.value=max-age=0, public. disabled
    2. cache_expired_time: 10
    3. cache_path: .cache
    4. .cache directory is existed

    cache_expired_time: 10 active
    """
    config_file = "online_with_cache_control_5_seconds.yml"
    file: Path = _CONFIG_DIR_.joinpath(config_file)
    I18N.add_config_file(file)
    I18N.set_current_locale(LOCALE)
    print(I18N._get_release_manager())
    rel = I18N.get_release(PRODUCT, VERSION)
    translation = rel.get_translation()

    # get old value and request server. a real request come in server
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"

    # get from cache
    time.sleep(9.5)
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"

    # cache expired and request server
    time.sleep(10.5)
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"


@pytest.mark.cache2
def test_online_cache_control_max_age_5_seconds():
    """
    Online Mode: java or go
    1. server cache-control.value=max-age=5, public. disabled
    2. cache_expired_time: 10
    3. cache_path: .cache
    4. .cache directory is existed

    server cache-control.value=max-age=5 active
    """
    config_file = "online_with_cache_control_5_seconds.yml"
    file: Path = _CONFIG_DIR_.joinpath(config_file)
    I18N.add_config_file(file)
    I18N.set_current_locale(LOCALE)
    rel = I18N.get_release(PRODUCT, VERSION)
    translation = rel.get_translation()

    # get old value and request server. a real request come in server
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"
    time.sleep(4.5)

    # get from cache
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"
    time.sleep(5.5)

    # check cache expired and request server.
    tran1 = translation.get_string("about", "about.message")
    assert tran1 == "test de key"
