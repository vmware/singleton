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
      to_run = proc do |cache_key|
        cache_item = SgtnClient::CacheUtil.get_cache(cache_key)
        cache_item&.dig(:items).nil? || CacheUtil.is_expired(cache_item)
      end

      SingleOperation.new(none_alive, to_run) do |cache_key, _, component, locale|
        Thread.new do
          cache_item = SgtnClient::CacheUtil.write_cache(cache_key, super(component, locale))
          # delete thread from hash after finish
          Thread.new { @single_loader.remove_object(cache_key) }
          cache_item
        end
      end
    end

    cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
    @single_loader.operate(cache_key, component, locale).value
  end
end
