# -*-coding:UTF-8 -*-
#
# Copyright 2020-2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

try:
    from icu import Locale, Formattable, MessageFormat, UnicodeString
    _temp = Locale('en')
    _support_icu = True
except Exception:
    _support_icu = False


def _icu_format(locale, text, array):
    _locale = Locale(locale)
    _msg_fmt = MessageFormat(text, _locale)
    _fmt_params = []
    for one in array:
        _fmt_params.append(Formattable(one))
    result = _msg_fmt.format(_fmt_params)
    return result


class SgtnIcu:

    @classmethod
    def is_available(cls):
        return _support_icu

    @classmethod
    def format(cls, locale, text, array):
        if not _support_icu:
            return text.format(*array)

        try:
            return _icu_format(locale, text, array)
        except Exception:
            return text.format(*array)

    @classmethod
    def get_locale_display_name(cls, locale, display_locale):
        if not cls.is_available() or not display_locale:
            return None

        icu_locale = Locale(locale)
        icu_locale_display = Locale(display_locale)
        string = UnicodeString()
        display_name = '{0}'.format(icu_locale.getDisplayName(icu_locale_display, string))
        return display_name
