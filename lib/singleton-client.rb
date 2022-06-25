# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require_relative 'singleton-ruby'

module Sgtn # :nodoc:
  class << self
    extend Forwardable
    def_delegator SgtnClient, :load, :load_config
    delegate %i[translate t get_translations locale locale=] => SgtnClient::Translation,
             %i[product_name version vip_server translation_bundle source_bundle cache_expiry_period log_file log_level].flat_map { |m|
               [m, "#{m}=".to_sym]
             } => SgtnClient::Config.instance
  end

  I18nBackend = SgtnClient::I18nBackend
end
