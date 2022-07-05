# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient

  class ValidateUtil

      def self.validate_config()
        puts "validating config..."
        messages = ""

        version = SgtnClient.config.version
        SgtnClient.config.version = version.to_s
        if version.to_s !~ /\A(\d+\.)*\d+\z/
          messages = messages +  "Configuration[version] has to be standard as '#.#.#, e.g '1.0.0'!\n"
        end
        
        cache_expiry_period = SgtnClient.config.cache_expiry_period
        if cache_expiry_period != nil && (cache_expiry_period.is_a? Integer) == false
          messages = messages +  "Configuration[cache_expiry_period] has to be a number!\n"
        end

        if messages != ""
          raise Exceptions::MissingConfig.new(messages)
        end
      end

  end

end
