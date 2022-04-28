# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'singleton-client'

# load config file to initialize app
Singleton.load_config('./config/sgtnclient.yml', 'test')

component = 'JAVA'
locale = 'zh-CN'
en_locale = 'en'

normal_cases = {}
plural_cases = {}
formatting_cases = {}
setting_locale = {}
string_translation = { 'normal' => normal_cases, 'plural' => plural_cases, 'formatting' => formatting_cases, 'setting locale' => setting_locale }

# translate a string
normal_cases['translate a string'] = Singleton.t('helloworld', component, locale)

# translate a string
normal_cases['translate an English string'] = Singleton.t('helloworld', component, en_locale)

# translate a string only in source
normal_cases['translate a string only in source'] = Singleton.t('hello', component, locale)

# translate a non-existing string
normal_cases['translate a nonexistent string returning key'] = Singleton.t('nonexistent', component, locale)
normal_cases['translate a nonexistent string with default value'] = Singleton.t('nonexistent', component, locale) { 'default value when translation is missing' }

# translate a string with pluralization
plural_cases['translate a plural string - 0'] = Singleton.t('plural_key', component, locale, cat_count: 0, place: '房间')
plural_cases['translate a plural string - 1'] = Singleton.t('plural_key', component, locale, cat_count: 1, place: '盒子')
plural_cases['translate a plural string - 2'] = Singleton.t('plural_key', component, locale, cat_count: 2, place: 'bush')
plural_cases['-----------------------'] = ''
plural_cases['translate an English plural string - 0'] = Singleton.t('plural_key', component, en_locale, cat_count: 0, place: 'room')
plural_cases['translate an English plural string - 1'] = Singleton.t('plural_key', component, en_locale, cat_count: 1, place: 'box')
plural_cases['translate an English plural string - 2'] = Singleton.t('plural_key', component, en_locale, cat_count: 2, place: 'bush')

# format translation
formatting_cases["translate a string with placeholders - #{locale}"] = Singleton.t('welcome', component, locale, place: '虚拟世界', name: '机器人')
formatting_cases["translate a string with placeholders - #{en_locale}"] = Singleton.t('welcome', component, en_locale, place: '虚拟世界', name: '机器人')

# set locale
Singleton.locale = locale
setting_locale["set loale to #{locale} before translating"] = Singleton.t('helloworld', component)
Singleton.locale = en_locale
setting_locale["set loale to #{en_locale} before translating"] = Singleton.t('helloworld', component)

bundle_translations = {}
bundle_translations["#{locale} translations"] = Singleton.get_translations(component, locale)
bundle_translations['English translations'] = Singleton.get_translations(component, en_locale)
Singleton.locale = locale
bundle_translations["set loale to #{locale}"] = Singleton.get_translations(component)

@Result = { 'string translation' => string_translation, 'bundle translations' => bundle_translations }

pp @Result
