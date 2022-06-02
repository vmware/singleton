# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'logging'

module SgtnClient
      module Core
            autoload :Cache,        "sgtn-client/core/cache"
      end

      autoload :Translation,        "sgtn-client/api/translation"
      autoload :T,                  "sgtn-client/api/t"
      autoload :Source,             "sgtn-client/api/source"
      autoload :Config,             "sgtn-client/core/config"
      autoload :Exceptions,         "sgtn-client/core/exceptions"
      autoload :ValidateUtil,       "sgtn-client/util/validate-util"
      autoload :LocaleUtil,         "sgtn-client/util/locale-util"
      autoload :FileUtil,           "sgtn-client/util/file-util"
      autoload :CacheUtil,          "sgtn-client/util/cache-util"
      autoload :StringUtil,          "sgtn-client/util/string-util"

      module Formatters
            autoload :PluralFormatter,    "sgtn-client/formatters/plurals/plural_formatter"
      end


      class << self
            def load(config_file, env, log_file = './sgtnclient.log')
                  # load configuration file
                  begin
                    SgtnClient::Config.load(config_file, env)
                    SgtnClient::ValidateUtil.validate_config()
                  rescue => exception
                    file = File.open('./error.log', 'a')
                    file.sync = true
                    log = Logger.new(file)
                    log.error exception.message
                  end

                  # create log file
                  SgtnClient.logger.info "[Client][load]create log file=#{log_file}"
                  SgtnClient.logger = Logging.logger(log_file, 4, 104857)

                  # Set log level for sandbox mode
                  env = SgtnClient::Config.default_environment
                  mode = SgtnClient::Config.configurations[env]["mode"]
                  SgtnClient.logger.debug "[Client][load]set log level, mode=#{mode}"
                  SgtnClient.logger.level = mode == 'sandbox' ? :debug : :info

                  # initialize cache
                  disable_cache = SgtnClient::Config.configurations[env]["disable_cache"]
                  SgtnClient.logger.debug "[Client][load]cache initialize, disable_cache=#{disable_cache}"
                  if disable_cache != nil
                        SgtnClient::Core::Cache.initialize(disable_cache)
                  else
                        SgtnClient::Core::Cache.initialize()
                  end
            end

            def logger
                  @logger ||= Logging.logger(STDOUT)
            end

            def logger=(log)
                  @logger = log
            end           
      end
  
end
