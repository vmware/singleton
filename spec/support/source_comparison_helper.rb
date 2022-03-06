# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#
module SourceComparisonHelpers
  def load_bundle(bundle_path)
    File.open(bundle_path) do |file|
      sources = YAML::load(file)
    end
  end
end