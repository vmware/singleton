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

    private

    def translation
      @translation ||= begin
        if config.pseudo_mode  
          Translation.include Pseudo
        end
        Translation.new
      end
    end

    def_delegator SgtnClient::Config, :instance, :config
    delegate %i[translate t get_translations] => :translation,
             %i[locale locale=] => SgtnClient,
             %i[logger product_name version vip_server translation_bundle
                source_bundle cache_expiry_period log_file log_level].flat_map { |m|
               [m, "#{m}=".to_sym]
             } => :config

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
  end

  I18nBackend = SgtnClient::I18nBackend

  class Translation
    include SgtnClient::Translation::Implementation
  end

  module Pseudo
    PREFIX = '@@'
    SUFFIX = PREFIX
    EN_LOCALE = :en

    def translate(key, component, locale = nil, **kwargs, &block)
      translation = super(key, component, EN_LOCALE, **kwargs, &block)
      "#{PREFIX}#{translation}#{SUFFIX}"
    end
    alias t translate

    def get_translations(component, locale = nil)
      translations = super(component, EN_LOCALE)
      translations["messages"].transform_values! { |v| "#{PREFIX}#{v}#{SUFFIX}" }
      translations
    end
  end
end
