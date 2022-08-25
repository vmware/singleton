# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module Pseudo # :nodoc:
    EN_LOCALE = :en

    def initialize
      @prefix = SgtnClient.config.pseudo_prefix || '@@'
      @suffix = SgtnClient.config.pseudo_suffix || @prefix
    end

    def translate!(key, component, _locale = nil, **kwargs, &block)
      translation = super(key, component, EN_LOCALE, **kwargs, &block)
      "#{@prefix}#{translation}#{@suffix}"
    end

    def get_translations!(component, _locale = nil)
      translations = super(component, EN_LOCALE)
      translations['messages'].transform_values! { |v| "#{@prefix}#{v}#{@suffix}" }
      translations
    end
  end
end
