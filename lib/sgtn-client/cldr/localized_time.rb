# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

require 'date'
require 'time'

Time.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
          def l_full_s(locale = TwitterCldr.locale)
             self.localize(locale).to_full_s
          end
        LOCALIZE

Time.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
          def l_long_s(locale = TwitterCldr.locale)
             self.localize(locale).to_long_s
          end
        LOCALIZE

Time.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
          def l_medium_s(locale = TwitterCldr.locale)
             self.localize(locale).to_medium_s
          end
        LOCALIZE


Time.class_eval <<-LOCALIZE, __FILE__, __LINE__ + 1
        def l_short_s(locale = TwitterCldr.locale)
           self.localize(locale).to_short_s
        end
      LOCALIZE
