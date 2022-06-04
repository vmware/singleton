# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient

  class ValidateUtil

      def self.validate_config()
        env = SgtnClient::Config.default_environment
        puts "[ValidateUtil][validate_config] env = #{env}"
        messages = "\n"

        #version = SgtnClient::Config.configurations[env]["version"]
        #if version.is_a? Integer
          #messages = messages +  "Configuration[version] has to be standard as '#.#.#, e.g '1.0.0'!\n"
        #end
        
        cache_expiry_period = SgtnClient::Config.configurations[env]["cache_expiry_period"]
        if cache_expiry_period != nil && (cache_expiry_period.is_a? Integer) == false
          messages = messages +  "Configuration[cache_expiry_period] has to be a number!\n"
        end
        
        disable_cache = SgtnClient::Config.configurations[env]["disable_cache"]
        if disable_cache != nil && disable_cache != false && disable_cache != true
          messages = messages +  "Configuration[disable_cache] has to be a 'true' or 'false'!\n"
        end
        
        if messages != "\n"
          raise SgtnClient::Exceptions::MissingConfig.new(messages)
        end
      end

  end

end
