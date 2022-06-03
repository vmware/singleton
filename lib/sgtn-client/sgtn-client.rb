# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'logger'
require 'lumberjack'

module SgtnClient
  module Core
    autoload :Cache, 'sgtn-client/core/cache'
  end

  autoload :Translation,        'sgtn-client/api/translation'
  autoload :T,                  'sgtn-client/api/t'
  autoload :Source,             'sgtn-client/api/source'
  autoload :Config,             'sgtn-client/core/config'
  autoload :Exceptions,         'sgtn-client/core/exceptions'
  autoload :ValidateUtil,       'sgtn-client/util/validate-util'
  autoload :LocaleUtil,         'sgtn-client/util/locale-util'
  autoload :FileUtil,           'sgtn-client/util/file-util'
  autoload :CacheUtil,          'sgtn-client/util/cache-util'
  autoload :StringUtil,         'sgtn-client/util/string-util'

  module Formatters
    autoload :PluralFormatter, 'sgtn-client/formatters/plurals/plural_formatter'
  end

  class << self
    def load(config_file, env, log_file = './singleton.log')
      SgtnClient::Config.load(config_file, env)
      SgtnClient::ValidateUtil.validate_config

      # create logger
      env = SgtnClient::Config.default_environment
      mode = SgtnClient::Config.configurations[env]['mode']
      level = mode == 'sandbox' ? :debug : :info
      logger.info "[Client][load]create log file=#{log_file}, log level=#{level}"
      @logger = Lumberjack::Logger.new(log_file, level: level, max_size: '1M', keep: 4)
    end

    def logger
      @logger ||= Logger.new(STDOUT)
    end

    attr_writer :logger
  end
end
