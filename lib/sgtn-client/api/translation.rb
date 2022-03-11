# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'multi_json'

module SgtnClient
  module Core
    autoload :Request,     'sgtn-client/core/request'
    autoload :Cache,       'sgtn-client/core/cache'
    autoload :CacheUtil,   'sgtn-client/util/cache-util'
    autoload :LocaleUtil, 'sgtn-client/util/locale-util'
  end

  class Translation
    def self.getString(component, key, locale)
      SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}"
      str = getTranslation(component, key, locale)
      if str.nil? && !LocaleUtil.is_source_locale(locale)
        str = getTranslation(component, key, LocaleUtil.get_source_locale)
      end
      str
    end

    def self.getString_p(component, key, plural_args, locale)
      SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
      str = getTranslation(component, key, locale)
      if str.nil?
        unless LocaleUtil.is_source_locale(locale)
          str = getTranslation(component, key, LocaleUtil.get_source_locale)
          str.to_plural_s(LocaleUtil.get_source_locale, plural_args) if str
        end
      else
        str.to_plural_s(locale, plural_args)
      end
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
        s = sprintf(s % args)
      end
      s
    end

    def self.getStrings(component, locale)
      SgtnClient.logger.debug "[Translation][getStrings]component=#{component}, locale=#{locale}"
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      items = get_cs(component, locale)
      if (items.nil? || items['messages'].nil?) && !LocaleUtil.is_source_locale(locale)
        items = get_cs(component, LocaleUtil.get_source_locale)
      end

      items
    end

    def self.getTranslation(component, key, locale)
      locale = SgtnClient::LocaleUtil.get_best_locale(locale)
      items = get_cs(component, locale)
      items&.dig('messages', key)
    end

    def self.get_cs(component, locale)
      cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
      SgtnClient.logger.debug "[Translation][get_cs]cache_key=#{cache_key}"
      expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
      if items.nil?
        items = single_refresh(component, locale).value # refresh synchronously if not in cache
      elsif expired && locale != LocaleUtil.get_source_locale # local source never expires.
        single_refresh(component, locale) # refresh in background
      end

      items
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

    def self.load_bundles_for_comparison(component, locale)
      # source locale always uses source bundles
      return Source.getBundle(component) if LocaleUtil.is_source_locale(locale)

      source_bundle = get_cs(component, LocaleUtil.get_source_locale)
      translation_bundle_thread = Thread.new { load(component, locale) }
      old_source_bundle = load(component, LocaleUtil.get_source_locale)
      translation_bundle = translation_bundle_thread.value

      [source_bundle, old_source_bundle, translation_bundle]
    end

    def self.load_and_compare_source(component, locale)
      source_bundle, old_source_bundle, translation_bundle = load_bundles_for_comparison(component, locale)
      return source_bundle if LocaleUtil.is_source_locale(locale)

      if translation_bundle.nil? || source_bundle.nil? || old_source_bundle.nil? ||
         translation_bundle.empty? || source_bundle.empty? || old_source_bundle.empty?
        return translation_bundle
      end

      source_messages = source_bundle['messages']
      old_source_messages = old_source_bundle['messages']
      translation_messages = translation_bundle['messages']
      new_translation_messages = {}
      source_messages.each do |key, value|
        new_translation_messages[key] = old_source_messages[key] == value ? translation_messages[key] : value
      end
      translation_bundle['messages'] = new_translation_messages
      translation_bundle
    end

    @@refresh_threads_lock = Mutex.new
    @@refresh_threads = {}
    def self.single_refresh(component, locale)
      cache_key = SgtnClient::CacheUtil.get_cachekey(component, locale)
      @@refresh_threads_lock.synchronize do
        thread = @@refresh_threads[cache_key]
        if thread.nil? || thread.alive? == false
          expired, items = SgtnClient::CacheUtil.get_cache(cache_key)
          if expired || items.nil?
            SgtnClient.logger.info do
              message = expired ? 'Cache expired' : 'Initialize cache'
              format('%-18s %-16s %-15s %s', Thread.current.name, "[#{__method__}]", "[#{component}.#{locale}]", "Starting a new thread: #{message}")
            end
            thread = Thread.new(Thread.current.name) do |parent_thread_name|
              Thread.current.name = (0...3).map { rand(65..90).chr }.join
              Thread.current.name = "#{parent_thread_name}:#{Thread.current.name}"
              SgtnClient.logger.info { format('%-18s %-16s %-15s %s', Thread.current.name, "[#{__method__}]", "[#{component}.#{locale}]", 'Thread is started') }
              items = load_and_compare_source(component, locale)
              SgtnClient::CacheUtil.write_cache(cache_key, items) if items&.empty? == false

              SgtnClient.logger.info { format('%-18s %-16s %-15s %s', Thread.current.name, "[#{__method__}]", "[#{component}.#{locale}]", 'Thread is going to end') }
              items
            end
            @@refresh_threads[cache_key] = thread
          else
            SgtnClient.logger.info { format('%-18s %-16s %-15s %s', Thread.current.name, "[#{__method__}]", "[#{component}.#{locale}]", ' Not expired') }
          end
        else
          SgtnClient.logger.info { format('%-18s %-16s %-15s %s', Thread.current.name, "[#{__method__}]", "[#{component}.#{locale}]", 'Another thread is alive') }
        end
        return thread
      end
    end
  end
end
