# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient::TranslationLoader
  autoload :Source , 'sgtn-client/loader/source'
  autoload :SgtnServer , 'sgtn-client/loader/server'
  autoload :LocalTranslation , 'sgtn-client/loader/local_translation'
  autoload :Chain, 'sgtn-client/loader/chain_loader'
  autoload :SourceComparer, 'sgtn-client/loader/source_comparer'
  autoload :SingleLoader, 'sgtn-client/loader/single_loader'
  autoload :Cache, 'sgtn-client/loader/cache'


  module LoaderFactory
    def self.create(config)
      loaders = []
      loaders << Source.new if config['source_bundle']
      loaders << SgtnServer.new if config['vip_server']
      loaders << LocalTranslation.new if config['translation_bundle']
      raise SingletonError, 'No translation is available!' if loaders.empty?

      chain_loader = Class.new(Chain)
      chain_loader.include SourceComparer if config['source_bundle']
      chain_loader.include SingleLoader
      chain_loader.include Cache

      chain_loader.new(*loaders)
    end
  end
end
