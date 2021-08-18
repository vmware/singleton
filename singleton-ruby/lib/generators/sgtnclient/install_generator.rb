module SgtnClient
    module Generators
      class InstallGenerator < Rails::Generators::Base
        source_root File.expand_path('../templates', __FILE__)

        def copy_config_file
          copy_file "sgtnclient.yml", "config/sgtnclient.yml"
        end

        def copy_initializer_file
          copy_file "sgtnclient.rb",  "config/initializers/sgtnclient.rb"
        end
      end
    end
end
