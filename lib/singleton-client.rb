# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'forwardable'
require_relative 'singleton-ruby'

module Sgtn # :nodoc:
  class << self
    extend Forwardable
    def_delegator SgtnClient, :load, :load_config
    def_delegators SgtnClient::Translation, :translate, :t, :get_translations, :locale, :locale=
  end

  I18nBackend = SgtnClient::I18nBackend
end
