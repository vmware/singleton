# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

def wait_threads_finish
  Thread.list.each do |thread|
    thread.join(1) if thread.alive? && !thread.equal?(Thread.main)
  end
end

def expire_cache(id)
  cache_item = SgtnClient::CacheUtil.get_cache(id)
  cache_item[:expiry] = Time.now
end

class Thread
  class << self
    alias origin_new new
    def new(*args, &block)
      new_thread = origin_new(*args, &block)
    ensure
      new_thread[:parent] = Thread.current
      new_thread.name = if Thread.current != Thread.main
                          "#{Thread.current.name}.#{new_thread.name || new_thread.object_id}"
                        else
                          new_thread.name || new_thread.object_id.to_s
                        end
    end
  end
end
