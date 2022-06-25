# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require 'yaml'

module SgtnClient # :nodoc:
  autoload :Common,             'sgtn-client/common'
  autoload :TranslationLoader,  'sgtn-client/loader'
  autoload :SingleOperation,    'sgtn-client/common/single_operation'

  module Core # :nodoc:
    autoload :Cache, 'sgtn-client/core/cache'
  end

  autoload :Translation,        'sgtn-client/api/translation'
  autoload :T,                  'sgtn-client/api/t'
  autoload :Source,             'sgtn-client/api/source'
  autoload :Config,             'sgtn-client/core/config'
  autoload :Exceptions,         'sgtn-client/core/exceptions'
  autoload :ValidateUtil,       'sgtn-client/util/validate-util'
  autoload :LocaleUtil,         'sgtn-client/util/locale-util'
  autoload :CacheUtil,          'sgtn-client/util/cache-util'
  autoload :StringUtil,         'sgtn-client/util/string-util'
  autoload :SingletonError,     'sgtn-client/exceptions'
  autoload :I18nBackend,        'sgtn-client/i18n_backend'

  module Formatters # :nodoc:
    autoload :PluralFormatter, 'sgtn-client/formatters/plurals/plural_formatter'
  end

  module Implementation # :nodoc:
    extend Forwardable

    def config
      Config.instance
    end
    def_delegators :config, :logger, :logger=

    def load(config_file, env, log_file = nil)
      configurations = YAML.load(File.read(config_file))
      config_hash = configurations[env]
      raise "Configuration[#{env}] NotFound" unless config_hash

      config_hash['log_file'] = log_file if log_file
      config.update(config_hash)
      ValidateUtil.validate_config
    end
  end

  extend Implementation
end
