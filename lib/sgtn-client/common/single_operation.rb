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
    def operate(id, *args)
      @lock.synchronize do
        obj = @hash[id]
        @conditions.each do |con|
          return obj unless con.call(id, obj, *args)
        end
        # TODO: whatif returning nil
        @hash[id] = @creator.call(id, obj, *args)
      end
    end

    def remove_object(id)
      @lock.synchronize do
        @hash.delete(id)
      end
    end
  end
end
