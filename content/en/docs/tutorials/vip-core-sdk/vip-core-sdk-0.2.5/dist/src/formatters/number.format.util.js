"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
var CurrenciesDataType;
(function (CurrenciesDataType) {
    CurrenciesDataType["DIGIST"] = "_digits";
    CurrenciesDataType["ROUNDING"] = "_rounding";
})(CurrenciesDataType = exports.CurrenciesDataType || (exports.CurrenciesDataType = {}));
var RoundingMode;
(function (RoundingMode) {
    RoundingMode[RoundingMode["ROUND_UP"] = 0] = "ROUND_UP";
    RoundingMode[RoundingMode["ROUND_DOWN"] = 1] = "ROUND_DOWN";
    RoundingMode[RoundingMode["ROUND_CEIL"] = 2] = "ROUND_CEIL";
    RoundingMode[RoundingMode["ROUND_FLOOR"] = 3] = "ROUND_FLOOR";
    RoundingMode[RoundingMode["ROUND_HALF_UP"] = 4] = "ROUND_HALF_UP";
    RoundingMode[RoundingMode["ROUND_HALF_DOWN"] = 5] = "ROUND_HALF_DOWN";
    RoundingMode[RoundingMode["ROUND_HALF_EVEN"] = 6] = "ROUND_HALF_EVEN";
    RoundingMode[RoundingMode["ROUND_HALF_CEIL"] = 7] = "ROUND_HALF_CEIL";
    RoundingMode[RoundingMode["ROUND_HALF_FLOOR"] = 8] = "ROUND_HALF_FLOOR";
    RoundingMode[RoundingMode["EUCLID"] = 9] = "EUCLID";
})(RoundingMode = exports.RoundingMode || (exports.RoundingMode = {}));
var NumberFormatTypes;
(function (NumberFormatTypes) {
    NumberFormatTypes["DECIMAL"] = "decimal";
    NumberFormatTypes["PERCENT"] = "percent";
    NumberFormatTypes["CURRENCIES"] = "currencies";
    NumberFormatTypes["PLURAL"] = "plural";
})(NumberFormatTypes = exports.NumberFormatTypes || (exports.NumberFormatTypes = {}));
