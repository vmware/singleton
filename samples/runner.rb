# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

# Run Samples on different scope

module RunSample
  def self.run(file, variable)
    object_binding = binding
    object_binding.eval(File.read("./#{file}"))
    object_binding.eval(variable)
  end
end
