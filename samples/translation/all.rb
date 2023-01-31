# Copyright 2022-2023 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'singleton-client'

# load config file to initialize app
Sgtn.load_config('./config/sgtnclient.yml', 'test')
Sgtn.pseudo_tag = '@&'

key = 'helloworld'
plural_key = 'plural_key'
component = 'JAVA'
locale = 'zh-CN'
en_locale = 'en'

################## string translations ###################
normal_cases = {}
plural_cases = {}
formatting_cases = {}
setting_locale = {}
string_translation = { 'normal' => normal_cases, 'plural' => plural_cases, 'formatting' => formatting_cases, 'setting locale' => setting_locale }

# translate a string
normal_cases['translate a string'] = Sgtn.t!(key, component, locale)

# translate an English string
normal_cases['translate an English string'] = Sgtn.t!(key, component, en_locale)

# translate a string only in source
normal_cases['translate a string only in source'] = Sgtn.t!('hello', component, locale)

# translate a string whose source is changed
normal_cases['translate a string whose source is changed'] = Sgtn.t!('source_changed_key', component, locale, place_holder: 'sky')

# translate a non-existing string
normal_cases['translate a nonexistent string, returning key'] = Sgtn.t('nonexistent', component, locale)

# pseudo translation
string_translation['pseudo translation'] = Sgtn.t!(key, component, Sgtn::PSEUDO_LOCALE)

# translate a string with pluralization
plural_cases['translate a plural string - 0'] = Sgtn.t!(plural_key, component, locale, cat_count: 0, place: '房间')
plural_cases['translate a plural string - 1'] = Sgtn.t!(plural_key, component, locale, cat_count: 1, place: '盒子')
plural_cases['translate a plural string - 2'] = Sgtn.t!(plural_key, component, locale, cat_count: 2, place: 'bush')
plural_cases['-----------------------'] = ''
plural_cases['translate an English plural string - 0'] = Sgtn.t!(plural_key, component, en_locale, cat_count: 0, place: 'room')
plural_cases['translate an English plural string - 1'] = Sgtn.t!(plural_key, component, en_locale, cat_count: 1, place: 'box')
plural_cases['translate an English plural string - 2'] = Sgtn.t!(plural_key, component, en_locale, cat_count: 2, place: 'bush')

# format translation
formatting_cases["translate a string with placeholders - #{locale}"] = Sgtn.t!('welcome', component, locale, place: '虚拟世界', name: '机器人')
formatting_cases["translate a string with placeholders - #{en_locale}"] = Sgtn.t!('welcome', component, en_locale, place: '虚拟世界', name: '机器人')

# set locale
Sgtn.locale = locale
setting_locale["set locale to #{locale} before translating"] = Sgtn.t!(key, component)
Sgtn.locale = en_locale
setting_locale["set locale to #{en_locale} before translating"] = Sgtn.t!(key, component)
##########################################################

################## bundle translations ###################
bundle_translations = {}
bundle_translations["#{locale} translations"] = Sgtn.get_translations!(component, locale)
bundle_translations['English translations'] = Sgtn.get_translations!(component, en_locale)
Sgtn.locale = locale
bundle_translations["set locale to #{locale}"] = Sgtn.get_translations!(component)
bundle_translations['pseudo translations'] = Sgtn.get_translations!(component, Sgtn::PSEUDO_LOCALE)

##########################################################

@result = { 'string translation' => string_translation, 'bundle translations' => bundle_translations }

puts JSON.pretty_generate(@result)
