# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :SingleOperation, 'sgtn-client/common/single_operation'
  autoload :CacheUtil, 'sgtn-client/util/cache-util'
end
module SgtnClient::TranslationData::SingleLoader
  def load_bundle(component, locale)
    @single_loader ||= begin
      none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
      to_run = proc do |id|
        cache_item = SgtnClient::CacheUtil.get_cache(id)
        cache_item&.dig(:items).nil? || CacheUtil.is_expired(cache_item)
      end
      creator = proc do |id, _, c, l|
        Thread.new do
          cache_item = SgtnClient::CacheUtil.write_cache(id, super(c, l))
          # delete thread from hash after finish
          Thread.new { @single_loader.remove_object(id) }
          cache_item
        end
      end

      SingleOperation.new(none_alive, to_run, &creator)
    end

    id = SgtnClient::CacheUtil.get_cachekey(component, locale)
    @single_loader.operate(id, component, locale).value
  end
end
