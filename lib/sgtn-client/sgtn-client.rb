# Copyright 2025 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require 'yaml'

module SgtnClient # :nodoc:
  autoload :Common,             'sgtn-client/common'
  autoload :TranslationLoader,  'sgtn-client/loader'
  autoload :SingleOperation,    'sgtn-client/common/single_operation'
  autoload :Translation,        'sgtn-client/api/translation'
  autoload :Fallbacks,          'sgtn-client/fallbacks'
  autoload :T,                  'sgtn-client/api/t'
  autoload :Source,             'sgtn-client/api/source'
  autoload :Config,             'sgtn-client/core/config'
  autoload :Exceptions,         'sgtn-client/core/exceptions'
  autoload :ValidateUtil,       'sgtn-client/util/validate-util'
  autoload :LocaleUtil,         'sgtn-client/util/locale-util'
  autoload :LocalizedString,    'sgtn-client/util/localized_string'
  autoload :SingletonError,     'sgtn-client/exceptions'

  module Formatters # :nodoc:
    autoload :PluralFormatter, 'sgtn-client/formatters/plurals/plural_formatter'
  end

  class << self
    extend Forwardable

    def_delegator Config, :instance, :config
    def_delegators :config, :logger, :logger=

    def load(config_file, env, log_file = nil)
      configurations = YAML.safe_load(File.read(config_file), aliases: true)
      config_hash = configurations[env]
      raise "Configuration[#{env}] NotFound" unless config_hash

      config_hash['log_file'] = log_file if log_file
      config.update(config_hash)
      ValidateUtil.validate_config
    end

    def locale
      RequestStore.store[:locale] ||= LocaleUtil.get_fallback_locale
    end

    def locale=(value)
      RequestStore.store[:locale] = value
    end
  end
end
