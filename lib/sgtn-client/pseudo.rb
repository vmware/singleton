# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class PseudoTranslation # :nodoc:
    include Translation::Implementation

    PREFIX = '@@'
    SUFFIX = PREFIX
    EN_LOCALE = :en

    def translate(key, component, _locale = nil, **kwargs, &block)
      translation = super(key, component, EN_LOCALE, **kwargs, &block)
      "#{PREFIX}#{translation}#{SUFFIX}"
    end
    alias t translate

    def get_translations(component, _locale = nil)
      translations = super(component, EN_LOCALE)
      translations['messages'].transform_values! { |v| "#{PREFIX}#{v}#{SUFFIX}" }
      translations
    end
  end
end
