# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

# frozen_string_literal: true

module Singleton
  module Base
    def translate(key, component: nil, locale: nil, **kwargs)
      SgtnClient.logger.debug "[Translation.getString]component: #{component}, key: #{key}, locale: #{locale}, args: #{args}"
      locale = LocaleUtil.get_best_locale(locale)
      str = getTranslation(component, key, locale)
      if str
        locale = str.locale if str.is_a?(SgtnClient::StringUtil)
      elsif !LocaleUtil.is_source_locale(locale)
        locale = LocaleUtil.get_source_locale
        str = getTranslation(component, key, locale)
      end
      return str if str.nil? || kwargs.empty?

      return str.localize(locale) % kwargs
    end
    alias t translate

    # raise error when translation is not found
    def translate!(key, **options)
      translate(key, **options, raise: true)
    end
    alias t! translate!

    # Executes block with given locale set.
    def with_locale(tmp_locale = nil)
      if tmp_locale.nil?
        yield
      else
        current_locale = locale
        self.locale = tmp_locale
        begin
          yield
        ensure
          self.locale = current_locale
        end
      end
    end

    def available_locales; end

    def available_components; end

    def init_translations; end
  end

  extend Base
end
