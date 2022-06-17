# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require_relative 'singleton-ruby'

module Sgtn # :nodoc:
  class << self
    extend Forwardable
    def_delegator SgtnClient, :load, :load_config
    def_delegators SgtnClient::Translation, :translate, :t, :get_translations, :locale, :locale=

    # Write methods which delegates to the configuration object
    %i[product_name version vip_server translation_bundle source_bundle cache_expiry_period mode].each do |method|
      instance_eval <<-DELEGATORS, __FILE__, __LINE__ + 1
          def #{method}
            SgtnClient.config.#{method}
          end

          def #{method}=(value)
            SgtnClient.config.#{method} = (value)
          end
      DELEGATORS
    end
  end

  I18nBackend = SgtnClient::I18nBackend
end
