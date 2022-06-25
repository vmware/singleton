#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

RSpec.describe 'Available Bundles - loader', :include_helpers, :extend_helpers do
  subject { SgtnClient::TranslationLoader::LoaderFactory.create(@config) }
  include_examples 'Available Bundles' do
    include_context 'reset client'
  end
end
