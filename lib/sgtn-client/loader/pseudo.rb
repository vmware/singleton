# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader
    module Pseudo # :nodoc:
      def load_bundle(component, locale)
        return super unless locale == Sgtn::PSEUDO_LOCALE

        SgtnClient.logger.debug { "[#{__FILE__}][#{__callee__}] component=#{component}, locale=#{locale}" }
        
        source_bundle = super(component, LocaleUtil.get_source_locale)
        tag = source_bundle.origin.pseudo_tag
        source_bundle.transform_values! { |v| "#{tag}#{v}#{tag}"}
      end
    end
  end
end
