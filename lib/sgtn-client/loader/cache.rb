# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :CacheUtil, 'sgtn-client/util/cache-util'
end
module SgtnClient::TranslationLoader::Cache
  def load_bundle(component, locale)
    cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
    SgtnClient.logger.debug "[Translation][get_cs]cache_key=#{cache_key}"
    cache_item = SgtnClient::CacheUtil.get_cache(cache_key)
    if cache_item.nil?
      cache_item = super # refresh synchronously if not in cache
      # TODO: if an error occurs when requesting a bundle, need to avoid more requests
    elsif SgtnClient::CacheUtil.is_expired(cache_item) && locale != SgtnClient::LocaleUtil.get_source_locale # local source never expires.
      Thread.new { super } # refresh in background
    end
    cache_item
  end
end
