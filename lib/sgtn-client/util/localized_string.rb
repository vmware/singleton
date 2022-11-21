# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class LocalizedString < String
    def initialize(value, locale)
      super(value)
      @locale = locale
    end

    def localize(locale)
      super(@locale || locale)
    end
  end
end
