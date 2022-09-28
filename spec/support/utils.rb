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
  get_cache(id)&.last_update = Time.at(0)
end

def clear_cache(loader = Sgtn.config.instance_variable_get(:@loader))
  loader&.instance_variable_get(:@cache_hash)&.clear
end

def get_cache(key, loader = Sgtn.config.instance_variable_get(:@loader))
  loader&.instance_variable_get(:@cache_hash)&.fetch(key, nil)
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
      Sgtn.logger.info "Enable trace on const #{const}"
      const.methods(false).each { |method| block.call(const.method(method)) }
      const.instance_methods(false).each { |method| block.call(const.instance_method(method)) }
      # const.singleton_methods(false).each { |method| block.call(const.singleton_method(method)) }
      
      traverse_modules(const, &block)
    end
  end
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
