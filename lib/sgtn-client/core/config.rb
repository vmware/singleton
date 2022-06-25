# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'observer'
require 'set'
require 'singleton'

module SgtnClient
  class Config
    include Observable
    include Singleton

    attr_accessor :product_name, :version, :vip_server, :translation_bundle, :source_bundle, :cache_expiry_period, :disable_cache, :mode

    # Set logger
    def logger=(logger)
      Logging.logger = logger
    end

    # Get logger
    def logger
      if (mode == 'live') && (Logging.logger.level == Logger::DEBUG)
        Logging.logger.warn 'DEBUG log level not allowed in live mode for security of confidential information. Changing log level to INFO...'
        Logging.logger.level = Logger::INFO
      end
      Logging.logger
    end

    def loader
      @loader ||= TranslationLoader::LoaderFactory.create(self)
    end

    def available_bundles
      loader.available_bundles
    rescue StandardError => e
      SgtnClient.logger.error 'failed to get available bundles'
      SgtnClient.logger.error e
      Set.new
    end

    def available_locales
      bundles = available_bundles
      return Set.new if bundles.nil? || bundles.empty?

      unless bundles.respond_to?(:locales)
        def bundles.locales
          @locales ||= reduce(Set.new) { |locales, id| locales << id.locale }
        end
        changed
        notify_observers(:available_locales)
      end
      bundles.locales
    end

    def update(options)
      options.each do |key, value|
        send("#{key}=", value)
      end
    end
  end
end
