# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'i18n'

module Sgtn # :nodoc:
  # When integrating Singleton in a client application that is already using [I18n::Backend (https://www.rubydoc.info/github/svenfuchs/i18n/master/I18n/Backend/), it would be useful to have Singleton override the said module in order to minimize necessary changes. Here is a common usage:
  #
  #   I18n::Backend::Simple.include(I18n::Backend::Fallbacks) # add fallbacks behavior to current backend
  #   I18n.backend = I18n::Backend::Chain.new(Sgtn::I18nBackend.new(component_name), I18n.backend)
  #   I18n.enforce_available_locales=false # disable available locales check
  #   I18n.default_locale = :en
  class I18nBackend
    def initialize(component)
      @component = component
      @translation = Class.new do
        include SgtnClient::Translation::Implementation
        include Sgtn::Pseudo
      end.new
    end

    def initialized?
      @initialized ||= true
    end

    def load_translations(*) end

    def store_translations(*) end

    def available_locales
      SgtnClient.config.available_locales(@component).to_a
    end

    def reload!; end

    def eager_load!; end

    def translations; end

    def exists?(locale, key, options)
      !!translate(locale, key, options)
    end

    def translate(locale, key, options)
      flat_key = I18n::Backend::Flatten.normalize_flat_keys(locale, key, options[:scope], '.')
      values = options.except(*I18n::RESERVED_KEYS)
      @translation.translate(flat_key, @component, locale, **values) { nil }
    end

    def localize(locale, object, format, options) end
  end
end
