# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Common
    class BundleID
      attr_reader :locale, :component

      def initialize(component, locale)
        @locale = locale
        @component = component
        @key = [@component, @locale].hash
      end

      def hash
        @key
      end

      def ==(other)
       (other.is_a? self.class) && @locale == other.locale && @component == other.component
      end

      alias eql? ==

      def to_s
        "{component: #{@component}, locale: #{@locale}}"
      end
    end
  end
end
