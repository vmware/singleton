Singleton Client for Python
============

One python package is provided as the python client to support sending API requests to Singleton service.
When integrated on the client side, it also provides additional features such as post-processing of localized resources and caching.


Prerequisites
------------

Python 2.x

Python 3.x

It's better to install [PyYAML](https://pypi.org/project/PyYAML/) module of python. Otherwise please change yaml files to json files.

If formats like Plural, Date, Number are needed, please install [PyICU](https://pypi.org/project/PyICU/) after icu4c is installed on the operating system.


How to get the client code
------------

 * Clone the repository by Git.
    ```
    git clone -b g11n-python-client https://github.com/vmware/singleton.git
    ```

The client code is under directory [sgtnclient](https://github.com/vmware/singleton/blob/g11n-python-client/sgtn4python/sgtnclient).
It needs a configuration file, for example, [sample_online_localsource.yml](https://github.com/vmware/singleton/blob/g11n-python-client/sgtn4python/sample/sample_online_localsource.yml).


How to write the configuration file
------------

Please refer to the [online document](https://github.com/vmware/singleton/blob/website/content/en/docs/overview/singleton-sdk/python-client-introduction.md#how-to-write-a-configuration-file).


How to apply the python client in an application code
------------
 * add configuration into the client
```
I18N.add_config_file('sgtn_client.yml')
```
 * (optional) set locale
```
I18N.set_current_locale('de')
```
 * get translation message
```
release_obj = I18N.get_release(PRODUCT, VERSION)
translation_obj = release.get_translation()
msg = translation_obj.get_string(COMPONENT, KEY, source = SOURCE, locale = LOCALE)
```


Existing features
------------
 * access the singleton service and get translation messages periodically
 * store the translation messages to local storage which is defined in 'cache_path'

Upcoming features
------------

Request for contributions from the community
------------

Sample application
------------
 * Sample python code that uses the python client: [sample_app.py](sgtn4python/sample/sample_app.py)
 * Sample configuration for the application: [sample_online_only.yml](sgtn4python/sample/sample_online_only.yml)
