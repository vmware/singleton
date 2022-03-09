# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'date'
require 'time'

DateTime.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 2
          def l_full_s(locale = TwitterCldr.locale, *args)
            timezone = args[0]
            display_name = args[1]
            if timezone.nil?
               self.localize(locale).to_full_s
            elsif display_name.nil?
               self.localize(locale).with_timezone(timezone).to_full_s
            else
               tz = TwitterCldr::Timezones::Timezone.instance(timezone, locale)
               tz.display_name_for(self, display_name)
            end
          end
        LOCALIZE

DateTime.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
          def l_long_s(locale = TwitterCldr.locale, *args)
             timezone = args[0]
             display_name = args[1]
             if timezone.nil?
                self.localize(locale).to_long_s
             elsif display_name.nil?
                self.localize(locale).with_timezone(timezone).to_long_s
             else
                tz = TwitterCldr::Timezones::Timezone.instance(timezone, locale)
                tz.display_name_for(self, display_name)
             end
          end
        LOCALIZE

DateTime.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
          def l_medium_s(locale = TwitterCldr.locale, *args)
             timezone = args[0]
             display_name = args[1]
             if timezone.nil?
                self.localize(locale).to_medium_s
             elsif display_name.nil?
                self.localize(locale).with_timezone(timezone).to_medium_s
             else
                tz = TwitterCldr::Timezones::Timezone.instance(timezone, locale)
                tz.display_name_for(self, display_name)
             end
          end
        LOCALIZE


DateTime.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
        def l_short_s(locale = TwitterCldr.locale, *args)
           timezone = args[0]
           display_name = args[1]
           if timezone.nil?
              self.localize(locale).to_short_s
           elsif display_name.nil?
              self.localize(locale).with_timezone(timezone).to_short_s
           else
              tz = TwitterCldr::Timezones::Timezone.instance(timezone, locale)
              tz.display_name_for(self, display_name)
           end
        end
      LOCALIZE
