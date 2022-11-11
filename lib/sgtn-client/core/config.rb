# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'concurrent/map'
require 'observer'
require 'set'
require 'singleton'

module SgtnClient
  class Config # :nodoc:
    include Observable
    include Singleton

    attr_accessor :product_name, :version, :vip_server, :translation_bundle, :source_bundle, :cache_expiry_period,
                  :log_file, :log_level
    attr_writer :logger, :pseudo_tag

    def pseudo_tag
      @pseudo_tag ||= '@@'
    end

    def logger
      @logger ||= if log_file
                    puts "create log file: '#{log_file}', level: #{log_level}"
                    require 'logging'
                    logger = Logging.logger(log_file, 4, 1_048_576)
                    logger.level = log_level
                    logger
                  else
                    require 'logger'
                    Logger.new(STDOUT, level: log_level || Logger::INFO)
                  end
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

    def available_components
      bundles = available_bundles
      return Set.new if bundles.nil? || bundles.empty?

      define_bundles_methods(bundles) unless bundles.respond_to?(:components)
      bundles.components
    end

    def available_locales(component)
      bundles = available_bundles
      return Set.new if bundles.nil? || bundles.empty?

      define_bundles_methods(bundles) unless bundles.respond_to?(:locales)
      bundles.locales(component)
    end

    def update(options)
      options.each do |key, value|
        send("#{key}=", value)
      end
    end

    private

    def define_bundles_methods(bundles)
      bundles.instance_eval do |_|
        @component_locales ||= Concurrent::Map.new

        def components
          @components ||= reduce(Set.new) { |components, id| components << id.component }
        end

        def locales(component)
          @component_locales[component] ||= begin
            return Set.new unless components.include?(component)

            each_with_object(Set.new) { |id, locales| locales << id.locale if id.component == component }
          end
        end
      end

      changed
      notify_observers(:available_locales)
    end
  end
end
