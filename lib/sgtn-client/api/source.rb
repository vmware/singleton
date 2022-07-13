# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/loader/source'

module SgtnClient
  class Source
    def self.loadBundles(locale)
      SgtnClient.logger.debug "[Source][loadBundles]locale=#{locale}"
    end
  end
end
