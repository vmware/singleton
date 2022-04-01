# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'sgtn-client/loader/source'

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  class Source
    def self.loadBundles(locale)
      SgtnClient.logger.debug "[Source][loadBundles]locale=#{locale}"
      SgtnClient::Config.configurations.default = locale
    end
  end
end
