# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'concurrent'

module SgtnClient # :nodoc:
  module Common # :nodoc:
    class ConcurrentHash < Hash # :nodoc:
      def initialize(*)
        @lock = Concurrent::ReadWriteLock.new
        super
      end

      def [](key)
        @lock.with_read_lock { super }
      end

      def []=(key, value)
        @lock.with_write_lock { super }
      end
    end
  end
end
