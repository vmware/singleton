# frozen_string_literal: true

# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'active_support'

module Helpers
  mattr_accessor :singleton_server, :server_url, :component, :locale, :en_locale

  self.singleton_server = 'https://localhost:8090'
  self.server_url = File.join(singleton_server, '/i18n/api/v2/translation/products',
                              'test', 'versions', '4.8.1')
  self.component = 'JAVA'
  self.locale = 'zh-Hans'
  self.en_locale = 'en'

  def nonexistent_response
    File.new('spec/fixtures/mock_responses/nonexistent').read
  end

  def stubs
    []
  end
end
