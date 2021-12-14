---
title: "Python Client Introduction"
date: 2021-12-10T10:07:51+08:00
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

It's better to install [PyYAML](https://pypi.org/project/PyYAML/) module of python. Otherwise, the configuration file should be written in json.


# How to use python client as a python package
Install [sgtnclient](https://pypi.org/project/sgtnclient) as a python package.
```
pip install sgtnclient
```
```
pip3 install sgtnclient
```


# How to get and use the client code
```
git clone -b g11n-python-client https://github.com/vmware/singleton.git
```
Install it as a python package.
```
python setup.py install
```
Or use its code directly by putting them to a place where they can be imported to the application code.


# How to write a configuration file
The python client of Singleton needs a configuration file. Here is an [example](https://github.com/vmware/singleton/blob/g11n-python-client/sgtn4python/sample/sample_online_localsource.yml).

* Basic definition

| Key | Type | Description |
| ------ | ------ | ------ |
| product | string | Product name |
| l10n_version | string | L10N version, sometimes same with product version |
| online_service_url | string | Points to singleton service |
| offline_resources_base_url | string | Points to local directory starting with 'file://' |
| components | dict | Definition of components |

* Extended definition

| Key | Type | Description | Default |
| ------ | ------ | ------ | ------ |
| default_locale | string | Default locale | en-US |
| source_locale | string | Source locale | Same with default locale |
| log_path | string | Log path, './' means the path of configuration. | None. Not in use |
| cache_type | string | Cache type. 'by_key' means not using component in api. | default |
| cache_path | string | Cache file path, './' means the path of configuration. | None. Not in use |
| try_wait | integer | Interval to try again when failed and max delay of http request | 10 |
| cache_expired_time | integer | Interval to update data | 3600 |
| pseudo | string | Switch of pseudo testing | false |
| multitask | string | 'async' means using coroutine | None. Not by coroutine |

* Component definition

| Key | Type | Description | Default |
| ------ | ------ | ------ | ------ |
| name | string | Component name |  |
| locales | dict | Definition for locales inside a component item |  |
| template | string | Top item name to define a template for component  | component_template |
| locales_refer | string | Top item name to define locales |  |
| language_tag | string | Locale name |  |
| offline_resources_path | string | Local resource path |  |

* Special string for component definition

| String | Description |
| ------ | ------ |
| $COMPONENT | Current component inside a component item |
| $LOCALE | Current locale inside a locale item |

* Example of component definition in original way

```yaml
components:
  - name: about
    locales:
      - language_tag: en-US
        offline_resources_path:
          - about/messages.properties
      - language_tag: de
        offline_resources_path:
          - about/messages_de.json
      - language_tag: fr
        offline_resources_path:
          - about/messages_fr.json
  - name: common
    locales:
      - language_tag: en-US
        offline_resources_path:
          - common/messages.properties
      - language_tag: de
        offline_resources_path:
          - common/messages_de.json
      - language_tag: fr
        offline_resources_path:
          - common/messages_fr.json
  - name: contact
    locales:
      - language_tag: en-US
        offline_resources_path:
          - contact/messages.properties
      - language_tag: de
        offline_resources_path:
          - contact/messages_de.json
      - language_tag: fr
        offline_resources_path:
          - contact/messages_fr.json
```

* Example of component definition in concise way

```yaml
locales:
  - language_tag: en-US
    offline_resources_path:
      - $COMPONENT/messages.properties
  - language_tag: de
  - language_tag: fr

component_template:
  locales_refer: locales
  offline_resources_path:
    - $COMPONENT/messages_$LOCALE.json

components:
  - name: about
    template: component_template # default
  - name: common
  - name: contact
```

# APIs Available
* [Entry of library](#Entry-of-library)
   * [I18N](#I18N)
* [Interface For Release](#Interface-For-Release)
   * [Release](#Release)
* [Interface For Configuration](#Interface-For-Configuration)
   * [Config](#Config)
* [Interface For Translation](#Interface-For-Translation)
   * [Translation](#Translation)

## Entry of library
### I18N

* It's the entry code that creates and initializes the configuration object and the release object.

```python
def add_config_file(config_file, outside_config=None)
def add_config(base_path, config_data)
def set_current_locale(locale)
def get_current_locale()
def get_release(product, version)
```

### I18N / add_config_file

* Load a configuration json or yaml file and initialize a corresponding release object.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| config_file | string | Configuration file |
| outside_config | dict | Configuration that overrides the configuration file |

### I18N / add_config

* Load a configuration json or yaml text with a base directory and initialize a correspondent release object.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| base_path | string | The path of configuration |
| config_data | dict | Configuration data |

### I18N / set_current_locale

* Set locale of current thread.

| Parameter | Type | Description |
| ------ | ------ | ------ |
| locale | string | Locale name |

### I18N / get_current_locale

* Get locale of current thread.

| Return | Description |
| ------ | ------ |
| string | Locale name |

### I18N / get_release

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

* kwargs

| Key | Type | Description | Default |
| ------ | ------ | ------ | ------ |
| locale | string | Locale | Current locale |
| source | string | Source message | Source from resource file |
| format_items | string | Items for string formatting | Nothing |

* Example of string formatting which follows the python format function
```python
msg = trans.get_string(COMPONENT, KEY) => 'AAA{0}BBB{1}CCC'
msg = trans.get_string(COMPONENT, KEY, format_items = ['11', '22']) => 'AAA11BBB22CCC'

msg = trans.get_string(COMPONENT, KEY) => 'AAA{x}BBB{y}CCC'
msg = trans.get_string(COMPONENT, KEY, format_items = {'x': 'ee', 'y': 'ff'}) => 'AAAeeBBBffCCC'
```

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
