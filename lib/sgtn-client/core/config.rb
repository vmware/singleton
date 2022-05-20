# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'erb'
require 'yaml'
require 'observer'

module SgtnClient
  #include Exceptions

  module TranslationLoader
    autoload :LoaderFactory, 'sgtn-client/loader/loader_factory'
  end

  module Configuration

    def config
        @config ||= Config.config
    end

    def set_config(env, override_configurations = {})
      @config =
        case env
        when Config
          env
        when Hash
          begin
            config.dup.merge!(env)
          rescue Errno::ENOENT => error
            Config.new(env)
          end
        else
          Config.config(env, override_configurations)
        end
    end

    alias_method :config=, :set_config

  end
  

  class Config
    extend Observable

    attr_accessor :username, :password, :signature, :app_id, :cert_path,
    :token, :token_secret, :subject,
    :http_timeout, :http_proxy,
    :device_ipaddress, :sandbox_email_address,
    :mode, :endpoint, :merchant_endpoint, :platform_endpoint, :ipn_endpoint,
    :rest_endpoint, :rest_token_endpoint, :client_id, :client_secret,
    :openid_endpoint, :openid_redirect_uri, :openid_client_id, :openid_client_secret,
    :verbose_logging, :product_name, :version, :vip_server,
    :translation_bundle, :source_bundle, :cache_expiry_period, :disable_cache, :default_language


    # Create Config object
    # === Options(Hash)
    # * <tt>username</tt>   -- Username
    # * <tt>password</tt>   -- Password
    # * <tt>signature</tt> (Optional if certificate present) -- Signature
    # * <tt>app_id</tt>     -- Application ID
    # * <tt>cert_path</tt> (Optional if signature present)  -- Certificate file path
    def initialize(options)
      merge!(options)
    end

    # Override configurations
    def merge!(options)
      options.each do |key, value|
        send("#{key}=", value)
      end
      self
    end
    
    class << self

        @@config_cache = {}
        def load(file_name, default_env = default_environment)
          @@config_cache        = {}
          @@configurations      = read_configurations(file_name)
          @@default_environment = default_env
          config
        end


        # Get default environment name
        def default_environment
          @@default_environment ||= ENV['SGTN_ENV'] || ENV['RACK_ENV'] || ENV['RAILS_ENV'] || "development"
        end


        # Set default environment
        def default_environment=(env)
          @@default_environment = env.to_s
        end

        def configure(options = {}, &block)
          begin
            self.config.merge!(options)
          rescue Errno::ENOENT
            self.configurations = { default_environment => options }
          end
          block.call(self.config) if block
          self.config
        end
        alias_method :set_config, :configure

        # Create or Load Config object based on given environment and configurations.
        # === Attributes
        # * <tt>env</tt> (Optional) -- Environment name
        # * <tt>override_configuration</tt> (Optional) -- Override the configuration given in file.
        # === Example
        #   Config.config
        #   Config.config(:development)
        #   Config.config(:development, { :app_id => "XYZ" })
        def config(env = default_environment, override_configuration = {})
          if env.is_a? Hash
            override_configuration = env
            env = default_environment
          end
          if override_configuration.nil? or override_configuration.empty?
            default_config(env)
          else
            default_config(env).dup.merge!(override_configuration)
          end
        end
  
        def default_config(env = nil)
          env = (env || default_environment).to_s
          if configurations[env]
            @@config_cache[env] ||= new(configurations[env])
          else
            raise SgtnClient::Exceptions::MissingConfig.new("Configuration[#{env}] NotFound")
          end
        end
        
        # Get raw configurations in Hash format.
        def configurations
          @@configurations ||= read_configurations
        end
  
        # Set configuration
        def configurations=(configs)
          @@config_cache   = {}
          @@configurations = configs && Hash[configs.map{|k,v| [k.to_s, v] }]
        end

        # Set logger
        def logger=(logger)
          Logging.logger = logger
        end

        # Get logger
        def logger
          if @@configurations[:mode] == 'live' and Logging.logger.level == Logger::DEBUG
            Logging.logger.warn "DEBUG log level not allowed in live mode for security of confidential information. Changing log level to INFO..."
            Logging.logger.level = Logger::INFO
          end
          Logging.logger
        end


        def loader
          @loader ||= begin
            config = SgtnClient::Config.configurations[SgtnClient::Config.default_environment]
            SgtnClient::TranslationLoader::LoaderFactory.create(config)
          end
        end

        def available_bundles
          loader.available_bundles
        rescue StandardError => e
          SgtnClient.logger.error 'failed to get available bundles'
          SgtnClient.logger.error e
          Set.new
        end

        def available_locales
          bundles = available_bundles
          return Set.new if bundles.nil? || bundles.empty?

          unless bundles.respond_to?(:locales)
            def bundles.locales
              @locales ||= reduce(Set.new) { |locales, id| locales << id.locale }
            end
            changed
            notify_observers(:available_locales)
          end
          bundles.locales
        end

        private
        # Read configurations from the given file name
        # === Arguments
        # * <tt>file_name</tt> (Optional) -- Configuration file path
        def read_configurations(file_name = "config/sgtnclient.yml")
          erb = ERB.new(File.read(file_name))
          erb.filename = file_name
          YAML.load(erb.result)
        end       

    end
  end

end
