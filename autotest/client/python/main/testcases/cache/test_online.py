# import time
# from pathlib import Path
#
# import pytest
# from sgtnclient import I18N
#
# from .utils import ModifyCacheContext
#
# PRODUCT = "Cache"
# VERSION = "1.0.1"
# COMPONENT = "about"
# LOCALE = "de"
#
# __CACHE__ = Path(__file__).parent
# __CONFIG__ = __CACHE__.joinpath('config')
# __RESOURCES__ = __CACHE__.joinpath('resources')
#
# BASE_URL = "http://localhost:8091"
#
# """
# Cache 测试
#
# 配置项生效
# 1. 服务端不配置cache-control。默认读取yaml->cache_expired_time。如果无配置项，默认60秒。
# 2. 服务端配置cache-control=0
# 3. 服务端配置cache-control=5，配置项yaml->cache_expired_time=10秒。按照5秒计算
#
# 场景测试
# 前置条件：cache_expired_time默认5秒生效。
# 1. 缓存失效，拷贝测试.cache。进行测试5秒内，不会更新测试cache，5秒后，更新测试cache。
# 2. 缓存粒度，请求key，local，product。cache根据component级别进行缓存和更新。
# 3. 更新策略，5秒过期后，再次请求，返回旧值，同时更新缓存
# """
#
#
# class TestOnlineCache:
#
#     @pytest.mark.cache1
#     @pytest.mark.skip("Manual due to Conflict")
#     def test_online_cache_update(self):
#         """
#         Online Mode:
#         1. cache expire time: 5s
#         2. cache content {messages_$locale.json} and different key will return from cache.
#         3. must run as script, cache_path active only run as script
#         4. run as script could not expire. everytime it start, it will load .cache and recalculate expire time.
#         """
#         config_file = "online_without_cache_control.yml"
#         file: Path = __CONFIG__.joinpath(config_file)
#         I18N.add_config_file(file)
#         I18N.set_current_locale(LOCALE)
#         rel = I18N.get_release(PRODUCT, VERSION)
#         translation = rel.get_translation()
#
#         # cache expired and get old value
#         file = Path(__file__).parent.joinpath(".cache").joinpath("Cache").joinpath("1.0.1").joinpath("about").joinpath(
#             "messages_de.json")
#         with ModifyCacheContext(file):
#             tran1 = translation.get_string("about", "about.message")
#             assert tran1 == "test de key (CACHED)"
#
#         # run as script, if start load .cache not check expire. during 5s expire
#         tran1 = translation.get_string("about", "about.message")
#         assert tran1 == "test de key (CACHED)"
#
#         # cache expired will request server to update
#         time.sleep(5.5)
#         tran1 = translation.get_string("about", "about.message")
#         assert tran1 == "test de key (CACHED)"
#
#         # cache will update
#         time.sleep(1)
#         with open(file, mode='r') as f:
#             data = f.readlines()
#         assert data[7] == '    "about.message": "test de key",\n'
#
#     @pytest.mark.cache1
#     def test_online_cache_translation(self):
#         """
#         Online Mode:
#         1. cache component + locale
#         2. same component + locale , diff key will return from cache.
#         3. diff component or diff locale will request server
#         """
#         config_file = "online_without_cache_control.yml"
#         file: Path = __CONFIG__.joinpath(config_file)
#         I18N.add_config_file(file)
#         rel = I18N.get_release(PRODUCT, VERSION)
#         translation = rel.get_translation()
#
#         # diff local will request server
#         tran1 = translation.get_string("about", "about.message", locale="fr")
#         assert tran1 == "test fr key"
#
#         # diff key will return from cache
#         tran1 = translation.get_string("about", "about.description", locale="fr")
#         assert tran1 == "Utilisez cette zone pour fournir des informations supplémentaires"
#
#         # diff component will request server
#         tran1 = translation.get_string("contact", "contact.message", locale="de")
#         assert tran1 == "Ihrer Kontaktseite."
#
#
# if __name__ == '__main__':
#     pytest.main(['-s', 'TestOnlineCache'])
