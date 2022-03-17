# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
  autoload :SingleOperation, 'sgtn-client/common'
  autoload :CacheUtil, 'sgtn-client/util/cache-util'

  module Core
    autoload :Request, 'sgtn-client/core/request'
  end

  module Translation
    def self.getString(component, key, locale)
      SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}"
      getTranslation(component, key, locale)
    end

    def self.getString_p(component, key, plural_args, locale)
      SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      cache_item = get_cs(component, locale)
      str = cache_item&.dig(:items, 'messages', key)
      locale = cache_item&.dig(:metadata, FALLBACK_LOCALE) || cache_item&.dig(:items, 'messages', FALLBACK_LOCALE, key) || locale
      str.to_plural_s(locale, plural_args)
    end

    def self.getString_f(component, key, args, locale, *optionals)
      SgtnClient.logger.debug "[Translation][getString_f]component=#{component}, key=#{key}, locale=#{locale}"
      s = getString(component, key, locale, *optionals)
      return nil if s.nil?

      if args.is_a?(Hash)
        args.each do |source, arg|
          s.gsub! "{#{source}}", arg
        end
      elsif args.is_a?(Array)
        s = s % args
      end
      s
    end

    def self.getStrings(component, locale)
      SgtnClient.logger.debug "[Translation][getStrings]component=#{component}, locale=#{locale}"
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      items = get_cs(component, locale)&.dig(:items)
      return items unless items&.dig('messages').nil?
      get_cs(component, LocaleUtil.get_source_locale)&.dig(:items) unless LocaleUtil.is_source_locale(locale)
    end

    def self.getTranslation(component, key, locale)
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      get_cs(component, locale)&.dig(:items, 'messages', key)
    end

    def self.get_cs(component, locale)
      cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
      SgtnClient.logger.debug "[Translation][get_cs]cache_key=#{cache_key}"
      cache_item = SgtnClient::CacheUtil.get_cache(cache_key)
      if cache_item.nil?
        cache_item = refresh_cache(component, locale).value # refresh synchronously if not in cache
      elsif CacheUtil.is_expired(cache_item) && locale != LocaleUtil.get_source_locale # local source never expires.
        refresh_cache(component, locale) # refresh in background
      end

      return cache_item
    end

    def self.load(component, locale)
      env = SgtnClient::Config.default_environment
      mode = SgtnClient::Config.configurations[env]['bundle_mode']
      SgtnClient.logger.debug "[Translation][load]mode=#{mode}"
      if mode == 'offline'
        load_o(component, locale)
      else
        load_s(component, locale)
      end
    end

    def self.load_o(component, locale)
      env = SgtnClient::Config.default_environment
      product_name = SgtnClient::Config.configurations[env]['product_name']
      version = SgtnClient::Config.configurations[env]['version'].to_s
      translation_bundle = SgtnClient::Config.configurations[env]['translation_bundle']
      bundlepath = translation_bundle + '/' + product_name + '/' + version + '/' + component + '/messages_' + locale + '.json'
      SgtnClient::FileUtil.read_json(bundlepath)
    end

    def self.load_s(component, locale)
      env = SgtnClient::Config.default_environment
      product_name = SgtnClient::Config.configurations[env]['product_name']
      vip_server = SgtnClient::Config.configurations[env]['vip_server']
      version = SgtnClient::Config.configurations[env]['version'].to_s
      url = vip_server + '/i18n/api/v2/translation/products/' + product_name + '/versions/' + version + '/locales/' + locale + '/components/' + component + '?checkTranslationStatus=false&machineTranslation=false&pseudo=false'
      begin
        obj = SgtnClient::Core::Request.get(url)
      rescue StandardError => e
        SgtnClient.logger.error e.message
      end
      obj && obj['data']
    end

    def self.fetch(component, locale)
      # source locale always uses source bundles
      return Source.getBundle(component) if LocaleUtil.is_source_locale(locale)

      translation_bundle_thread = Thread.new { load(component, locale) }
      old_source_bundle = load(component, LocaleUtil.get_source_locale)
      source_bundle = get_cs(component, LocaleUtil.get_source_locale)&.dig(:items)
      translation_bundle = translation_bundle_thread.value

      return source_bundle, LocaleUtil.get_source_locale if translation_bundle.nil?
      compare_source(translation_bundle, old_source_bundle, source_bundle)
    end

    FALLBACK_LOCALE = :fallback_locale
    def self.compare_source(translation_bundle, old_source_bundle, source_bundle)
      return translation_bundle if source_bundle.nil? || old_source_bundle.nil?
 
      old_source_messages = old_source_bundle['messages']
      translation_messages = translation_bundle['messages']
      translation_bundle['messages'] = new_translation_messages = {}
      source_bundle['messages'].each do |key, value|
        translation = translation_messages[key]
        if old_source_messages[key] == value && !translation.nil?
          new_translation_messages[key] = translation
        else
          new_translation_messages[key] = value
          new_translation_messages[FALLBACK_LOCALE] ||= Hash.new
          new_translation_messages[FALLBACK_LOCALE][key] = LocaleUtil.get_source_locale
        end
      end
      translation_bundle
    end

    none_alive = proc { |_, thread| thread.nil? || thread.alive? == false }
    to_run = proc do |cache_key|
      cache_item = SgtnClient::CacheUtil.get_cache(cache_key)
      cache_item.nil? || CacheUtil.is_expired(cache_item)
    end
    @refresh_cache_operator = SingleOperation.new(none_alive, to_run) do |cache_key, _, component, locale|
      Thread.new do
        items, locale = fetch(component, locale)
        metadata = {FALLBACK_LOCALE => locale} unless locale.nil?
        cache_item = SgtnClient::CacheUtil.write_cache(cache_key, items, metadata)
        # delete thread from hash after finish
        Thread.new { @refresh_cache_operator.remove_object(cache_key) }
        cache_item
      end
    end
    def self.refresh_cache(component, locale)
      cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
      @refresh_cache_operator.operate(cache_key, component, locale)
    end
  end
end
