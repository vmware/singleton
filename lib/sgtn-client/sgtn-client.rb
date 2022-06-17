# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

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
            def configure(options = {}, &block)
            Config.configure(options, &block)
            end

            include Logging
            def load(*args)
                  # load configuration file
                  begin
                    Config.load(args[0], args[1])
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
                  if args[2] != nil
                        file = args[2]
                  end
                  file = File.open(file, 'a')
                  file.sync = true
                  logger = Logger.new(file, LOGFILE_SHIFT_AGE)

                  # Set log level for sandbox mode
                  env = Config.default_environment
                  mode = Config.configurations[env]["mode"]
                  logger.debug "[Client][load]set log level, mode=#{mode}"
                  if mode == 'sandbox'
                        logger.level = Logger::DEBUG
                  else 
                        logger.level = Logger::INFO
                  end

                  # initialize cache
                  disable_cache = Config.configurations[env]["disable_cache"]
                  logger.debug "[Client][load]cache initialize, disable_cache=#{disable_cache}"
                  if disable_cache != nil
                        Core::Cache.initialize(disable_cache)
                  else
                        Core::Cache.initialize()
                  end
            end

            def logger
                  Config.logger
            end

            def logger=(log)
                  Config.logger = log
            end           
      end
  
end
