# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Common
    class BundleID
      attr_reader :locale, :component

      def initialize(component, locale)
        @locale = locale
        @component = component
      end

      def hash
        @key ||= [@component, @locale].hash
      end

      def ==(other)
        self.class === other && @locale == other.locale && @component == other.component
      end

      alias eql? ==

      def to_s
        "locale:#{@locale}, component:#{@component}}"
      end
    end

    class BundleData < Hash # :nodoc:
      attr_reader :locale, :component, :info

      def initialize(constructor = {}, **kwargs)
        if constructor.is_a?(Hash)
          super()
          update(constructor)
        else
          super(constructor)
        end
        @info = kwargs[:info]
      end
    end

    class SetData < Set # :nodoc:
      attr_accessor :info
      def initialize(*args, **kwargs)
        super(*args)
        @info = kwargs[:info]
      end
    end

    class DataInfo
      attr_reader :etag, :expiration
      attr_accessor :dirty

      def initialize(age: default_age, etag: nil)
        @expiration = Time.now + age * 60
        @etag = etag
      end

      def expired?
        (Time.now - @expiration).positive?
      end

      private

      def default_age
        @@default_age ||= begin
          env = Config.default_environment
          Config.configurations[env]['cache_expiry_period']
        end
      end
    end
  end
end
