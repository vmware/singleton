# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'set'
require 'time'

module SgtnClient
  module Common
    class BundleID # :nodoc:
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

    module DataInfo # :nodoc:
      attr_accessor :last_update

      def initialize(*)
        @last_update = Time.now
        super
      end

      def expired?
        Time.now >= @last_update + DataInfo.age
      end

      def self.age
        @age ||= (SgtnClient.config.cache_expiry_period || 24 * 60) * 60
      end
    end

    class BundleData < Hash # :nodoc:
      include DataInfo

      attr_reader :origin, :locale, :component

      def initialize(*args, origin: nil, locale: nil, component: nil)
        if !args.empty? && args[0].is_a?(Hash)
          update(args[0])
          super()
        else
          super
        end

        @origin = origin
        @locale = locale
        @component = component
      end
    end

    class SetData < Set # :nodoc:
      include DataInfo
    end
  end
end
