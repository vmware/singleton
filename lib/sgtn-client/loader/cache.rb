# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'
  autoload :SingleOperation, 'sgtn-client/common/single_operation'
end
module SgtnClient::TranslationLoader::Cache
  def load_bundle(component, locale)
    cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
    SgtnClient.logger.debug "[#{self.to_s}][#{__FILE__}][#{__method__}] cache_key=#{cache_key}"
    cache_item = SgtnClient::CacheUtil.get_cache(cache_key)
    if cache_item.nil?
      # refresh synchronously if not in cache
      SgtnClient.logger.debug "[#{self.to_s}][#{__FILE__}][#{__method__}] Cache miss. cache_key=#{cache_key}"
      cache_item = (single_loader { |c, l| super(c, l) }).operate(cache_key, component, locale).value 
      # TODO: if an error occurs when requesting a bundle, need to avoid more requests
    elsif SgtnClient::CacheUtil.is_expired(cache_item) && locale != SgtnClient::LocaleUtil.get_source_locale # local source never expires.
      SgtnClient.logger.debug "[#{self.to_s}][#{__FILE__}][#{__method__}] Bundle cache is expired. cache_key=#{cache_key}"
      @single_loader.operate(cache_key, component, locale) # refresh in background
    end
    cache_item
  end

  private

  def single_loader
    @single_loader ||= begin
      none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
      to_run = proc do |id|
        cache_item = SgtnClient::CacheUtil.get_cache(id)
        cache_item&.dig(:items).nil? || SgtnClient::CacheUtil.is_expired(cache_item)
      end
      creator = proc do |id, _, c, l|
        Thread.new do
          SgtnClient.logger.debug "Refreshing cache for #{c}/#{l}"
          cache_item = SgtnClient::CacheUtil.write_cache(id, yield(c, l))
          # delete thread from hash after finish
          Thread.new { @single_loader.remove_object(id) }
          cache_item
        end
      end

      SgtnClient::SingleOperation.new(none_alive, to_run, &creator)
    end
  end
end
