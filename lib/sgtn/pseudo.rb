# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module Sgtn
  class PseudoTranslation < Translation # :nodoc:
    module Implementation
      def initialize
        @source_locale = SgtnClient::LocaleUtil.get_source_locale
        @prefix = Sgtn.pseudo_prefix || '@@'
        @suffix = Sgtn.pseudo_suffix || @prefix
      end

      def get_string!(key, component, _locale)
        translation, = super(key, component, @source_locale)
        "#{@prefix}#{translation}#{@suffix}"
      end

      def get_translations!(component, _locale = nil)
        translations = super(component, @source_locale)
        translations['messages'].transform_values! { |v| "#{@prefix}#{v}#{@suffix}" }
        translations
      end
    end

    include Implementation
  end
end
