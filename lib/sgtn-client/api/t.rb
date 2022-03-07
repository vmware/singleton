# 
#  Copyright 2019-2022 VMware, Inc.
#  SPDX-License-Identifier: EPL-2.0
#

module SgtnClient
    class T < Translation

        def self.s(key)
            locale = RequestStore.store[:locale]
            component = RequestStore.store[:component]
            return getString(component, key, locale)
        end

        def self.s_f(key, args)
            locale = RequestStore.store[:locale]
            component = RequestStore.store[:component]
            return getString_f(component, key, args, locale)
        end

        def self.c(args = {})
            locale = RequestStore.store[:locale]
            component = RequestStore.store[:component]
            return getStrings(component, locale, args)
        end
    end
end