# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require_relative 'singleton-ruby'

module Sgtn # :nodoc:
  class << self
    extend Forwardable

    # load configurations from a file
    def load_config(config_file, env)
      SgtnClient.load(config_file, env)
    end

    def with_locale(tmp_locale = nil)
      if tmp_locale.nil?
        yield
      else
        current_locale = locale
        self.locale = tmp_locale
        begin
          yield
        ensure
          self.locale = current_locale
        end
      end
    end

    private

    def translation
      @translation ||= begin
        Class.new do
          include SgtnClient::Translation::Implementation

          def get_translations(component, locale = nil)
            get_translations!(component, locale)
          rescue StandardError => e
            Sgtn.logger.error "[#{method(__callee__).owner}.#{__callee__}] {#{component}, #{locale}}. #{e}"
            nil
          end

          def translate(key, component, locale = nil, **kwargs, &block)
            translate!(key, component, locale, **kwargs, &block)
          rescue StandardError => e
            Sgtn.logger.debug { "[#{method(__callee__).owner}.#{__callee__}] {#{key}, #{component}, #{locale}}. #{e}" }
            key
          end
          alias_method :t, :translate
          alias_method :t!, :translate!
        end.new
      end
    end

    def_delegator SgtnClient::Config, :instance, :config
    delegate %i[translate! t! translate t get_translations! get_translations] => :translation,
             %i[locale locale=] => SgtnClient,
             %i[logger product_name version vip_server translation_bundle
                source_bundle cache_expiry_period log_file log_level
                pseudo_mode pseudo_prefix pseudo_suffix ].flat_map { |m|
               [m, "#{m}=".to_sym]
             } => :config
  end

  I18nBackend = SgtnClient::I18nBackend
end
