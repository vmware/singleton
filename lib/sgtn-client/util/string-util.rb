# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  class StringUtil < String
    def initialize(str, locale)
      super(str)
      @locale = locale
    end
    attr_accessor :locale
  end
end 