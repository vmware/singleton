# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :SingleOperation, 'sgtn-client/common/single_operation'
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module TranslationLoader
    module SingleLoader
      def load_bundle(component, locale)
        # require 'pry-byebug'
        # require 'pry-inline'
        # binding.pry
        SgtnClient.logger.debug "[#{__FILE__}.#{__callee__}] component=#{component}, locale=#{locale}"
        SgtnClient.logger.debug self.class
        # SgtnClient.logger.debug caller
        @single_loader ||= begin
          none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
          creator = proc do |id, _, *args|
            Thread.new do
              SgtnClient.logger.debug "start single loading #{id}"
              item = super(*args)

              # delete thread from hash after finish
              Thread.new { @single_loader.remove_object(id) }
              item
            end
          end

          SgtnClient::SingleOperation.new(none_alive, &creator)
        end

        thread = @single_loader.operate(SgtnClient::CacheUtil.get_cachekey(component, locale), component, locale)
        thread&.value
      end
    end
  end
end
