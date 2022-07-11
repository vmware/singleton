# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class StringUtil < String
    def initialize(str, locale)
      super(str)
      @locale = locale
    end

    def localize(locale)
      super(@locale || locale)
    end
  end
end
