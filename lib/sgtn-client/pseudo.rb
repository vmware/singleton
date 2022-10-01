# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Pseudo # :nodoc:
    def initialize
      @source_locale = LocaleUtil.get_source_locale
      @prefix = SgtnClient.config.pseudo_prefix || '@@'
      @suffix = SgtnClient.config.pseudo_suffix || @prefix
    end

    def get_translation!(key, component, _locale)
      translation, = super(key, component, @source_locale)
      "#{@prefix}#{translation}#{@suffix}"
    end

    def get_translations!(component, _locale = nil)
      translations = super(component, @source_locale)
      translations['messages'].transform_values! { |v| "#{@prefix}#{v}#{@suffix}" }
      translations
    end
  end
end
