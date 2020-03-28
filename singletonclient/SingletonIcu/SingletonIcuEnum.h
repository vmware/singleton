/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

using namespace System;

namespace SingletonIcu {

	public enum class DateTimeStyle
	{
		/** Full style */
		UDAT_FULL,
		/** Long style */
		UDAT_LONG,
		/** Medium style */
		UDAT_MEDIUM,
		/** Short style */
		UDAT_SHORT,
		/** Default style */
		UDAT_DEFAULT = UDAT_MEDIUM,

		/** Bitfield for relative date */
		UDAT_RELATIVE = (1 << 7),

		UDAT_FULL_RELATIVE = UDAT_FULL | UDAT_RELATIVE,

		UDAT_LONG_RELATIVE = UDAT_LONG | UDAT_RELATIVE,

		UDAT_MEDIUM_RELATIVE = UDAT_MEDIUM | UDAT_RELATIVE,

		UDAT_SHORT_RELATIVE = UDAT_SHORT | UDAT_RELATIVE,


		/** No style */
		UDAT_NONE = -1,

		/**
		 * Use the pattern given in the parameter to udat_open
		 * @see udat_open
		 * @stable ICU 50
		 */
		 UDAT_PATTERN = -2,
	};

	public enum class RelativeDateTimeStyle
	{
		/**
		 * Everything spelled out.
		 * @stable ICU 54
		 */
		UDAT_STYLE_LONG,

		/**
		 * Abbreviations used when possible.
		 * @stable ICU 54
		 */
		UDAT_STYLE_SHORT,

		/**
		 * Use the shortest possible form.
		 * @stable ICU 54
		 */
		UDAT_STYLE_NARROW,
	};

	public enum class DisplayContextType
	{
		/**
		 * Type to retrieve the dialect handling setting, e.g.
		 * UDISPCTX_STANDARD_NAMES or UDISPCTX_DIALECT_NAMES.
		 * @stable ICU 51
		 */
		UDISPCTX_TYPE_DIALECT_HANDLING = 0,
		/**
		 * Type to retrieve the capitalization context setting, e.g.
		 * UDISPCTX_CAPITALIZATION_NONE, UDISPCTX_CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE,
		 * UDISPCTX_CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE, etc.
		 * @stable ICU 51
		 */
		UDISPCTX_TYPE_CAPITALIZATION = 1,
		/**
		 * Type to retrieve the display length setting, e.g.
		 * UDISPCTX_LENGTH_FULL, UDISPCTX_LENGTH_SHORT.
		 * @stable ICU 54
		 */
		UDISPCTX_TYPE_DISPLAY_LENGTH = 2,
		/**
		 * Type to retrieve the substitute handling setting, e.g.
		 * UDISPCTX_SUBSTITUTE, UDISPCTX_NO_SUBSTITUTE.
		 * @stable ICU 58
		 */
		UDISPCTX_TYPE_SUBSTITUTE_HANDLING = 3
	};

	public enum class DisplayContext 
	{
		/**
		 * ================================
		 * DIALECT_HANDLING can be set to one of UDISPCTX_STANDARD_NAMES or
		 * UDISPCTX_DIALECT_NAMES. Use UDisplayContextType UDISPCTX_TYPE_DIALECT_HANDLING
		 * to get the value.
		 */
		/**
		 * A possible setting for DIALECT_HANDLING:
		 * use standard names when generating a locale name,
		 * e.g. en_GB displays as 'English (United Kingdom)'.
		 * @stable ICU 51
		 */
		UDISPCTX_STANDARD_NAMES = ((int)(DisplayContextType::UDISPCTX_TYPE_DIALECT_HANDLING)<<8) + 0,
		/**
		 * A possible setting for DIALECT_HANDLING:
		 * use dialect names, when generating a locale name,
		 * e.g. en_GB displays as 'British English'.
		 * @stable ICU 51
		 */
		UDISPCTX_DIALECT_NAMES = ((int)(DisplayContextType::UDISPCTX_TYPE_DIALECT_HANDLING)<<8) + 1,
		/**
		 * ================================
		 * CAPITALIZATION can be set to one of UDISPCTX_CAPITALIZATION_NONE,
		 * UDISPCTX_CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE,
		 * UDISPCTX_CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE,
		 * UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU, or
		 * UDISPCTX_CAPITALIZATION_FOR_STANDALONE.
		 * Use UDisplayContextType UDISPCTX_TYPE_CAPITALIZATION to get the value.
		 */
		/**
		 * The capitalization context to be used is unknown (this is the default value).
		 * @stable ICU 51
		 */
		UDISPCTX_CAPITALIZATION_NONE = ((int)(DisplayContextType::UDISPCTX_TYPE_CAPITALIZATION)<<8) + 0,
		/**
		 * The capitalization context if a date, date symbol or display name is to be
		 * formatted with capitalization appropriate for the middle of a sentence.
		 * @stable ICU 51
		 */
		UDISPCTX_CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE = ((int)(DisplayContextType::UDISPCTX_TYPE_CAPITALIZATION)<<8) + 1,
		/**
		 * The capitalization context if a date, date symbol or display name is to be
		 * formatted with capitalization appropriate for the beginning of a sentence.
		 * @stable ICU 51
		 */
		UDISPCTX_CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE = ((int)(DisplayContextType::UDISPCTX_TYPE_CAPITALIZATION)<<8) + 2,
		/**
		 * The capitalization context if a date, date symbol or display name is to be
		 * formatted with capitalization appropriate for a user-interface list or menu item.
		 * @stable ICU 51
		 */
		UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU = ((int)(DisplayContextType::UDISPCTX_TYPE_CAPITALIZATION)<<8) + 3,
		/**
		 * The capitalization context if a date, date symbol or display name is to be
		 * formatted with capitalization appropriate for stand-alone usage such as an
		 * isolated name on a calendar page.
		 * @stable ICU 51
		 */
		UDISPCTX_CAPITALIZATION_FOR_STANDALONE = ((int)(DisplayContextType::UDISPCTX_TYPE_CAPITALIZATION)<<8) + 4,
		/**
		 * ================================
		 * DISPLAY_LENGTH can be set to one of UDISPCTX_LENGTH_FULL or
		 * UDISPCTX_LENGTH_SHORT. Use UDisplayContextType UDISPCTX_TYPE_DISPLAY_LENGTH
		 * to get the value.
		 */
		/**
		 * A possible setting for DISPLAY_LENGTH:
		 * use full names when generating a locale name,
		 * e.g. "United States" for US.
		 * @stable ICU 54
		 */
		UDISPCTX_LENGTH_FULL = ((int)(DisplayContextType::UDISPCTX_TYPE_DISPLAY_LENGTH)<<8) + 0,
		/**
		 * A possible setting for DISPLAY_LENGTH:
		 * use short names when generating a locale name,
		 * e.g. "U.S." for US.
		 * @stable ICU 54
		 */
		UDISPCTX_LENGTH_SHORT = ((int)(DisplayContextType::UDISPCTX_TYPE_DISPLAY_LENGTH)<<8) + 1,
		/**
		 * ================================
		 * SUBSTITUTE_HANDLING can be set to one of UDISPCTX_SUBSTITUTE or
		 * UDISPCTX_NO_SUBSTITUTE. Use UDisplayContextType UDISPCTX_TYPE_SUBSTITUTE_HANDLING
		 * to get the value.
		 */
		/**
		 * A possible setting for SUBSTITUTE_HANDLING:
		 * Returns a fallback value (e.g., the input code) when no data is available.
		 * This is the default value.
		 * @stable ICU 58
		 */
		UDISPCTX_SUBSTITUTE = ((int)(DisplayContextType::UDISPCTX_TYPE_SUBSTITUTE_HANDLING)<<8) + 0,
		/**
		 * A possible setting for SUBSTITUTE_HANDLING:
		 * Returns a null value when no data is available.
		 * @stable ICU 58
		 */
		UDISPCTX_NO_SUBSTITUTE = ((int)(DisplayContextType::UDISPCTX_TYPE_SUBSTITUTE_HANDLING)<<8) + 1

	};

	public enum class RelativeDateTimeUnit
	{
		/**
		 * Specifies that relative unit is year, e.g. "last year",
		 * "in 5 years". 
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_YEAR,
		/**
		 * Specifies that relative unit is quarter, e.g. "last quarter",
		 * "in 5 quarters".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_QUARTER,
		/**
		 * Specifies that relative unit is month, e.g. "last month",
		 * "in 5 months".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_MONTH,
		/**
		 * Specifies that relative unit is week, e.g. "last week",
		 * "in 5 weeks".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_WEEK,
		/**
		 * Specifies that relative unit is day, e.g. "yesterday",
		 * "in 5 days".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_DAY,
		/**
		 * Specifies that relative unit is hour, e.g. "1 hour ago",
		 * "in 5 hours".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_HOUR,
		/**
		 * Specifies that relative unit is minute, e.g. "1 minute ago",
		 * "in 5 minutes".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_MINUTE,
		/**
		 * Specifies that relative unit is second, e.g. "1 second ago",
		 * "in 5 seconds".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_SECOND,
		/**
		 * Specifies that relative unit is Sunday, e.g. "last Sunday",
		 * "this Sunday", "next Sunday", "in 5 Sundays".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_SUNDAY,
		/**
		 * Specifies that relative unit is Monday, e.g. "last Monday",
		 * "this Monday", "next Monday", "in 5 Mondays".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_MONDAY,
		/**
		 * Specifies that relative unit is Tuesday, e.g. "last Tuesday",
		 * "this Tuesday", "next Tuesday", "in 5 Tuesdays".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_TUESDAY,
		/**
		 * Specifies that relative unit is Wednesday, e.g. "last Wednesday",
		 * "this Wednesday", "next Wednesday", "in 5 Wednesdays".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_WEDNESDAY,
		/**
		 * Specifies that relative unit is Thursday, e.g. "last Thursday",
		 * "this Thursday", "next Thursday", "in 5 Thursdays". 
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_THURSDAY,
		/**
		 * Specifies that relative unit is Friday, e.g. "last Friday",
		 * "this Friday", "next Friday", "in 5 Fridays".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_FRIDAY,
		/**
		 * Specifies that relative unit is Saturday, e.g. "last Saturday",
		 * "this Saturday", "next Saturday", "in 5 Saturdays".
		 * @stable ICU 57
		 */
		UDAT_REL_UNIT_SATURDAY,
	};
	
}
