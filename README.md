Singleton Client for Python
============

One python file is provided as the python client to support sending API requests to Singleton service.
When integrated on the client side, it also provides additional features such as post-processing of localized resources and caching.


Prerequisites
------------

Python 2.x

Python 3.x

It's better to install [PyYAML](https://pypi.org/project/PyYAML/) module of python.


How to get the client code
------------

 * Clone the repository by Git.
    ```
    git clone -b g11n-python-client https://github.com/vmware/singleton.git
    ```

The client code is [sgtn_client.py](sgtn4python/sgtn_client.py), [sgtn_util.py](sgtn4python/sgtn_util.py) and [sgtn_properties.py](sgtn4python/sgtn_properties.py).
It needs a configuration file, for example, [sgtn_client.yml](sgtn4python/test/sgtn_client.yml).


How to write the configuration file
------------

Please refer to the configuration file [sample_online_only.yml](sgtn4python/sample/sample_online_only.yml) in the sample application.


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



