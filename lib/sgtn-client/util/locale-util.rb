module SgtnClient

    DEFAULT_LOCALES = ['en', 'de', 'es', 'fr', 'ko', 'ja', 'zh-Hans', 'zh-Hant']

    MAP_LOCALES = {
                    "zh-CN" =>"zh-Hans",
                    "zh-TW" =>"zh-Hant",
                    "zh-Hans-CN" =>"zh-Hans",
                    "zh-Hant-TW" =>"zh-Hant",
                  }

    class LocaleUtil
        def self.process_locale(locale=nil)
            locale ||= SgtnClient::Config.configurations.default
            locale.to_s
        end
        def self.fallback(locale)
            found = SgtnClient::DEFAULT_LOCALES.select {|e| e == locale}
            if !found.empty?
                return found[0]
            end
            if SgtnClient::MAP_LOCALES.key?(locale)
                return SgtnClient::MAP_LOCALES[locale]
            end
            parts = locale.split("-")
            if parts.size > 1
                f = SgtnClient::DEFAULT_LOCALES.select {|e| e == parts[0]}
                if !f.empty?
                    return f[0]
                end
            end 
            return locale
        end
    end
end