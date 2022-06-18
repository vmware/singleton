# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/loader/loader_factory'

module SgtnClient
      LOGFILE_SHIFT_AGE = 4

      autoload :Common,             'sgtn-client/common'
      autoload :TranslationLoader,  'sgtn-client/loader'
      autoload :SingleOperation,    'sgtn-client/common/single_operation'

      module Core
            autoload :Cache,        "sgtn-client/core/cache"
      end

      autoload :Translation,        "sgtn-client/api/translation"
      autoload :T,                  "sgtn-client/api/t"
      autoload :Source,             "sgtn-client/api/source"
      autoload :Config,             "sgtn-client/core/config"
      autoload :Logging,            "sgtn-client/core/logging"
      autoload :Exceptions,         "sgtn-client/core/exceptions"
      autoload :ValidateUtil,       "sgtn-client/util/validate-util"
      autoload :LocaleUtil,         "sgtn-client/util/locale-util"
      autoload :CacheUtil,          "sgtn-client/util/cache-util"
      autoload :StringUtil,         "sgtn-client/util/string-util"
      autoload :SingletonError,     'sgtn-client/exceptions'
      autoload :I18nBackend,        "sgtn-client/i18n_backend"

      module Formatters
            autoload :PluralFormatter,    "sgtn-client/formatters/plurals/plural_formatter"
      end


      class << self
            include Logging

            def config
                  @config ||= SgtnClient::Config.instance
            end

            def load(file_name, env = nil, log_file = nil)
                  begin
                        configurations = YAML.load(File.read(file_name))
                        env ||= ENV['SGTN_ENV'] || ENV['RACK_ENV'] || ENV['RAILS_ENV'] || 'development'
                        config_hash = configurations[env]
                        raise "Configuration[#{env}] NotFound" unless config_hash

                        config_hash.each do |key, value|
                              config.send("#{key}=", value)
                        end
						ValidateUtil.validate_config()
                  rescue => exception
                    file = File.open('./error.log', 'a')
                    file.sync = true
                    log = Logger.new(file)
                    log.error exception.message
                  end

                  # create log file
                  file = './sgtnclient_d.log'
                  logger.debug "[Client][load]create log file=#{file}"
                  if log_file != nil
                        file = log_file
                  end
                  file = File.open(file, 'a')
                  file.sync = true
                  logger = Logger.new(file, LOGFILE_SHIFT_AGE)

                  # Set log level for sandbox mode
                  mode = SgtnClient.config.mode
                  SgtnClient.logger.debug "[Client][load]set log level, mode=#{mode}"
                  if mode == 'sandbox'
                        logger.level = Logger::DEBUG
                  else 
                        logger.level = Logger::INFO
                  end

                  SgtnClient::Core::Cache.initialize()
            end

            def logger
                  config.logger
            end

            def logger=(log)
                  config.logger = log
            end           
      end
  
end
