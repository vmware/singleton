# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'i18n'

module SgtnClient # :nodoc:
  # Sometimes it is needed to use Singleton as an I18n backend to minimize the changes when integrating Singleton with
  # your application. Here is the common usage:
  #
  #   I18n::Backend::Simple.include(I18n::Backend::Fallbacks) # add fallbacks behavior to current backend
  #   I18n.backend = I18n::Backend::Chain.new(Sgtn::I18nBackend.new(component_name), I18n.backend)
  #   I18n.enforce_available_locales=false # disable available locales check
  #   I18n.default_locale = :en
  class I18nBackend
    def initialize(component)
      @component = component
    end

    def initialized?
      @initialized = true
    end

    def load_translations(*) end

    def store_translations(*) end

    def available_locales
      SgtnClient::Config.available_locales.to_a
    end

    def reload!; end

    def eager_load!; end

    def translations; end

    def exists?(locale, key)
      !!(translate(locale, key) { nil })
    end

    def translate(locale, key, options)
      flat_key = I18n::Backend::Flatten.normalize_flat_keys(locale, key, options[:scope], '.')
      values = options.except(*I18n::RESERVED_KEYS)
      SgtnClient::Translation.translate(flat_key, @component, locale, **values) { nil }
    end
  end
end
