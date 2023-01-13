# Copyright 2022-2023 VMware, Inc.
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

        data = super(component, LocaleUtil.get_source_locale)
        data.each { |k, v| data[k] = "#{@pseudo_tag}#{v}#{@pseudo_tag}" }
      end
    end
  end
end
