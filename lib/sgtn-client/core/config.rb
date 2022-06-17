# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'observer'
require 'yaml'

module SgtnClient
  class Config
    include Observable

    attr_accessor :product_name, :version, :vip_server, :translation_bundle, :source_bundle, :cache_expiry_period, :disable_cache, :mode

    @instance_mutex = Mutex.new

    private_class_method :new
    def initialize; end

    def self.instance
      @instance || @instance_mutex.synchronize { @instance ||= new }
    end

    # Set logger
    def logger=(logger)
      Logging.logger = logger
    end

    # Get logger
    def logger
      Logging.logger
    end

    def loader
      @loader ||= SgtnClient::TranslationLoader::LoaderFactory.create(self)
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
  end
end
