# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

def wait_threads_finish
  Thread.list.each do |thread|
    thread.join(1) if thread.alive? && !thread.equal?(Thread.main)
  rescue
  end
end

def expire_cache(id)
  cache_item = SgtnClient::CacheUtil.get_cache(id)
  cache_item[:expiry] = Time.now
end

def extract_arguments(trace)
  param_names = trace.parameters.map(&:last)
  param_names.map { |n| [n, trace.binding.eval(n.to_s)] }.to_h
end

def extract_locals(trace)
  local_names = trace.binding.local_variables
  local_names.map { |n|
    [n, trace.binding.local_variable_get(n)]
  }.to_h
end

def traverse_modules(m, &block)
  m.constants(false).each do |c|
    const = m.const_get(c, false)
    if [Class, Module].include? const.class
      SgtnClient.logger.info "Enable trace on const #{const}"
      const.methods(false).each { |method| block.call(const.method(method)) }
      const.instance_methods(false).each { |method| block.call(const.instance_method(method)) }
      # const.singleton_methods(false).each { |method| block.call(const.singleton_method(method)) }
      
      traverse_modules(const, &block)
    end
  end
end
