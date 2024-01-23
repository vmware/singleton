/*
 * Copyright 2022-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package formatting

import (
	"math/big"
	"regexp"
	"strconv"
	"strings"
	"time"

	"sgtnserver/api"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrservice"
	"sgtnserver/modules/formatting"

	"github.com/gin-gonic/gin"
	jsoniter "github.com/json-iterator/go"
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

	ctxWithLog := logger.NewContext(c, c.MustGet(api.LoggerKey))
	patterData, cldrLocale, err := cldrservice.GetPatternByLocale(ctxWithLog, params.Locale, cldr.PatternNumbers, "")
	if err != nil {
		patterData, cldrLocale, err = cldrservice.GetPatternByLocale(ctxWithLog, cldr.EnLocale, cldr.PatternNumbers, "")
	}
	if err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WrapErrorWithMessage(err, "fail to get pattern data"))
		return
	}

	numbers := patterData["numbers"].(jsoniter.Any)
	decimalFormats := numbers.Get("numberFormats", "decimalFormats").ToString()
	numberSymbols := numbers.Get("numberSymbols")
	decimalSign := numberSymbols.Get("decimal").ToString()
	groupSign := numberSymbols.Get("group").ToString()

	re := regexp.MustCompile(`^#,(?:(#+),)*(#*0*)\..*$`)
	stringSubmatch := re.FindStringSubmatch(decimalFormats)
	groupLength := len(stringSubmatch[1])
	if groupLength == 0 {
		groupLength = len(stringSubmatch[2])
	}

	bigFloat, _, _ := big.ParseFloat(params.Number, 10, 0, big.ToNearestAway)
	roundedNumberStr := bigFloat.Text('f', params.Scale)
	sign := ""
	if roundedNumberStr[0] == '-' {
		sign = "-"
		roundedNumberStr = roundedNumberStr[1:]
	}

	decimalPointIndex := strings.LastIndex(roundedNumberStr, ".")
	if decimalPointIndex == -1 {
		decimalPointIndex = len(roundedNumberStr)
	}

	var sb strings.Builder
	sb.WriteString(sign)
	firstPartLength := decimalPointIndex - len(stringSubmatch[2]) // Remove the decimal part and the last group of the integer part
	if firstPartLength > 0 {
		i := firstPartLength % groupLength // the first group length
		if i != 0 {
			sb.WriteString(roundedNumberStr[:i])
			sb.WriteString(groupSign)
		}
		for ; i < firstPartLength; i += groupLength {
			sb.WriteString(roundedNumberStr[i : i+groupLength])
			sb.WriteString(groupSign)
		}
		sb.WriteString(roundedNumberStr[i:decimalPointIndex])
	} else {
		sb.WriteString(roundedNumberStr[:decimalPointIndex])
	}

	if params.Scale > 0 {
		sb.WriteString(decimalSign)
		sb.WriteString(roundedNumberStr[decimalPointIndex+1:])
	}

	data := NumberResp{
		Locale:          cldrLocale,
		Number:          params.Number,
		Scale:           strconv.FormatInt(int64(params.Scale), 10),
		FormattedNumber: sb.String()}
	api.HandleResponse(c, data, nil)
}
