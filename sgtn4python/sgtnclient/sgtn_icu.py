# -*-coding:UTF-8 -*-
#
# Copyright 2020-2022 VMware, Inc.
# SPDX-License-Identifier: EPL-2.0
#

_support_icu = False
try:
    from icu import Locale, Formattable, MessageFormat
    _temp = Locale('en')
    _support_icu = True
except Exception:
    pass


def _icu_format(locale, text, array):
    _locale = Locale(locale)
    _msg_fmt = MessageFormat(text, _locale)
    _fmt_params = []
    for one in array:
        _fmt_params.append(Formattable(one))
    result = _msg_fmt.format(_fmt_params)
    return result


def sgtn_is_icu_available():
    return _support_icu


def sgtn_icu_format(locale, text, array):
    if not _support_icu:
        return text.format(*array)

    try:
        return _icu_format(locale, text, array)
    except Exception:
        return text.format(*array)
