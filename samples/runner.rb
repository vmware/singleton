# Run Samples on different scope
#require 'sgtn-client'

#require 'singleton-ruby'

#include SgtnClient

module RunSample
  def self.logger
    SgtnClient.logger
  end

  def self.run(file, variable)
    object_binding = binding
    object_binding.eval(File.read("./#{file}"))
    object_binding.eval(variable)
  end
end
