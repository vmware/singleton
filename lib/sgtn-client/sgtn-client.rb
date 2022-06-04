# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require 'logger'
require 'lumberjack'

module SgtnClient # :nodoc:
  module Core # :nodoc:
    autoload :Cache, 'sgtn-client/core/cache'
  end

  autoload :Translation,   'sgtn-client/api/translation'
  autoload :T,             'sgtn-client/api/t'
  autoload :Source,        'sgtn-client/api/source'
  autoload :Config,        'sgtn-client/core/config'
  autoload :Exceptions,    'sgtn-client/core/exceptions'
  autoload :ValidateUtil,  'sgtn-client/util/validate-util'
  autoload :LocaleUtil,    'sgtn-client/util/locale-util'
  autoload :FileUtil,      'sgtn-client/util/file-util'
  autoload :CacheUtil,     'sgtn-client/util/cache-util'
  autoload :StringUtil,    'sgtn-client/util/string-util'

  module Formatters # :nodoc:
    autoload :PluralFormatter, 'sgtn-client/formatters/plurals/plural_formatter'
  end

  module Implementation # :nodoc:
    extend Forwardable

    def_delegators Config, :logger, :logger=

    def load(config_file, env, log_file = nil)
      SgtnClient::Config.load(config_file, env)
      SgtnClient::Config.configurations[SgtnClient::Config.default_environment]['log_file'] = log_file if log_file
      SgtnClient::ValidateUtil.validate_config
    end
  end

  extend Implementation
end
