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

      def get_string!(key, component, locale)
        if Sgtn.pseudo_mode
          # source is always available, so actual_locale is same as @source_locale
          translation, actual_locale = super(key, component, @source_locale)
          ["#{@prefix}#{translation}#{@suffix}", actual_locale]
        else
          super(key, component, locale)
        end
      end

      def get_translations!(component, locale = nil)
        if Sgtn.pseudo_mode
          translations = super(component, @source_locale)
          translations['messages'].transform_values! { |v| "#{@prefix}#{v}#{@suffix}" }
          translations
        else
          super(component, locale)
        end
      end
    end

    include Implementation
  end
end
