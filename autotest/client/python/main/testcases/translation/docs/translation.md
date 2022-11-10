### 支持模式：


环境安装
1. 进入python虚拟环境 source ~/virtualenv/python/bin/active
2. pip install -r requirements.txt
3. cd sgtn4python
4. python setup.py build
5. python setup.py install 安装sgtnclient
6. pip install test/ICU/libs/PyICU-2.9-cp39-cp39-win_amd64.whl 安装icu库

1. online
    * 线上模式，如果有lastest和en。一定会对比，如果不相等，直接返回latest，如果相等，才会返回
    * get_string(component, key, locale, source)参数解析
        * component如果没有命中，会直接返回4xx，触发混合模式
        * key没有的话，服务器不会返回文件，本地直接处理返回key。
        * locale如果没有配置或者配置成None，默认en，或者读取配置
        * source如果配置，则会比较服务器的message_latest.json 和 message_en.json，message_latest是收集上来的最新source，message_
          %lc.json是翻译返回的数据。 如果配置了source。会优先通过key-source去latest中查询，如果查询到了，在去en中查询，
2. offline 
      * source如果指定，会根据component-key-source组合去指定的地方查询$COMPONENT/messages_en.properties文件。
        如果找到，说明最新的en已经同步latest，可以直接读取语言翻译，如果找不到，source还没有同步到en。直接返回source

         ```yml
         locales:
             -   language_tag: en-US
                 source_resources_path:
                     - $COMPONENT/messages.properties
                 offline_resources_path:
                     - $COMPONENT/messages_en.properties
         
         ```

3. online+offline 不支持
4. online+offline+localcache

```yaml
product: PythonClient
l10n_version: 1.0.1

online_service_url: http://localhost:8091
#offline_resources_base_url: http://localhost:8080/bundles/PythonClient/4.0.0/ # 最后必须增加/，否则执行失败
#offline_resources_base_url: file://d:/l10ntest/PythonClient/2.0.0/
# log path for the python client
log_path: ../log/   # 相对i18n实例的位置

# local storage path for caching
#cache_path: ./singleton_default/

# how many seconds should be waited to try again for accessing the remote service,
# when there is no data cached
try_delay: 10

# 这个如果不配置默认en，并且翻译指定的locale未命中，则返回配置的翻译。
# 优先级，默认最低为en，其次配置文件default_locale:de，最后是I18N.set_current_locale("fr")优先级最高
default_locale: de
source_locale: en

# how many seconds should be waited to update cache data when there is old data existed
cache_expired_time: 60

#multitask: async
```

