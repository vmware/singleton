# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'active_support/ordered_hash'

module SgtnClient # :nodoc:
  module Common # :nodoc:
    class OrderedSet < Set
      def initialize(enum = nil, &block)
        @hash = ActiveSupport::OrderedHash.new
        super
      end
    end
  end
end
