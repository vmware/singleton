# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :SingleOperation, 'sgtn-client/common/single_operation'
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module TranslationLoader
    module SingleLoader

      def load_bundle(component, locale)
        @single_loader ||= begin
          none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
          creator = proc do |id, _, *args|
            Thread.new do
              SgtnClient.logger.debug "Refreshing cache for #{id}"
              item = super(*args)

              # delete thread from hash after finish
              Thread.new { @single_loader.remove_object(id) }
              item
            rescue StandardError => e
              SgtnClient.logger.error "Error while loading: '#{id}', args: #{args}. error: #{e.message}"
              SgtnClient.logger.error e.backtrace
              nil
            end
          end

          SgtnClient::SingleOperation.new(none_alive, &creator)
        end

        @single_loader.operate(SgtnClient::CacheUtil.get_cachekey(component, locale), component, locale)&.value
      end
    end
  end
end
