# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'time'

module SgtnClient::Core
        class Cache
            Entry = Struct.new(:expiry, :items)

            def self.get(key)
                SgtnClient.logger.debug "[Cache][get]get cache for key: #{key}"
                return data&.dig(key)
            end

            def self.put(key, items, ttl)
                # hours from new
                SgtnClient.logger.debug "[Cache][put]put cache for key '#{key}' with expired time at'" + (Time.now + ttl*60).to_s
                data[key] = Entry.new(Time.now + ttl*60, items)
            end

            def self.clear
                SgtnClient.logger.debug "[Cache][clear]clear cache!"
                @data = {}
            end

            def self.data
                @data ||= {}
            end
        end

end
