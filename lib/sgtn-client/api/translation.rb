# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

require 'multi_json'

module SgtnClient

  module Core
    autoload :Request,     "sgtn-client/core/request"
    autoload :Cache,       "sgtn-client/core/cache"
    autoload :CacheUtil,   "sgtn-client/util/cache-util"
    autoload :LocaleUtil,   "sgtn-client/util/locale-util"
  end
  
  class Translation

      def self.getString(component, key, locale)
        SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}"
        str = SgtnClient::ServiceUtil.get_translation(component, key, locale)
        if str.nil?
          str = SgtnClient::Source.getSource(component, key, SgtnClient::Config.configurations.default)
          if str.nil?
            SgtnClient.logger.debug "[Translation][getString] Missing source string with key: #{key}, component: #{component}, locale: #{locale}"
          end
        else
          unless SgtnClient::LocaleUtil.is_source_locale(locale)
            source = SgtnClient::Source.getSource(component, key, SgtnClient::Config.configurations.default)
            str = SgtnClient::ServiceUtil.compare_source(component, key, SgtnClient::LocaleUtil.get_source_locale, source, str)
          end
        end
        str
      end

      def self.getString_p(component, key, plural_args, locale)
        SgtnClient.logger.debug "[Translation][getString_p]component=#{component}, key=#{key}, locale=#{locale}"
        str = SgtnClient::ServiceUtil.get_translation(component, key, locale)
        if str.nil?
          str = SgtnClient::Source.getSource(component, key, SgtnClient::Config.configurations.default)
          if str.nil?
            SgtnClient.logger.debug "[Translation][getString_p] Missing source string with key: #{key}, component: #{component}, locale: #{locale}"
            return nil
          end
          str.to_plural_s(:en, plural_args)
        else
          str.to_plural_s(locale, plural_args)
        end
      end

      def self.getString_f(component, key, args, locale, *optionals)
         SgtnClient.logger.debug "[Translation][getString_f]component=#{component}, key=#{key}, locale=#{locale}"
         s = getString(component, key, locale, *optionals)
         if s.nil?
          return nil
         end
         if args.is_a?(Hash)
          args.each do |source, arg|
            s.gsub! "{#{source}}", arg
          end
         elsif args.is_a?(Array)
          s = sprintf s % args
         end
         return s
      end

      def self.getStrings(component, locale)
        SgtnClient.logger.debug "[Translation][getStrings]component=#{component}, locale=#{locale}"
        locale = SgtnClient::LocaleUtil.get_best_locale(locale)
        items = SgtnClient::ServiceUtil.get_cs(component, locale)
        default = SgtnClient::Config.configurations.default
        if items.nil? || items["messages"] == nil
          items = {}
          s = SgtnClient::Source.getSources(component, default)
          if s.nil?
            SgtnClient.logger.error "[Translation][getStrings] Missing component: #{component}, locale: #{locale}"
          else
            default_component, value = s.first
            items["component"] = component
            items["messages"] = value
            items["locale"] = 'source'
          end
        end
        return items
       end
  end
end