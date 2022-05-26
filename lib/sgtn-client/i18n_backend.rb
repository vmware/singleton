# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'i18n'

module SgtnClient # :nodoc:
  class I18nBackend # :nodoc:
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

    def exists?(locale, key, options); end

    def translate(locale, key, options)
      flat_key = I18n::Backend::Flatten.normalize_flat_keys(locale, key, options[:scope], '.')
      values = options.except(*I18n::RESERVED_KEYS)
      SgtnClient::Translation.translate(flat_key, @component, locale, **values) { nil }
    end

    protected

    def init_translations; end

    def lookup(locale, key, scope = [], options); end
  end
end
