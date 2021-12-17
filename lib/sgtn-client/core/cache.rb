require 'date'

module SgtnClient::Core
        class Cache
            Entry = Struct.new(:expiry, :value)

            def self.initialize(disabled=false, opts={})
                $opts = opts
                @mutex = Mutex.new
                SgtnClient.logger.debug "Initialize cache......"
                if disabled == false
                    $data = Hash.new
                    SgtnClient.logger.debug "Cache is enabled!"
                else
                    SgtnClient.logger.debug "Cache is disabled!"
                end
            end

            def self.keys
                if $data == nil
                    return nil
                end
                SgtnClient.logger.debug "Get cache keys"
                $data.keys
            end

            def self.get(key)
                if $data == nil
                    return nil
                end
                SgtnClient.logger.debug "Get cache for key: " + key
                invalidate
                $data[key][:value] if has(key)
            end

            def self.has(key)
                if $data == nil
                    return nil
                end
                SgtnClient.logger.debug "Has cache for key: " + key
                $data.has_key? key
            end

            def self.put(key, value, ttl=nil)
                @mutex.synchronize do
                    if $data == nil
                        return nil
                    end
                    ttl ||= @opts[:ttl]
                    # hours from new
                    SgtnClient.logger.debug "Put cache for key '" + key + "' with expired time at'" + (Time.now + ttl*60).to_s
                    $data[key] = Entry.new(Time.now + ttl*60, value)
                end
            end

            def self.delete(key)
                @mutex.synchronize do
                    if $data == nil
                        return nil
                    end
                    SgtnClient.logger.debug "Delete cache for key: " + key
                    $data.delete key
                end
            end

            def self.clear
                @mutex.synchronize do
                    if $data == nil
                        return nil
                    end
                    SgtnClient.logger.debug "Clear cache!"
                    $data = Hash.new
                end
            end

            def self.invalidate
                @mutex.synchronize do
                    if $data == nil
                        return nil
                    end
                    SgtnClient.logger.debug "Invalidating expired cache......"
                    now = Time.now
                    $data.each {
                        |k, v|
                            SgtnClient.logger.debug "Checking cache: key=#{k}, expiredtime=#{v[:expiry]}, now=#{now}, expired=#{(v[:expiry] < now)}"
                        }
                    $data.delete_if {|k, v| v[:expiry] < now}
                end
            end
        end

end