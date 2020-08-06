---
title: "Python Client Introduction"
date: 2020-07-30T20:07:51+08:00
draft: false
weight: 10
---


# Introduction
Several python files are provided as the python client to support accessing Singleton service, making Singleton users to handle its globalization easily.


# Features in Python Client

- Getting configuration content.
- Getting the translation.
- Getting messages of a locale.


# Prerequisites

Python 2.x

Python 3.x

It's better to install [PyYAML](https://pypi.org/project/PyYAML/) module of python.


# How to get the client code
```
git clone -b g11n-python-client https://github.com/vmware/singleton.git
```
The client code is [sgtn_client.py](https://github.com/vmware/singleton/blob/g11n-python-client/sgtn4python/sgtn_client.py), [sgtn_properties.py](https://github.com/vmware/singleton/blob/g11n-python-client/sgtn4python/sgtn_properties.py) and [sgtn_util.py](https://github.com/vmware/singleton/blob/g11n-python-client/sgtn4python/sgtn_util.py).
It needs a configuration file, like the [sample configuration file](https://github.com/vmware/singleton/blob/g11n-python-client/sgtn4python/sample/sample_online_offline.yml).


# APIs Available
* [Factory Class](#Factory-Class)
   * [I18n](#I18n)
* [Interface For Release](#Interface-For-Release)
   * [Release](#Release)
* [Interface For Configuration](#Interface-For-Configuration)
   * [Config](#Config)
* [Interface For Translation](#Interface-For-Translation)
   * [Translation](#Translation)

## Factory Class
### I18n

* It's the factory class that creates and initializes the configuration object and the release object.

```python
class I18n():
    def add_config_file(cls, config_file)
    def add_config(cls, base_path, config_data)
    def set_current_locale(cls, locale)
    def get_current_locale(cls)
    def get_release(cls, product, version)
```

### I18n / add_config_file

* Load a configuration json or yaml file and initialize a corresponding release object.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| config_file | string | Configuration file |

### I18n / add_config

* Load a configuration json or yaml text with a base directory and initialize a correspondent release object.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| base_path | string | Base directory |
| config_data | string | Configuration text in json or yaml |

### I18n / set_current_locale

* Set locale of current thread.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale name |

### I18n / get_current_locale

* Get locale of current thread.

| Return | Description |
| ------ | ------ |
| string | Locale name |

### I18n / get_release

| Parameter | Type | Description |
| ------ | ------ | ------ |
| product | string | Product name |
| version | string | Version |

* Get release object.

| Return | Description |
| ------ | ------ |
| [Release](#Release) | Release object |


## Interface For Release
### Release
```python
class Release:
    def get_config(self)
    def get_translation(self)
```

### Release / get_config

* Get the configuration object owned by the release object.

| Return | Description |
| ------ | ------ |
| [Config](#Config) | The configuration object |

### Release / get_translation

* Get the translation object owned by the release object.

| Return | Description |
| ------ | ------ |
| [Translation](#Translation) | The translation object |


## Interface For Configuration
### Config
```python
class Config:
    def get_config_data(self)
    def get_info(self)
```
### Config / get_config_data

* Get configuration data.

| Return | Description |
| ------ | ------ |
| object | Configuration data |

### Config / get_info

* Get brief information of configuration.

| Return | Description |
| ------ | ------ |
| object | Brief information data |


## Interface For Translation
### Translation
```python
class Translation:
    def get_string(self, component, key, **kwargs)
    def get_locale_strings(self, locale)
    def get_locale_supported(self, locale)
```

### Translation / get_string

* Get the translation message of a component and a key.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| component | string | Component |
| key | string | Key |
| kwargs | object | Parameters like 'source', 'locale' and 'format_items' |

| Return | Description |
| ------ | ------ |
| string | The translation message |

### Translation / get_locale_strings

* Get translation messages of a locale.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |

| Return | Description |
| ------ | ------ |
| object | All translation messages of a locale |

### Translation / get_locale_supported

* Get supported locale name of the input locale.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale |

| Return | Description |
| ------ | ------ |
| string | Supported locale name |
