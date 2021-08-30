
module SgtnClient

  class ValidateUtil

      def self.validate_config()
        SgtnClient.logger.debug "-----------Start to validate configuration's setting itmes-----------"
        env = SgtnClient::Config.default_environment
        messages = "\n"

        mode = SgtnClient::Config.configurations[env]["mode"]
        if mode != 'sandbox' && mode != 'live'
          messages =  messages + "Configuration[mode] has to be 'sandbox' or 'live'!\n"
        end
        
        bundle_mode = SgtnClient::Config.configurations[env]["bundle_mode"]
        if bundle_mode != 'offline' && bundle_mode != 'online'
          messages = messages +  "Configuration[bundle_mode] has to be 'offline' or 'online'!\n"
        end
        
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
        SgtnClient.logger.debug "-----------End  to  validate configuration's setting itmes-----------"
      end

  end

end