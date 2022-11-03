# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class LocalizedString < String
    def initialize(value, locale, from, to)
      if to.locale == Sgtn::PSEUDO_LOCALE
        super("#{from.origin.pseudo_tag}#{value}#{from.origin.pseudo_tag}")
      else
        super(value)
      end

      @locale = locale
    end

    def localize(locale)
      super(@locale || locale)
    end
  end
end
