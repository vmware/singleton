# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class SingleOperation
    def initialize(*conditions, &block)
      raise 'no way to create a new obj' unless block

      @lock = Mutex.new
      @hash = {}

      @conditions = conditions
      @creator = block
    end

    # return new created object
    def single_operation(id, *args)
      @lock.synchronize do
        obj = @hash[id]
        @conditions.each do |con|
          return obj unless con.call(id, obj, *args)
        end
        @hash[@id] = @creator.call(id, obj, *args)
      end
    end
  end
end
