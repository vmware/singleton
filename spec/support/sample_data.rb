# Copyright 2025 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SampleData
  def samples
    @@samples ||= YAML.safe_load(File.read(File.expand_path("../../config/sample_data.yml", __FILE__)))
  end
end
