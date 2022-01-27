require 'date'

module SgtnClient::Core
        class Cache
            Entry = Struct.new(:expiry, :value)

            def self.initialize(disabled=false, opts={})
                @@opts = opts
                @mutex = Mutex.new
                SgtnClient.logger.debug "Initialize cache......"
                if disabled == false
                    @@data = Hash.new
                    SgtnClient.logger.debug "Cache is enabled!"
                else
                    @@data = nil
                    SgtnClient.logger.debug "Cache is disabled!"
                end
            end

            def self.keys
                if @@data == nil
                    return nil
                end
                SgtnClient.logger.debug "Get cache keys"
                @@data.keys
            end

            def self.get(key)
                if @@data == nil
                    return nil, nil
                end
                SgtnClient.logger.debug "Get cache for key: " + key
                invalidate(key)
                #@@data[key][:value] if has(key)
            end

            def self.has(key)
                if @@data == nil
                    return nil
                end
                SgtnClient.logger.debug "Has cache for key: " + key
                @@data.has_key? key
            end

            def self.put(key, value, ttl=nil)
                @mutex.synchronize do
                    if @@data == nil
                        return nil
                    end
                    ttl ||= @@opts[:ttl]
                    # hours from new
                    SgtnClient.logger.debug "Put cache for key '" + key + "' with expired time at'" + (Time.now + ttl*60).to_s
                    @@data[key] = Entry.new(Time.now + ttl*60, value)
                end
            end

            def self.delete(key)
                @mutex.synchronize do
                    if @@data == nil
                        return nil
                    end
                    SgtnClient.logger.debug "Delete cache for key: " + key
                    @@data.delete key
                end
            end

            def self.clear
                @mutex.synchronize do
                    if @@data == nil
                        return nil
                    end
                    SgtnClient.logger.debug "Clear cache!"
                    @@data = Hash.new
                end
            end

            def self.invalidate(key)
                @mutex.synchronize do
                    if @@data == nil
                        return nil, nil
                    end
                    SgtnClient.logger.debug "Invalidating expired cache......"
                    now = Time.now
                    if has(key)
                        v = @@data[key]
                        expired = false
                        SgtnClient.logger.debug "Checking cache: key=#{key}, expiredtime=#{v[:expiry]}, now=#{now}, expired=#{(v[:expiry] < now)}"
                        if v[:expiry] < now
                            SgtnClient.logger.debug "Before deleting the cache: data=#{@@data}"
                            @@data.delete(key)
                            SgtnClient.logger.debug "After deleting the cache: data=#{@@data}"
                            expired = true
                        end
                        return expired, v[:value]
                    else
                        return nil, nil
                    end
                end
            end
        end

end