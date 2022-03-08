require 'date'

module SgtnClient::Core
        class Cache
            Entry = Struct.new(:expiry, :value)

            def self.initialize(disabled=false, opts={})
                @@opts = opts
                @mutex = Mutex.new
                if disabled == false
                    @@data = Hash.new
                    SgtnClient.logger.debug "[Cache][initialize]cache is enabled!"
                else
                    @@data = nil
                    SgtnClient.logger.debug "[Cache][initialize]cache is disabled!"
                end
            end

            def self.keys
                if @@data == nil
                    return nil
                end
                SgtnClient.logger.debug "[Cache][keys]get cache keys"
                @@data.keys
            end

            def self.get(key)
                if @@data == nil
                    return nil, nil
                end
                SgtnClient.logger.debug "[Cache][get]get cache for key: " + key
                invalidate(key)
            end

            def self.has(key)
                if @@data == nil
                    return nil
                end
                SgtnClient.logger.debug "[Cache][has]check if the cache has key: #{(@@data.has_key? key)}"
                @@data.has_key? key
            end

            def self.put(key, value, ttl=nil)
                @mutex.synchronize do
                    if @@data == nil || value == nil
                        return nil
                    end
                    ttl ||= @@opts[:ttl]
                    # hours from new
                    SgtnClient.logger.debug "[Cache][put]put cache for key '" + key + "' with expired time at'" + (Time.now + ttl*60).to_s
                    @@data[key] = Entry.new(Time.now + ttl*60, value)
                end
            end

            def self.delete(key)
                @mutex.synchronize do
                    if @@data == nil
                        return nil
                    end
                    SgtnClient.logger.debug "[Cache][delete]delete cache for key: " + key
                    @@data.delete key
                end
            end

            def self.clear
                @mutex.synchronize do
                    if @@data == nil
                        return nil
                    end
                    SgtnClient.logger.debug "[Cache][clear]clear cache!"
                    @@data = Hash.new
                end
            end

            def self.invalidate(key)
                @mutex.synchronize do
                    if @@data == nil
                        return nil, nil
                    end
                    SgtnClient.logger.debug "[Cache][invalidate]invalidate expired cache......"
                    now = Time.now
                    if has(key)
                        v = @@data[key]
                        expired = v[:expiry] < now
                        SgtnClient.logger.debug "[Cache][invalidate]check cache: key=#{key}, expiredtime=#{v[:expiry]}, now=#{now}, expired=#{(v[:expiry] < now)}"
                        return expired, v[:value]
                    else
                        return nil, nil
                    end
                end
            end
        end

end
