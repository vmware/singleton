# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module Pseudo # :nodoc:
      def initialize(*)
        super
        @pseudo_tag = Sgtn.pseudo_tag
      end

      def load_bundle(component, locale)
        return super unless locale == Sgtn::PSEUDO_LOCALE

        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}" }

        super(component, LocaleUtil.get_source_locale).transform_values! { |v| "#{@pseudo_tag}#{v}#{@pseudo_tag}" }
      end
    end
  end
end
