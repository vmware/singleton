# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient

  class ValidateUtil

      def self.validate_config()
        SgtnClient.logger.debug "[ValidateUtil][validate_config]"
        messages = ""

        mode = SgtnClient.config.mode
        if mode != 'sandbox' && mode != 'live'
          messages =  messages + "Configuration[mode] has to be 'sandbox' or 'live'!\n"
        end

        version = SgtnClient.config.version
        SgtnClient.config.version = version.to_s
        if version.to_s !~ /\A(\d+\.)*\d+\z/
          messages = messages +  "Configuration[version] has to be standard as '#.#.#, e.g '1.0.0'!\n"
        end
        
        cache_expiry_period = SgtnClient.config.cache_expiry_period
        if cache_expiry_period != nil && (cache_expiry_period.is_a? Integer) == false
          messages = messages +  "Configuration[cache_expiry_period] has to be a number!\n"
        end
        
        disable_cache = SgtnClient.config.disable_cache
        if disable_cache != nil && disable_cache != false && disable_cache != true
          messages = messages +  "Configuration[disable_cache] has to be a 'true' or 'false'!\n"
        end
        
        if messages != ""
          raise Exceptions::MissingConfig.new(messages)
        end
      end

  end

end
