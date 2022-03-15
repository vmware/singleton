# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'date'

module SgtnClient::Core
        class Cache
            Entry = Struct.new(:expiry, :items, :fallback_keys, :fallback_locale)

            def self.initialize(disabled=false, opts={})
                @@opts = opts
                @mutex = Mutex.new
                SgtnClient.logger.debug "[Cache][initialize] Disable cache? #{disabled}"
                @@data = disabled ? nil : Hash.new 
            end

            def self.get(key)
                SgtnClient.logger.debug "[Cache][get]get cache for key: " + key
                return @@data&.dig(key)
            end

            def self.put(key, items, ttl=nil, fallback_keys=nil, fallback_locale=nil)
                if @@data == nil || items == nil
                    return nil
                end
                ttl ||= @@opts[:ttl]
                # hours from new
                SgtnClient.logger.debug "[Cache][put]put cache for key '" + key + "' with expired time at'" + (Time.now + ttl*60).to_s
                @@data[key] = Entry.new(Time.now + ttl*60, items, fallback_keys, fallback_locale)
            end

            def self.clear
                SgtnClient.logger.debug "[Cache][clear]clear cache!"
                @@data = Hash.new
            end
        end

end
