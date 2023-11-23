/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package formatting

import (
	"strconv"
	"time"

	"golang.org/x/text/language"
	"golang.org/x/text/message"
	"golang.org/x/text/number"

	"sgtnserver/api"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/formatting"

	"github.com/gin-gonic/gin"
)

// GetLocalizedDate godoc
// @Summary Get localized date
// @Description Get localized date by locale and pattern
// @Tags formatting-api
// @Produce json
// @Param locale query string true "locale String. e.g. 'en-US'"
// @Param longDate query int true "long value of the date(e.g. 1472728030290). the specified number of milliseconds since the standard base time known as 'the epoch', namely January 1, 1970, 00:00:00 GMT."
// @Param pattern query string true "pattern used to format the long date(the value could be one of this: YEAR = 'y',QUARTER = 'QQQQ',ABBR_QUARTER = 'QQQ',QUARTER_YEAR = 'QQQQy',QUARTER_ABBR_YEAR = 'QQQy',MONTH = 'MMMM',ABBR_MONTH = 'MMM',NUM_MONTH = 'M',MONTH_YEAR = 'MMMMy',MONTH_ABBR_YEAR = 'MMMy',MONTH_NUM_YEAR = 'My',DAY = 'd',MONTH_DAY_YEAR = 'MMMMdy',ABBR_MONTH_DAY_YEAR = 'MMMdy',NUM_MONTH_DAY_YEAR = 'Mdy',WEEKDAY = 'EEEE',ABBR_WEEKDAY = 'E',WEEKDAY_MONTH_DAY_YEAR = 'EEEEMMMMdy',ABBR_WEEKDAY_MONTH_DAY_YEAR = 'EMMMdy',NUM_WEEKDAY_MONTH_DAY_YEAR = 'EMdy',MONTH_DAY = 'MMMMd',ABBR_MONTH_DAY = 'MMMd',NUM_MONTH_DAY = 'Md',WEEKDAY_MONTH_DAY = 'EEEEMMMMd',ABBR_WEEKDAY_MONTH_DAY = 'EMMMd',NUM_WEEKDAY_MONTH_DAY = 'EMd')"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /formatting/date/localizedDate [get]
// @Deprecated
func GetLocalizedDate(c *gin.Context) {
	params := DateReq{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	tm := time.Unix(0, params.LongDate*int64(time.Millisecond))
	formatted, err := formatting.SimpleFormatDateTime(ctx, tm, params.Pattern, params.Locale)
	data := DateResp{
		Pattern:       params.Pattern,
		Locale:        params.Locale,
		LongDate:      params.LongDate,
		FormattedDate: formatted}

	api.HandleResponse(c, data, err)
}

// GetLocalizedNumber godoc
// @Summary Get localized number
// @Description Get localized number by locale and scale
// @Tags formatting-api
// @Produce json
// @Param locale query string true "locale String. e.g. 'en-US'"
// @Param number query number true "number to format"
// @Param scale query int false "decimal digits"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 500 {string} string "Internal Server Error"
// @Router /formatting/number/localizedNumber [get]
// @Deprecated
func GetLocalizedNumber(c *gin.Context) {
	params := NumberRequest{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	language_tag, err := language.Parse(params.Locale)
	if err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WrapErrorWithMessage(err, "invalid Locale '%s'", params.Locale))
		return
	}

	result := message.NewPrinter(language_tag).Sprint(number.Decimal(params.Number, number.MinFractionDigits(int(params.Scale))))
	data := NumberResp{
		Locale:          params.Locale,
		Number:          strconv.FormatFloat(params.Number, 'f', -1, 64),
		Scale:           strconv.FormatInt(int64(params.Scale), 10),
		FormattedNumber: result}
	api.HandleResponse(c, data, nil)
}
