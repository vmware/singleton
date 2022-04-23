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
