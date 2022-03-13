# Copyright 2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0

module SgtnClient
    class T

        def self.s(key)
            locale = RequestStore.store[:locale]
            component = RequestStore.store[:component]
            return Translation.getString(component, key, locale)
        end

        def self.s_f(key, args)
            locale = RequestStore.store[:locale]
            component = RequestStore.store[:component]
            return Translation.getString_f(component, key, args, locale)
        end

        def self.c()
            locale = RequestStore.store[:locale]
            component = RequestStore.store[:component]
            return Translation.getStrings(component, locale)
        end
    end
end
