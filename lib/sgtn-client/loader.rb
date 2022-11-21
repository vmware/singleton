# frozen_string_literal: true

#  Copyright 2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0

module SgtnClient
  module TranslationLoader # :nodoc:
    autoload :Cache, 'sgtn-client/loader/cache'
    autoload :CacheFiller, 'sgtn-client/loader/cache'
    autoload :Chain, 'sgtn-client/loader/chain_loader'
    autoload :CONSTS, 'sgtn-client/loader/consts'
    autoload :LoaderFactory, 'sgtn-client/loader/loader_factory'
    autoload :LocalTranslation, 'sgtn-client/loader/local_translation'
    autoload :Pseudo, 'sgtn-client/loader/pseudo'
    autoload :SgtnServer, 'sgtn-client/loader/server'
    autoload :SingleLoader, 'sgtn-client/loader/single_loader'
    autoload :SourceComparer, 'sgtn-client/loader/source_comparer'
    autoload :Source, 'sgtn-client/loader/source'
  end
end
